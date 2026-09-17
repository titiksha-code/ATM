// State management
let authToken = null;
let currentSessionUser = null;
let activeFocusedInput = null;

// Track active input for virtual numpad
document.addEventListener('focusin', (e) => {
    if (e.target && (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA')) {
        activeFocusedInput = e.target;
    }
});

// ─── API Helper ───
async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };
    if (authToken) {
        headers['Authorization'] = `Bearer ${authToken}`;
    }

    const options = { method, headers };
    if (body) {
        options.body = JSON.stringify(body);
    }

    logHttp(method, endpoint, 'PENDING');

    try {
        const res = await fetch(endpoint, options);
        let data = null;
        const contentType = res.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            data = await res.json();
        } else if (contentType && contentType.includes('text/csv')) {
            const blob = await res.blob();
            logHttp(method, endpoint, `${res.status} OK (CSV Blob)`);
            return { ok: true, status: res.status, blob };
        }

        if (!res.ok) {
            const errorMsg = (data && data.message) ? data.message : `HTTP error ${res.status}`;
            logHttp(method, endpoint, `${res.status} ERROR`, true);
            return { ok: false, status: res.status, message: errorMsg, data };
        }

        logHttp(method, endpoint, `${res.status} OK`);
        return { ok: true, status: res.status, data: data ? data.data : null };
    } catch (err) {
        logHttp(method, endpoint, `NETWORK ERR: ${err.message}`, true);
        return { ok: false, status: 0, message: err.message };
    }
}

function logHttp(method, url, statusText, isError = false) {
    const list = document.getElementById('api-log-list');
    if (!list) return;

    const item = document.createElement('div');
    item.className = `log-item ${isError ? 'error' : (statusText.includes('PENDING') ? 'info' : 'success')}`;
    item.innerHTML = `
        <span class="log-method">${method}</span>
        <span class="log-url">${url}</span>
        <span class="log-status" style="margin-left:auto; font-weight:700;">${statusText}</span>
    `;
    list.prepend(item);
    if (list.children.length > 25) {
        list.removeChild(list.lastChild);
    }
}

// ─── Screen Navigation ───
function showScreen(screenId) {
    document.querySelectorAll('.screen-view').forEach(s => s.classList.remove('active'));
    const target = document.getElementById(screenId);
    if (target) {
        target.classList.add('active');
    }
    // Clear alerts on screen switch
    document.querySelectorAll('.alert-box').forEach(a => {
        a.style.display = 'none';
        a.textContent = '';
    });
}

// ─── Initial Load: Fetch Demo Cards ───
async function loadDemoCards() {
    const container = document.getElementById('demo-cards-container');
    const res = await apiCall('/api/v1/auth/demo-cards');

    if (!res.ok || !res.data) {
        container.innerHTML = `<div style="color:var(--danger); font-size:0.8rem;">Failed to load demo accounts.</div>`;
        return;
    }

    container.innerHTML = '';
    res.data.forEach(card => {
        const el = document.createElement('div');
        el.className = 'demo-card-item';
        el.innerHTML = `
            <div class="card-item-top">
                <span class="card-item-chip">CHIP & PIN</span>
                <span class="card-item-pin-hint">PIN: ${card.pinHint}</span>
            </div>
            <div class="card-item-num">${formatCardSpacing(card.cardNumber)}</div>
            <div class="card-item-bottom">
                <span class="card-item-name">${card.name}</span>
                <span class="card-item-balance">$${Number(card.balance).toLocaleString('en-US', {minimumFractionDigits:2})}</span>
            </div>
        `;

        el.addEventListener('click', () => {
            document.querySelectorAll('.demo-card-item').forEach(c => c.classList.remove('selected'));
            el.classList.add('selected');

            // Switch to login screen if not already there
            showScreen('screen-login');
            const cardInput = document.getElementById('input-card');
            const pinInput = document.getElementById('input-pin');

            cardInput.value = card.cardNumber;
            pinInput.value = card.pinHint !== '****' ? card.pinHint : '';
            pinInput.focus();

            // Simulate card insertion animation on card slot
            const cardSlot = document.getElementById('visual-card-slot');
            cardSlot.classList.add('glowing-slot');
            setTimeout(() => cardSlot.classList.remove('glowing-slot'), 1200);
        });

        container.appendChild(el);
    });
}

