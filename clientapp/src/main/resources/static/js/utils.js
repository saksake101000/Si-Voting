/**
 * SI-VOTING - Utility Functions
 * Common helper functions used across the application
 */

// ========== Date & Time Utilities ==========

/**
 * Format date to readable string
 * @param {Date|string} date - Date object or ISO string
 * @param {string} format - Format type: 'short', 'long', 'time'
 * @returns {string} Formatted date string
 */
function formatDate(date, format = 'short') {
    const d = new Date(date);
    
    if (isNaN(d.getTime())) {
        return 'Invalid Date';
    }
    
    const options = {
        short: { year: 'numeric', month: 'short', day: 'numeric' },
        long: { year: 'numeric', month: 'long', day: 'numeric' },
        time: { hour: '2-digit', minute: '2-digit' },
        datetime: { 
            year: 'numeric', 
            month: 'short', 
            day: 'numeric',
            hour: '2-digit', 
            minute: '2-digit' 
        }
    };
    
    return d.toLocaleDateString('id-ID', options[format] || options.short);
}

/**
 * Get relative time (e.g., "2 hours ago")
 */
function getRelativeTime(date) {
    const now = new Date();
    const past = new Date(date);
    const diffMs = now - past;
    const diffSec = Math.floor(diffMs / 1000);
    const diffMin = Math.floor(diffSec / 60);
    const diffHour = Math.floor(diffMin / 60);
    const diffDay = Math.floor(diffHour / 24);
    
    if (diffSec < 60) return 'Baru saja';
    if (diffMin < 60) return `${diffMin} menit yang lalu`;
    if (diffHour < 24) return `${diffHour} jam yang lalu`;
    if (diffDay < 7) return `${diffDay} hari yang lalu`;
    
    return formatDate(date, 'short');
}

/**
 * Check if event is currently active
 */
function isEventActive(startDate, endDate) {
    const now = new Date();
    const start = new Date(startDate);
    const end = new Date(endDate);
    
    return now >= start && now <= end;
}

/**
 * Get event status
 */
function getEventStatus(startDate, endDate) {
    const now = new Date();
    const start = new Date(startDate);
    const end = new Date(endDate);
    
    if (now < start) return 'upcoming';
    if (now >= start && now <= end) return 'active';
    return 'ended';
}

// ========== String Utilities ==========

/**
 * Truncate text to specified length
 */
function truncate(text, maxLength = 100, suffix = '...') {
    if (!text || text.length <= maxLength) return text;
    return text.substring(0, maxLength).trim() + suffix;
}

/**
 * Capitalize first letter
 */
function capitalize(text) {
    if (!text) return '';
    return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
}

/**
 * Generate random string
 */
function randomString(length = 8) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

// ========== Number Utilities ==========

/**
 * Format number with thousand separator
 */
function formatNumber(num) {
    return new Intl.NumberFormat('id-ID').format(num);
}

/**
 * Calculate percentage
 */
function percentage(value, total, decimals = 1) {
    if (total === 0) return 0;
    return ((value / total) * 100).toFixed(decimals);
}

// ========== Validation Utilities ==========

/**
 * Validate email format
 */
function isValidEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

/**
 * Validate password strength
 */
function isStrongPassword(password) {
    // At least 8 characters, 1 uppercase, 1 lowercase, 1 number
    const re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/;
    return re.test(password);
}

/**
 * Validate phone number (Indonesian format)
 */
function isValidPhone(phone) {
    const re = /^(\+62|62|0)[0-9]{9,12}$/;
    return re.test(phone.replace(/[\s-]/g, ''));
}

// ========== DOM Utilities ==========

/**
 * Show element
 */
function show(element) {
    if (typeof element === 'string') {
        element = document.querySelector(element);
    }
    if (element) {
        element.classList.remove('hidden');
        element.classList.add('visible');
    }
}

/**
 * Hide element
 */
function hide(element) {
    if (typeof element === 'string') {
        element = document.querySelector(element);
    }
    if (element) {
        element.classList.add('hidden');
        element.classList.remove('visible');
    }
}

/**
 * Toggle element visibility
 */
function toggle(element) {
    if (typeof element === 'string') {
        element = document.querySelector(element);
    }
    if (element) {
        element.classList.toggle('hidden');
    }
}

/**
 * Add loading spinner to button
 */
function addLoadingToButton(button, text = 'Loading...') {
    if (typeof button === 'string') {
        button = document.querySelector(button);
    }
    if (button) {
        button.disabled = true;
        button.dataset.originalText = button.innerHTML;
        button.innerHTML = `
            <span class="spinner spinner-sm"></span>
            <span>${text}</span>
        `;
    }
}

/**
 * Remove loading from button
 */
function removeLoadingFromButton(button) {
    if (typeof button === 'string') {
        button = document.querySelector(button);
    }
    if (button && button.dataset.originalText) {
        button.disabled = false;
        button.innerHTML = button.dataset.originalText;
    }
}

