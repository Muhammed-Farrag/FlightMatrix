async function processPayment(data) { return API.post('/payments/process', data); }
async function getPaymentStatus(ref) { return API.get(`/payments/${ref}`); }

function renderETicket(b) {
  const dep = new Date(b.flight?.departureTime || Date.now());
  const origCode = (b.flight?.origin?.match(/\(([^)]+)\)/) || [])[1] || '???';
  const destCode = (b.flight?.destination?.match(/\(([^)]+)\)/) || [])[1] || '???';
  const origCity = (b.flight?.origin || '').split('(')[0].trim();
  const destCity = (b.flight?.destination || '').split('(')[0].trim();
  const paxName  = b.passengers?.[0]?.name || 'Passenger';
  const seatCls  = b.seatClass || '—';

  return `
  <div class="boarding-pass">
    <div class="boarding-pass__main">
      <div style="margin-bottom:18px;">
        <div style="font-size:11px;font-weight:700;color:var(--on-surface-variant);text-transform:uppercase;letter-spacing:.1em;margin-bottom:4px;">Boarding Pass</div>
        <div style="font-size:24px;font-weight:700;font-family:'Plus Jakarta Sans',sans-serif;">${paxName}</div>
        <div style="font-size:13px;color:var(--on-surface-variant);margin-top:2px;">${b.flight?.airline || ''}</div>
      </div>

      <div style="display:flex;align-items:center;gap:0;margin-bottom:20px;">
        <div>
          <div style="font-size:38px;font-weight:800;font-family:'Plus Jakarta Sans',sans-serif;line-height:1;">${origCode}</div>
          <div style="font-size:12px;color:var(--on-surface-variant);margin-top:2px;">${origCity}</div>
        </div>
        <div style="flex:1;margin:0 20px;position:relative;border-top:2px dashed var(--outline-variant);">
          <i data-lucide="plane" style="position:absolute;top:-11px;left:50%;transform:translateX(-50%);width:20px;height:20px;color:var(--primary);"></i>
        </div>
        <div style="text-align:right;">
          <div style="font-size:38px;font-weight:800;font-family:'Plus Jakarta Sans',sans-serif;line-height:1;">${destCode}</div>
          <div style="font-size:12px;color:var(--on-surface-variant);margin-top:2px;">${destCity}</div>
        </div>
      </div>

      <hr class="flight-path-divider">

      <div style="display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin-top:18px;">
        <div>
          <div style="font-size:10px;color:var(--on-surface-variant);text-transform:uppercase;letter-spacing:.06em;margin-bottom:4px;">Flight</div>
          <div class="data-mono" style="font-weight:600;">${b.flight?.flightNumber || '—'}</div>
        </div>
        <div>
          <div style="font-size:10px;color:var(--on-surface-variant);text-transform:uppercase;letter-spacing:.06em;margin-bottom:4px;">Date</div>
          <div class="data-mono">${dep.toLocaleDateString('en-US',{day:'2-digit',month:'short',year:'numeric'})}</div>
        </div>
        <div>
          <div style="font-size:10px;color:var(--on-surface-variant);text-transform:uppercase;letter-spacing:.06em;margin-bottom:4px;">Departure</div>
          <div class="data-mono">${dep.toLocaleTimeString('en-US',{hour:'2-digit',minute:'2-digit',hour12:false})}</div>
        </div>
        <div>
          <div style="font-size:10px;color:var(--on-surface-variant);text-transform:uppercase;letter-spacing:.06em;margin-bottom:4px;">Class</div>
          <div class="data-mono">${seatCls}</div>
        </div>
      </div>
    </div>

    <div class="boarding-pass__stub">
      <div>
        <div style="font-size:10px;color:var(--on-surface-variant);text-transform:uppercase;letter-spacing:.06em;margin-bottom:6px;">PNR</div>
        <div class="data-mono" style="font-size:16px;font-weight:700;color:var(--primary);letter-spacing:.1em;">${b.bookingReference}</div>
      </div>
      <div>
        <div style="font-size:10px;color:var(--on-surface-variant);text-transform:uppercase;letter-spacing:.06em;margin-bottom:6px;">Passengers</div>
        <div class="data-mono" style="font-size:20px;font-weight:700;">${b.passengers?.length || 1}</div>
      </div>
      <div>
        <div style="font-size:10px;color:var(--on-surface-variant);text-transform:uppercase;letter-spacing:.06em;margin-bottom:6px;">Gate</div>
        <div class="data-mono" style="font-size:20px;font-weight:700;">TBD</div>
      </div>
      <div style="margin-top:auto;">
        <span class="status-chip chip-confirmed">Confirmed</span>
      </div>
    </div>
  </div>`;
}