function formatCardSpacing(num) {
    if (!num) return '';
    return num.replace(/(\d{4})(?=\d)/g, '$1 ');
}

// ─── Authentication (Login & Logout) ───
document.getElementById('form-login').addEventListener('submit', async (e) => {
    e.preventDefault();
    const cardNumber = document.getElementById('input-card').value.trim();
    const pin = document.getElementById('input-pin').value.trim();
    const alertBox = document.getElementById('login-alert');

    alertBox.style.display = 'none';

    const res = await apiCall('/api/v1/auth/login', 'POST', { cardNumber, pin });

    if (!res.ok) {
        alertBox.className = 'alert-box danger';
        alertBox.textContent = res.message;
        alertBox.style.display = 'block';
        return;
    }

    authToken = res.data.token;
    currentSessionUser = res.data;

    // Refresh and update balance
    await refreshAccountOverview();

    // Show dashboard
    showScreen('screen-dashboard');
});

document.getElementById('btn-logout').addEventListener('click', async () => {
    if (authToken) {
        await apiCall('/api/v1/auth/logout', 'POST');
    }
    authToken = null;
    currentSessionUser = null;
    document.getElementById('input-pin').value = '';
    showScreen('screen-login');
    loadDemoCards(); // Refresh card balances
});

// ─── Refresh Account Overview ───
async function refreshAccountOverview() {
    const res = await apiCall('/api/v1/atm/balance');
    if (res.ok && res.data) {
        const acc = res.data;
        document.getElementById('dash-user-name').textContent = acc.fullName;
        document.getElementById('dash-card-masked').textContent = acc.maskedCardNumber;
        document.getElementById('dash-balance').textContent = `$${Number(acc.balance).toLocaleString('en-US', {minimumFractionDigits: 2})}`;
        document.getElementById('dash-acc-type').textContent = acc.accountType;
    }
}

// ─── Dashboard Menu Navigation ───
document.querySelectorAll('.menu-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const action = btn.getAttribute('data-action');
        if (action === 'balance') {
            refreshAccountOverview();
            printBalanceReceipt();
        } else if (action === 'withdraw') {
            showScreen('screen-withdraw');
            document.getElementById('input-withdraw-amount').value = '';
        } else if (action === 'deposit') {
            showScreen('screen-deposit');
            document.getElementById('input-deposit-amount').value = '';
        } else if (action === 'transfer') {
            showScreen('screen-transfer');
            document.getElementById('input-transfer-target').value = '';
            document.getElementById('input-transfer-amount').value = '';
            document.getElementById('recipient-verified-box').style.display = 'none';
        } else if (action === 'statement') {
            showScreen('screen-statement');
            loadMiniStatement();
        } else if (action === 'pinchange') {
            showScreen('screen-pinchange');
            document.getElementById('input-old-pin').value = '';
            document.getElementById('input-new-pin').value = '';
            document.getElementById('input-confirm-pin').value = '';
        }
    });
});

document.querySelectorAll('.btn-back').forEach(btn => {
    btn.addEventListener('click', () => {
        showScreen('screen-dashboard');
    });
});

// ─── Withdrawal ───
document.querySelectorAll('.preset-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.getElementById('input-withdraw-amount').value = btn.getAttribute('data-val');
    });
});

document.getElementById('form-withdraw').addEventListener('submit', async (e) => {
    e.preventDefault();
    const amount = parseFloat(document.getElementById('input-withdraw-amount').value);
    const alertBox = document.getElementById('withdraw-alert');

    const res = await apiCall('/api/v1/atm/withdraw', 'POST', { amount });

    if (!res.ok) {
        alertBox.className = 'alert-box danger';
        alertBox.textContent = res.message;
        alertBox.style.display = 'block';
        return;
    }

    alertBox.className = 'alert-box success';
    alertBox.textContent = `✔ ${res.data.message}`;
    alertBox.style.display = 'block';

    // Cash dispenser animation
    const cashSlot = document.getElementById('visual-cash-slot');
    cashSlot.style.borderColor = '#10b981';
    cashSlot.style.boxShadow = '0 0 16px rgba(16, 185, 129, 0.6)';
    setTimeout(() => {
        cashSlot.style.borderColor = '#1a2436';
        cashSlot.style.boxShadow = 'none';
    }, 2000);

    printReceipt({
        type: 'WITHDRAWAL',
        ref: res.data.txnRef,
        amount: res.data.amountWithdrawn,
        balanceAfter: res.data.newBalance,
        note: 'DISPENSED AT TERMINAL'
    });

    await refreshAccountOverview();
});

