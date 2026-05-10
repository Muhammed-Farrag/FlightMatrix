const API = {
  base: '/api',

  async get(path) {
    const res = await fetch(this.base + path, { credentials: 'include' });
    if (res.status === 401) { window.location.href = '/index.html'; return null; }
    return res.json();
  },

  async post(path, body) {
    const res = await fetch(this.base + path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(body)
    });
    return res.json();
  },

  async put(path, body) {
    const res = await fetch(this.base + path, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: body !== undefined ? JSON.stringify(body) : undefined
    });
    return res.json();
  },

  async del(path) {
    const res = await fetch(this.base + path, { method: 'DELETE', credentials: 'include' });
    return res.json();
  }
};

// Shared utilities
function formatTime(iso) {
  if (!iso) return '--:--';
  return new Date(iso).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false });
}

function formatDate(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleDateString('en-US', { day: '2-digit', month: 'short', year: 'numeric' });
}

function formatDateTime(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleString('en-US', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit', hour12: false });
}

function getParam(key) {
  return new URLSearchParams(window.location.search).get(key);
}
