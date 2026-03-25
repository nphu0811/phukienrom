/* ═══════════════════════════════════════════════════════
   PHUKIENROM - main.js
   Handles: JWT auth header injection, cart badge, toasts
   ═══════════════════════════════════════════════════════ */

// ── Toast Notification ────────────────────────────────────
const toastContainer = (() => {
  const el = document.createElement('div');
  el.className = 'toast-container';
  document.body.appendChild(el);
  return el;
})();

function showToast(message, type = 'success', duration = 3000) {
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.textContent = message;
  toastContainer.appendChild(toast);
  setTimeout(() => toast.remove(), duration);
}

// ── Auth State ────────────────────────────────────────────
function getToken()   { return localStorage.getItem('jwt_token'); }
function getUser()    {
  try { return JSON.parse(localStorage.getItem('user')); }
  catch { return null; }
}
function isLoggedIn() { return !!getToken(); }

function logout() {
  // Xóa cookie JWT và localStorage
  document.cookie = 'jwt=; path=/; max-age=0; SameSite=Lax';
  localStorage.removeItem('jwt_token');
  localStorage.removeItem('refresh_token');
  localStorage.removeItem('user');
  // Gọi đúng endpoint logout của Spring Security để invalidate session
  window.location.href = '/auth/logout';
}

// ── Inject JWT into all fetch calls via fetch wrapper ─────
const _originalFetch = window.fetch;
window.fetch = function(url, options = {}) {
  const token = getToken();
  if (token && typeof url === 'string' && url.startsWith('/api/')) {
    options.headers = {
      ...(options.headers || {}),
      'Authorization': 'Bearer ' + token
    };
  }
  return _originalFetch(url, options);
};


// ── Button loading helper ──────────────────────────────
function setLoading(btn, loading) {
  if (!btn) return;
  if (loading) {
    btn._originalText = btn.innerHTML;
    btn.classList.add('btn-loading');
    btn.disabled = true;
  } else {
    btn.classList.remove('btn-loading');
    btn.disabled = false;
    if (btn._originalText) btn.innerHTML = btn._originalText;
  }
}

// ── Cart Badge Update ─────────────────────────────────────
function updateCartBadge(count) {
  const badge = document.getElementById('cartCount');
  if (badge) {
    badge.textContent = count;
    badge.style.display = count > 0 ? 'flex' : 'none';
  }
}

async function refreshCartBadge() {
  if (!isLoggedIn()) return;
  try {
    const res = await fetch('/api/cart');
    if (res.ok) {
      const data = await res.json();
      updateCartBadge(data.data?.totalItems || 0);
    }
  } catch { /* silent */ }
}

// ── Page Load Tasks ────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  // Refresh cart badge on every page
  refreshCartBadge();

  // Sync user display with localStorage (optional)
  const user = getUser();
  const userDisplayEl = document.querySelector('.user-display-name');
  if (user && userDisplayEl) userDisplayEl.textContent = user.fullName;

  // Check URL for success param (post-order redirect)
  const params = new URLSearchParams(window.location.search);
  if (params.get('success') === 'true') {
    showToast('🎉 Đặt hàng thành công! Cảm ơn bạn đã mua hàng.', 'success', 5000);
  }
});

// ── Token Refresh Interceptor ─────────────────────────────
// If a 401 is received, try to refresh the token once
let isRefreshing = false;
const originalFetchForRefresh = window.fetch;
window.fetch = async function(url, options = {}) {
  const response = await originalFetchForRefresh(url, options);

  if (response.status === 401 && !url.includes('/api/auth/') && !isRefreshing) {
    const refreshToken = localStorage.getItem('refresh_token');
    if (refreshToken) {
      isRefreshing = true;
      try {
        const refreshRes = await originalFetchForRefresh('/api/auth/refresh', {
          method: 'POST',
          headers: { 'X-Refresh-Token': refreshToken }
        });
        if (refreshRes.ok) {
          const data = await refreshRes.json();
          localStorage.setItem('jwt_token', data.data.accessToken);
          // Retry original request with new token
          options.headers = {
            ...(options.headers || {}),
            'Authorization': 'Bearer ' + data.data.accessToken
          };
          isRefreshing = false;
          return originalFetchForRefresh(url, options);
        } else {
          // Refresh failed → logout
          logout();
        }
      } catch {
        logout();
      } finally {
        isRefreshing = false;
      }
    }
  }
  return response;
};

// ── Wishlist Toggle ────────────────────────────────────────
async function toggleWishlist(productId) {
  if (!isLoggedIn()) {
    window.location.href = '/auth/login?redirect=' + window.location.pathname;
    return;
  }
  if (!productId) {
    // Fallback: lấy từ button nếu không truyền tham số
    const btn = document.querySelector('.btn-wishlist');
    productId = btn ? btn.dataset.productId : null;
  }
  if (!productId || productId === 'undefined') {
    showToast('❌ Không xác định được sản phẩm', 'error');
    return;
  }
  try {
    const btn = document.querySelector('.btn-wishlist');
    if (btn) btn.disabled = true;

    const res  = await fetch('/api/wishlist/' + productId + '/toggle', { method: 'POST' });
    const data = await res.json();
    if (res.ok) {
      showToast(data.message || '✅ Đã cập nhật yêu thích', 'success');
      if (btn) {
        const inWishlist = data.data?.inWishlist;
        btn.innerHTML = inWishlist ? '♥ Yêu thích' : '♡ Yêu thích';
        btn.classList.toggle('active', !!inWishlist);
      }
    } else {
      showToast('❌ ' + (data.message || 'Lỗi'), 'error');
    }
  } catch {
    showToast('❌ Lỗi kết nối', 'error');
  } finally {
    const btn = document.querySelector('.btn-wishlist');
    if (btn) btn.disabled = false;
  }
}