// ─── Deposit ───
document.querySelectorAll('.preset-btn-dep').forEach(btn => {
    btn.addEventListener('click', () => {
        document.getElementById('input-deposit-amount').value = btn.getAttribute('data-val');
    });
});

document.getElementById('form-deposit').addEventListener('submit', async (e) => {
    e.preventDefault();
    const amount = parseFloat(document.getElementById('input-deposit-amount').value);
    const alertBox = document.getElementById('deposit-alert');

    const res = await apiCall('/api/v1/atm/deposit', 'POST', { amount });

    if (!res.ok) {
        alertBox.className = 'alert-box danger';
        alertBox.textContent = res.message;
        alertBox.style.display = 'block';
        return;
    }

    alertBox.className = 'alert-box success';
    alertBox.textContent = `✔ ${res.data.message}`;
    alertBox.style.display = 'block';

    printReceipt({
        type: 'CASH DEPOSIT',
        ref: res.data.txnRef,
        amount: res.data.amountDeposited,
        balanceAfter: res.data.newBalance,
        note: 'ACCEPTED AT TERMINAL'
    });

    await refreshAccountOverview();
});

// ─── Fund Transfer ───
document.getElementById('btn-verify-target').addEventListener('click', async () => {
    const target = document.getElementById('input-transfer-target').value.trim();
    const verifiedBox = document.getElementById('recipient-verified-box');

    if (!target) {
        alert('Please enter a card number or account number first.');
        return;
    }

    const res = await apiCall(`/api/v1/atm/transfer/verify?target=${encodeURIComponent(target)}`);

    if (!res.ok || !res.data.valid) {
        verifiedBox.className = 'recipient-box danger';
        verifiedBox.textContent = res.data ? res.data.message : 'Recipient not found';
        verifiedBox.style.display = 'block';
        return;
    }

    verifiedBox.className = 'recipient-box';
    verifiedBox.textContent = `✔ Recipient: ${res.data.recipientName} (${res.data.recipientAccountNo})`;
    verifiedBox.style.display = 'block';
});

document.getElementById('form-transfer').addEventListener('submit', async (e) => {
    e.preventDefault();
    const recipientTarget = document.getElementById('input-transfer-target').value.trim();
    const amount = parseFloat(document.getElementById('input-transfer-amount').value);
    const alertBox = document.getElementById('transfer-alert');

    const res = await apiCall('/api/v1/atm/transfer', 'POST', { recipientTarget, amount });

    if (!res.ok) {
        alertBox.className = 'alert-box danger';
        alertBox.textContent = res.message;
        alertBox.style.display = 'block';
        return;
    }

    alertBox.className = 'alert-box success';
    alertBox.textContent = `✔ ${res.data.message}`;
    alertBox.style.display = 'block';

    printReceipt({
        type: 'FUND TRANSFER',
        ref: res.data.txnRef,
        amount: res.data.amount,
        balanceAfter: res.data.newBalance,
        note: `TO: ${res.data.recipientName} (${res.data.recipientAccountNo})`
    });

    await refreshAccountOverview();
});

