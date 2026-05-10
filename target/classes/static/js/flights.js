async function searchFlights(params) {
  const q = new URLSearchParams(params).toString();
  return API.get(`/flights/search?${q}`);
}

async function getAllFlights() {
  return API.get('/flights/all');
}

function renderFlightCard(f, clickable = true) {
  const eco   = ((f.calculatedEconomyPrice   ?? f.economyPrice)    || 0).toFixed(2);
  const biz   = ((f.calculatedBusinessPrice  ?? f.businessPrice)   || 0).toFixed(2);
  const first = ((f.calculatedFirstClassPrice ?? f.firstClassPrice) || 0).toFixed(2);
  const depTime = formatTime(f.departureTime);
  const arrTime = formatTime(f.arrivalTime);
  const originCode = (f.origin.match(/\(([^)]+)\)/) || [])[1] || f.origin;
  const destCode   = (f.destination.match(/\(([^)]+)\)/) || [])[1] || f.destination;
  const originCity = f.origin.split('(')[0].trim();
  const destCity   = f.destination.split('(')[0].trim();
  const chipClass  = f.flightType === 'DOMESTIC' ? 'chip-dom' : 'chip-int';
  const clickAttrs = clickable ? ` style="cursor:pointer;" onclick="selectFlight(${f.id})"` : '';
  const bookBtn    = clickable
    ? `<button class="cloud-btn cloud-btn-sm" onclick="event.stopPropagation();selectFlight(${f.id})">Book <i data-lucide="arrow-right" style="width:14px;height:14px;"></i></button>`
    : '';

  return `
  <div class="flight-card"${clickAttrs}>
    <div style="display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:14px;">
      <div>
        <span class="data-mono" style="color:var(--primary);font-size:15px;font-weight:600;">${f.flightNumber}</span>
        <span style="margin-left:10px;color:var(--on-surface-variant);font-size:13px;">${f.airline}</span>
      </div>
      <span class="status-chip ${chipClass}">${f.flightType}</span>
    </div>

    <div style="display:flex;align-items:center;gap:0;margin-bottom:14px;">
      <div>
        <div style="font-size:26px;font-weight:800;font-family:'Plus Jakarta Sans',sans-serif;line-height:1.1;">${originCode}</div>
        <div style="font-size:12px;color:var(--on-surface-variant);">${originCity}</div>
        <div class="data-mono" style="font-size:13px;color:var(--primary);margin-top:2px;">${depTime}</div>
      </div>
      <div style="flex:1;margin:0 16px;position:relative;border-top:1px dashed var(--outline-variant);top:4px;">
        <i data-lucide="plane" style="position:absolute;top:-9px;left:50%;transform:translateX(-50%);width:16px;height:16px;color:var(--outline-variant);"></i>
      </div>
      <div style="text-align:right;">
        <div style="font-size:26px;font-weight:800;font-family:'Plus Jakarta Sans',sans-serif;line-height:1.1;">${destCode}</div>
        <div style="font-size:12px;color:var(--on-surface-variant);">${destCity}</div>
        <div class="data-mono" style="font-size:13px;color:var(--primary);margin-top:2px;">${arrTime}</div>
      </div>
    </div>

    <hr class="flight-path-divider">

    <div style="display:flex;gap:20px;align-items:center;flex-wrap:wrap;">
      <div>
        <div style="font-size:10px;color:var(--on-surface-variant);text-transform:uppercase;letter-spacing:.06em;margin-bottom:3px;">Economy</div>
        <div class="data-mono" style="font-size:14px;font-weight:600;">$${eco}</div>
      </div>
      <div>
        <div style="font-size:10px;color:var(--on-surface-variant);text-transform:uppercase;letter-spacing:.06em;margin-bottom:3px;">Business</div>
        <div class="data-mono" style="font-size:14px;font-weight:600;">$${biz}</div>
      </div>
      <div>
        <div style="font-size:10px;color:var(--tertiary);text-transform:uppercase;letter-spacing:.06em;margin-bottom:3px;">First Class</div>
        <span class="badge-gold">$${first}</span>
      </div>
      <div style="margin-left:auto;display:flex;align-items:center;gap:12px;">
        <span style="font-size:12px;color:var(--on-surface-variant);">${f.availableSeats} seats left</span>
        ${bookBtn}
      </div>
    </div>
  </div>`;
}

function selectFlight(id) {
  window.location.href = `/booking-create.html?flightId=${id}`;
}

function bindSearchForm() {
  const form = document.getElementById('search-form');
  if (!form) return;
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    await runSearch();
  });
}

async function runSearch() {
  const origin      = document.getElementById('origin')?.value || '';
  const destination = document.getElementById('destination')?.value || '';
  const date        = document.getElementById('date')?.value || '';
  const passengers  = document.getElementById('passengers')?.value || '1';
  const results     = document.getElementById('flight-results');
  if (!results) return;

  results.innerHTML = '<div style="text-align:center;padding:40px;"><span class="spinner"></span></div>';

  const params = { origin, destination, passengerCount: passengers };
  if (date) params.date = date;

  const res = await searchFlights(params);
  if (!res || !res.success) {
    results.innerHTML = '<div class="empty-state"><h3>Error</h3><p>Could not fetch flights.</p></div>';
    return;
  }
  if (!res.data || res.data.length === 0) {
    results.innerHTML = '<div class="empty-state"><h3>No flights found</h3><p>Try a different route or date.</p></div>';
    return;
  }
  results.innerHTML = res.data.map(f => renderFlightCard(f)).join('');
  if (typeof lucide !== 'undefined') lucide.createIcons();
}
