async function createBooking(data) { return API.post('/bookings', data); }
async function loadMyBookings()    { return API.get('/bookings/my'); }
async function getBooking(ref)     { return API.get(`/bookings/${ref}`); }
async function cancelBooking(ref)  { return API.put(`/bookings/${ref}/cancel`); }
async function getItinerary(ref)   { return API.get(`/bookings/${ref}/itinerary`); }

function statusChip(status) {
  const map = { CONFIRMED:'chip-confirmed', RESERVED:'chip-reserved', CANCELLED:'chip-cancelled' };
  return `<span class="status-chip ${map[status]||'chip-pending'}">${status}</span>`;
}

function payChip(status) {
  const map = { PAID:'chip-confirmed', PENDING:'chip-reserved', REFUNDED:'chip-int' };
  return `<span class="status-chip ${map[status]||'chip-pending'}">${status}</span>`;
}

function renderBookingRow(b) {
  const orig = (b.flight?.origin      || '').split('(')[0].trim();
  const dest = (b.flight?.destination || '').split('(')[0].trim();
  return `
  <tr style="cursor:pointer;" onclick="window.location='/booking-detail.html?ref=${b.bookingReference}'">
    <td><span class="data-mono" style="color:var(--primary);">${b.bookingReference}</span></td>
    <td>
      <span class="data-mono" style="font-size:13px;">${b.flight?.flightNumber || '—'}</span>
      <span style="margin-left:8px;font-size:12px;color:var(--on-surface-variant);">${orig} → ${dest}</span>
    </td>
    <td style="font-size:13px;">${formatDate(b.flight?.departureTime)}</td>
    <td>${statusChip(b.status)}</td>
    <td class="data-mono" style="font-size:13px;">$${(b.totalPrice||0).toFixed(2)}</td>
    <td>
      <a href="/booking-detail.html?ref=${b.bookingReference}" class="cloud-btn-outline" style="padding:5px 14px;font-size:12px;" onclick="event.stopPropagation()">View</a>
    </td>
  </tr>`;
}