// ─── Statement & CSV Export ───
async function loadMiniStatement() {
    const tbody = document.getElementById('statement-tbody');
    tbody.innerHTML = `<tr><td colspan="6" class="text-center">Fetching records...</td></tr>`;

    const res = await apiCall('/api/v1/transactions/mini-statement');
    if (!res.ok || !res.data || res.data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center">No recent transactions found.</td></tr>`;
        return;
    }

    tbody.innerHTML = '';
    res.data.forEach(t => {
        const tr = document.createElement('tr');
        const isPositive = t.txnType === 'DEPOSIT' || t.txnType === 'TRANSFER_IN';
        const formattedDate = new Date(t.txnDate).toLocaleString('en-GB', {
            day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
        });

        tr.innerHTML = `
            <td>${formattedDate}</td>
            <td><span class="badge ${isPositive ? 'text-success' : 'text-danger'}">${t.txnType}</span></td>
            <td>${t.txnRef}</td>
            <td>${t.description || '-'}</td>
            <td style="color:${isPositive ? '#10b981' : '#f43f5e'}">${isPositive ? '+' : '-'}$${Number(t.amount).toFixed(2)}</td>
            <td>$${Number(t.balanceAfter).toFixed(2)}</td>
        `;
        tbody.appendChild(tr);
    });
}