// ── Quick Add to Cart (from list page) ───────────────────
async function quickAddToCart(btn) {
  if (!isLoggedIn()) {
    window.location.href = '/auth/login';
    return;
  }
  // If product has variants, go to detail page
  const slug = btn.dataset.slug;
  if (slug) {
    window.location.href = '/products/' + slug;
    return;
  }
  // Single-variant products
  const variantId = btn.dataset.variantId;
  if (variantId) {
    setLoading(btn, true);
    try {
      const res  = await fetch('/api/cart/items', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ variantId: parseInt(variantId), quantity: 1 })
      });
      const data = await res.json();
      if (res.ok) {
        showToast('✅ Đã thêm vào giỏ hàng!', 'success');
        updateCartBadge(data.data.totalItems);
      } else {
        showToast('❌ ' + data.message, 'error');
      }
    } catch {
      showToast('❌ Lỗi kết nối', 'error');
    } finally {
      setLoading(btn, false);
    }
  }
}

// ── Format currency ────────────────────────────────────────
function formatVND(amount) {
  return new Intl.NumberFormat('vi-VN').format(amount) + '₫';
}

// ── Admin: confirm delete ─────────────────────────────────
async function adminDelete(url, label = 'mục này') {
  if (!confirm(`Bạn có chắc muốn xóa ${label}?`)) return;
  const res = await fetch(url, { method: 'DELETE' });
  if (res.ok) {
    showToast('✅ Đã xóa thành công', 'success');
    setTimeout(() => location.reload(), 800);
  } else {
    showToast('❌ Xóa thất bại', 'error');
  }
}

// ── Admin: toggle product active ─────────────────────────
async function toggleProductActive(id) {
  const res = await fetch(`/api/admin/products/${id}/toggle-active`, { method: 'PATCH' });
  if (res.ok) {
    showToast('✅ Đã cập nhật', 'success');
    setTimeout(() => location.reload(), 600);
  }
}

// ── Admin/Staff: update order status ────────────────────────────
async function updateOrderStatus(orderId, status) {
  const res  = await fetch(`/api/admin/orders/${orderId}/status?status=${status}`, {
    method: 'PUT'
  });
  const data = await res.json();
  if (res.ok) {
    showToast('✅ ' + data.message, 'success');
    setTimeout(() => location.reload(), 800);
  } else {
    showToast('❌ ' + data.message, 'error');
  }
}

// ── Auto-bind order status buttons on order-detail pages ──────────────────
document.addEventListener('DOMContentLoaded', function() {
  const statusSelect = document.getElementById('orderStatusSelect');
  const updateBtn = statusSelect ? statusSelect.parentElement.querySelector('button.btn-primary') : null;
  if (statusSelect && updateBtn) {
    const orderId = statusSelect.dataset.orderId || updateBtn.dataset.orderId;
    if (orderId) {
      updateBtn.addEventListener('click', () => updateOrderStatus(orderId, statusSelect.value));
    }
  }
});
// �� Admin: user management ����������������������������
async function adminUpdateUserRole(userId) {
  const role = prompt('Nh?p vai tr� m?i (CUSTOMER / STAFF / ADMIN):', 'CUSTOMER');
  if (!role) return;
  const normalized = role.trim().toUpperCase();
  if (!['CUSTOMER', 'STAFF', 'ADMIN'].includes(normalized)) {
    showToast('? Vai tr� kh�ng h?p l?', 'error');
    return;
  }
  const res = await fetch(`/api/admin/users/${userId}/role?role=${normalized}`, { method: 'PATCH' });
  const data = await res.json();
  if (res.ok) {
    showToast('? Vai tr� d� c?p nh?t', 'success');
    setTimeout(() => location.reload(), 600);
  } else {
    showToast('? ' + (data.message || 'L?i'), 'error');
  }
}

async function adminToggleUserActive(userId, active) {
  const res = await fetch(`/api/admin/users/${userId}/deactivate?active=${active}`, { method: 'PATCH' });
  const data = await res.json();
  if (res.ok) {
    showToast('? ' + data.message, 'success');
    setTimeout(() => location.reload(), 600);
  } else {
    showToast('? ' + (data.message || 'L?i'), 'error');
  }
}

async function adminResetPassword(userId) {
  const pwd = prompt('Nh?p m?t kh?u m?i (>=6 k� t?):');
  if (!pwd || pwd.length < 6) {
    showToast('? M?t kh?u ph?i t? 6 k� t?', 'error');
    return;
  }
  const res = await fetch(`/api/admin/users/${userId}/password`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ newPassword: pwd })
  });
  const data = await res.json();
  if (res.ok) {
    showToast('? �� d?i m?t kh?u', 'success');
  } else {
    showToast('? ' + (data.message || 'L?i'), 'error');
  }
}

async function adminDeleteUser(userId) {
  if (!confirm('X�a ngu?i d�ng n�y?')) return;
  const res = await fetch(`/api/admin/users/${userId}`, { method: 'DELETE' });
  const data = await res.json();
  if (res.ok) {
    showToast('? �� x�a ngu?i d�ng', 'success');
    setTimeout(() => location.reload(), 600);
  } else {
    showToast('? ' + (data.message || 'L?i'), 'error');
  }
}
