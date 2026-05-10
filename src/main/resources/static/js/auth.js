let _currentUser = null;

async function getCurrentUser() {
  if (_currentUser) return _currentUser;
  const res = await fetch('/api/auth/me', { credentials: 'include' });
  if (!res.ok) return null;
  const json = await res.json();
  if (json && json.success) { _currentUser = json.data; return _currentUser; }
  return null;
}

async function requireAuth(allowedRoles) {
  const user = await getCurrentUser();
  if (!user) { window.location.href = '/index.html'; return null; }
  if (allowedRoles && !allowedRoles.includes(user.role)) { window.location.href = '/index.html'; return null; }
  return user;
}

async function login(username, password) {
  return API.post('/auth/login', { username, password });
}

async function logout() {
  await API.post('/auth/logout', {});
  _currentUser = null;
  window.location.href = '/index.html';
}

function redirectByRole(role) {
  const map = { CUSTOMER: '/dashboard-customer.html', AGENT: '/dashboard-agent.html', ADMIN: '/dashboard-admin.html' };
  window.location.href = map[role] || '/index.html';
}

async function initNavbar(activePage) {
  const user = await getCurrentUser();
  if (!user) { window.location.href = '/index.html'; return; }

  const el = document.getElementById('navbar');
  if (!el) return;

  const roleLabel = { CUSTOMER: 'Customer', AGENT: 'Agent', ADMIN: 'Admin' }[user.role] || user.role;
  const dashUrl   = { CUSTOMER: '/dashboard-customer.html', AGENT: '/dashboard-agent.html', ADMIN: '/dashboard-admin.html' }[user.role] || '/';

  const links = {
    CUSTOMER: [
      { href: '/dashboard-customer.html', icon: 'layout-dashboard', label: 'Dashboard',      page: 'dashboard' },
      { href: '/flights-search.html',     icon: 'search',           label: 'Search Flights',  page: 'flights'   },
      { href: '/booking-list.html',       icon: 'ticket',           label: 'My Bookings',     page: 'bookings'  },
    ],
    AGENT: [
      { href: '/dashboard-agent.html',    icon: 'layout-dashboard', label: 'Dashboard',       page: 'dashboard' },
      { href: '/flights-search.html',     icon: 'plane',            label: 'Flights',          page: 'flights'   },
      { href: '/booking-list.html',       icon: 'ticket',           label: 'Bookings',         page: 'bookings'  },
    ],
    ADMIN: [
      { href: '/dashboard-admin.html',    icon: 'layout-dashboard', label: 'Dashboard',       page: 'dashboard' },
      { href: '/flights-search.html',     icon: 'plane',            label: 'Flights',          page: 'flights'   },
      { href: '/booking-list.html',       icon: 'ticket',           label: 'Bookings',         page: 'bookings'  },
      { href: '/admin-users.html',        icon: 'users',            label: 'Users',            page: 'users'     },
    ],
  }[user.role] || [];

  const navHtml = links.map(l => `
    <a href="${l.href}" class="navbar__link ${l.page === activePage ? 'active' : ''}">
      <i data-lucide="${l.icon}" style="width:15px;height:15px;"></i>${l.label}
    </a>`).join('');

  el.innerHTML = `
    <a href="${dashUrl}" class="navbar__brand">
      <span class="navbar__logo"><img src="/assets/logo.svg" alt="FlightMatrix logo"></span>
      FlightMatrix
    </a>
    <nav class="navbar__nav">${navHtml}</nav>
    <div class="navbar__right">
      <span style="font-size:13px;color:var(--on-surface-variant);">${user.name}</span>
      <span class="role-chip">${roleLabel}</span>
      <button class="navbar__logout" onclick="logout()">
        <i data-lucide="log-out" style="width:14px;height:14px;"></i>Logout
      </button>
    </div>`;

  if (typeof lucide !== 'undefined') lucide.createIcons();
}

function showToast(msg, type = 'error') {
  let c = document.getElementById('toast-container');
  if (!c) { c = document.createElement('div'); c.id = 'toast-container'; c.className = 'toast-container'; document.body.appendChild(c); }
  const t = document.createElement('div');
  t.className = `toast toast--${type}`;
  t.textContent = msg;
  c.appendChild(t);
  setTimeout(() => t.remove(), 4000);
}

function setLoading(btn, loading, label) {
  btn.disabled = loading;
  btn.innerHTML = loading ? '<span class="spinner"></span> ' + label : label;
}