document.getElementById('btn-export-csv').addEventListener('click', async () => {
    const res = await apiCall('/api/v1/transactions/export');
    if (res.ok && res.blob) {
        const url = window.URL.createObjectURL(res.blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `GlobalBank-Statement-${Date.now()}.csv`;
        document.body.appendChild(a);
        a.click();
        a.remove();
    }
});

// ─── Change PIN ───
document.getElementById('form-pinchange').addEventListener('submit', async (e) => {
    e.preventDefault();
    const currentPin = document.getElementById('input-old-pin').value.trim();
    const newPin = document.getElementById('input-new-pin').value.trim();
    const confirmPin = document.getElementById('input-confirm-pin').value.trim();
    const alertBox = document.getElementById('pinchange-alert');

    const res = await apiCall('/api/v1/auth/change-pin', 'POST', { currentPin, newPin, confirmPin });

    if (!res.ok) {
        alertBox.className = 'alert-box danger';
        alertBox.textContent = res.message;
        alertBox.style.display = 'block';
        return;
    }

    alertBox.className = 'alert-box success';
    alertBox.textContent = '✔ PIN updated successfully! Please remember your new PIN.';
    alertBox.style.display = 'block';
});

// ─── Virtual Receipt Printer ───
function printReceipt({ type, ref, amount, balanceAfter, note }) {
    const content = document.getElementById('receipt-content');
    const now = new Date().toLocaleString('en-US', {
        dateStyle: 'medium', timeStyle: 'medium'
    });

    const maskedCard = currentSessionUser ? currentSessionUser.maskedCardNumber : '****';
    const accNo = currentSessionUser ? currentSessionUser.accountNo : 'N/A';

    content.innerHTML = `
        <div class="receipt-row"><span>DATE/TIME:</span><span>${now}</span></div>
        <div class="receipt-row"><span>TERMINAL:</span><span>GB-ATM-409</span></div>
        <div class="receipt-row"><span>CARD NUMBER:</span><span>${maskedCard}</span></div>
        <div class="receipt-row"><span>ACCOUNT NO:</span><span>${accNo}</span></div>
        <div class="receipt-row"><span>TXN TYPE:</span><strong>${type}</strong></div>
        <div class="receipt-row"><span>REF NO:</span><span>${ref}</span></div>
        ${note ? `<div class="receipt-row"><span>NOTE:</span><span>${note}</span></div>` : ''}
        <div class="receipt-dash">--------------------------------</div>
        <div class="receipt-row"><span>AMOUNT:</span><strong>$${Number(amount).toFixed(2)}</strong></div>
        <div class="receipt-row"><span>AVAIL BALANCE:</span><strong>$${Number(balanceAfter).toFixed(2)}</strong></div>
    `;

    // Visual receipt printer animation
    const receiptSlot = document.getElementById('visual-receipt-slot');
    receiptSlot.style.borderColor = '#10b981';
    setTimeout(() => { receiptSlot.style.borderColor = '#1a2436'; }, 1500);
}

function printBalanceReceipt() {
    if (!currentSessionUser) return;
    printReceipt({
        type: 'BALANCE ENQUIRY',
        ref: 'TXN' + Date.now(),
        amount: 0.00,
        balanceAfter: document.getElementById('dash-balance').textContent.replace('$', '').replace(/,/g, ''),
        note: 'INQUIRY COMPLETED'
    });
}

document.getElementById('btn-clear-receipt').addEventListener('click', () => {
    document.getElementById('receipt-content').innerHTML = `
        <div class="receipt-empty-state">No transaction performed yet.<br>Your printed receipt will appear here.</div>
    `;
});

document.getElementById('btn-clear-log').addEventListener('click', () => {
    document.getElementById('api-log-list').innerHTML = '';
});

// ─── Virtual Hardware Numpad ───
document.querySelectorAll('.num-key').forEach(keyBtn => {
    keyBtn.addEventListener('click', () => {
        const key = keyBtn.getAttribute('data-key');
        const activeInput = activeFocusedInput || document.activeElement;

        if (key === 'enter') {
            // Find active form and submit
            const form = activeInput ? activeInput.closest('form') : document.querySelector('.screen-view.active form');
            if (form) {
                form.requestSubmit();
            }
            return;
        }

        if (key === 'cancel') {
            const activeScreen = document.querySelector('.screen-view.active');
            if (activeScreen && activeScreen.id !== 'screen-login' && activeScreen.id !== 'screen-dashboard') {
                showScreen('screen-dashboard');
            } else if (activeInput && activeInput.tagName === 'INPUT') {
                activeInput.value = '';
            }
            return;
        }

        if (key === 'clear') {
            if (activeInput && activeInput.tagName === 'INPUT') {
                activeInput.value = '';
            }
            return;
        }

        if (key === 'backspace') {
            if (activeInput && activeInput.tagName === 'INPUT') {
                activeInput.value = activeInput.value.slice(0, -1);
            }
            return;
        }

        // Numeric entry
        if (activeInput && activeInput.tagName === 'INPUT') {
            if (activeInput.maxLength && activeInput.maxLength > 0 && activeInput.value.length >= activeInput.maxLength) {
                return;
            }
            activeInput.value += key;
        }
    });
});

// ─── Admin Modal & Card Unlock ───
const adminModal = document.getElementById('admin-modal');
document.getElementById('btn-admin-modal').addEventListener('click', async () => {
    adminModal.style.display = 'flex';
    loadAdminOverview();
});

document.getElementById('btn-close-admin').addEventListener('click', () => {
    adminModal.style.display = 'none';
});

async function loadAdminOverview() {
    const statsContainer = document.getElementById('admin-stats-content');
    statsContainer.innerHTML = 'Loading metrics...';

    const res = await apiCall('/api/v1/admin/overview');
    if (res.ok && res.data) {
        const s = res.data;
        statsContainer.innerHTML = `
            <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; font-size:0.85rem; margin-top:8px;">
                <div>Total Users: <strong>${s.totalUsers}</strong></div>
                <div>Total Transactions: <strong>${s.totalTransactions}</strong></div>
                <div>Vault Total Cash: <strong>$${Number(s.totalDepositsInVault).toLocaleString('en-US', {minimumFractionDigits:2})}</strong></div>
                <div>Active Sessions: <strong>${s.activeSessions}</strong></div>
                <div>Locked Cards: <strong style="color:var(--danger)">${s.lockedCards}</strong></div>
            </div>
        `;
    }
}

document.getElementById('btn-admin-unlock-submit').addEventListener('click', async () => {
    const card = document.getElementById('admin-unlock-card').value.trim();
    const alertBox = document.getElementById('admin-unlock-alert');

    if (!card) {
        alert('Please enter a card number to unlock.');
        return;
    }

    const res = await apiCall(`/api/v1/admin/cards/${encodeURIComponent(card)}/unlock`, 'POST');
    alertBox.style.display = 'block';
    if (!res.ok) {
        alertBox.className = 'alert-box danger';
        alertBox.textContent = res.message;
    } else {
        alertBox.className = 'alert-box success';
        alertBox.textContent = res.data ? res.data.message : 'Card unlocked successfully!';
        loadDemoCards();
        loadAdminOverview();
    }
});

// Start up
window.addEventListener('DOMContentLoaded', () => {
    loadDemoCards();
});