// ========== Notification Utilities ==========

/**
 * Show toast notification
 */
function showToast(message, type = 'info', duration = 3000) {
    const icons = {
        success: 'fa-check-circle',
        error: 'fa-times-circle',
        warning: 'fa-exclamation-triangle',
        info: 'fa-info-circle'
    };

    const colors = {
        success: 'var(--success)',
        error: 'var(--danger)',
        warning: 'var(--warning)',
        info: 'var(--info)'
    };

    const toast = document.createElement('div');
    toast.className = 'fixed top-4 right-4 z-50 glass rounded-2xl p-4 shadow-2xl flex items-center gap-3 min-w-[300px] animate-slide-in';
    toast.innerHTML = `
        <div class="w-10 h-10 rounded-full flex items-center justify-center" style="background: ${colors[type]}20; color: ${colors[type]}">
            <i class="fa-solid ${icons[type]}"></i>
        </div>
        <div class="flex-1">
            <p class="font-semibold text-sm" style="color: ${colors[type]}">${capitalize(type)}</p>
            <p class="text-xs text-slate-600">${message}</p>
        </div>
        <button class="text-slate-400 hover:text-slate-600 transition" onclick="this.parentElement.remove()">
            <i class="fa-solid fa-xmark"></i>
        </button>
    `;

    document.body.appendChild(toast);

    // Auto-remove
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

/**
 * Show confirmation dialog
 */
function showConfirm(message, onConfirm, onCancel) {
    const modal = document.createElement('div');
    modal.className = 'fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4';
    modal.innerHTML = `
        <div class="glass rounded-3xl p-8 w-full max-w-md shadow-2xl">
            <div class="text-center mb-6">
                <div class="w-20 h-20 mx-auto mb-4 rounded-full bg-warning/10 flex items-center justify-center text-3xl text-warning">
                    <i class="fa-solid fa-exclamation-triangle"></i>
                </div>
                <h3 class="text-2xl font-bold text-primary mb-2">Konfirmasi</h3>
                <p class="text-slate-600">${message}</p>
            </div>
            <div class="flex gap-3">
                <button id="btnCancel" class="flex-1 btn btn-outline">Batal</button>
                <button id="btnConfirm" class="flex-1 btn btn-primary">Konfirmasi</button>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    modal.querySelector('#btnConfirm').addEventListener('click', () => {
        modal.remove();
        if (onConfirm) onConfirm();
    });

    modal.querySelector('#btnCancel').addEventListener('click', () => {
        modal.remove();
        if (onCancel) onCancel();
    });

    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.remove();
            if (onCancel) onCancel();
        }
    });
}

// ========== Storage Utilities ==========

/**
 * Set item in localStorage with expiry
 */
function setStorage(key, value, expiryDays = null) {
    const item = {
        value: value,
        expiry: expiryDays ? Date.now() + (expiryDays * 24 * 60 * 60 * 1000) : null
    };
    localStorage.setItem(key, JSON.stringify(item));
}

/**
 * Get item from localStorage (check expiry)
 */
function getStorage(key) {
    const itemStr = localStorage.getItem(key);
    if (!itemStr) return null;

    try {
        const item = JSON.parse(itemStr);
        if (item.expiry && Date.now() > item.expiry) {
            localStorage.removeItem(key);
            return null;
        }
        return item.value;
    } catch {
        return itemStr;
    }
}

/**
 * Remove item from localStorage
 */
function removeStorage(key) {
    localStorage.removeItem(key);
}

// ========== URL Utilities ==========

/**
 * Get query parameter from URL
 */
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

/**
 * Set query parameter in URL
 */
function setQueryParam(param, value) {
    const url = new URL(window.location);
    url.searchParams.set(param, value);
    window.history.pushState({}, '', url);
}

/**
 * Remove query parameter from URL
 */
function removeQueryParam(param) {
    const url = new URL(window.location);
    url.searchParams.delete(param);
    window.history.pushState({}, '', url);
}

// ========== Copy to Clipboard ==========

/**
 * Copy text to clipboard
 */
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        showToast('Berhasil disalin ke clipboard!', 'success');
        return true;
    } catch (error) {
        console.error('Failed to copy:', error);
        showToast('Gagal menyalin ke clipboard', 'error');
        return false;
    }
}

// ========== Debounce ==========

/**
 * Debounce function execution
 */
function debounce(func, wait = 300) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Export utilities to global scope
window.utils = {
    formatDate,
    getRelativeTime,
    isEventActive,
    getEventStatus,
    truncate,
    capitalize,
    randomString,
    formatNumber,
    percentage,
    isValidEmail,
    isStrongPassword,
    isValidPhone,
    show,
    hide,
    toggle,
    addLoadingToButton,
    removeLoadingFromButton,
    showToast,
    showConfirm,
    setStorage,
    getStorage,
    removeStorage,
    getQueryParam,
    setQueryParam,
    removeQueryParam,
    copyToClipboard,
    debounce
};
