// Proceso de checkout

let selectedAddress = null;
let addresses = [];

async function loadCheckout() {
    await loadAddresses();
    await loadOrderSummary();
}

async function loadAddresses() {
    try {
        addresses = await apiRequest(API_ENDPOINTS.ADDRESSES);
        selectedAddress = addresses.find(addr => addr.esPredeterminada) || addresses[0];
        
        renderAddresses();
        initMap();

    } catch (error) {
        console.error('Error loading addresses:', error);
    }
}

function renderAddresses() {
    const container = document.getElementById('addresses-container');
    if (!container) return;

    container.innerHTML = `
        <h3>Dirección de Entrega</h3>
        <div class="addresses-list">
            ${addresses.map(address => `
                <div class="address-option ${address.idDireccion === selectedAddress?.idDireccion ? 'selected' : ''}" 
                     onclick="selectAddress(${address.idDireccion})">
                    <div class="address-text">${address.direccionTexto}</div>
                    <div class="address-city">${address.ciudad}</div>
                    ${address.esPredeterminada ? '<span class="default-badge">Predeterminada</span>' : ''}
                </div>
            `).join('')}
            
            <button onclick="showAddressForm()" class="btn btn-outline">Agregar Nueva Dirección</button>
        </div>
        
        <div id="address-form" class="address-form" style="display: none;">
            <h4>Nueva Dirección</h4>
            <form onsubmit="saveAddress(event)">
                <div class="form-group">
                    <label for="address-input">Dirección:</label>
                    <input type="text" id="address-input" required>
                </div>
                <div id="map" style="height: 300px; margin: 10px 0;"></div>
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Guardar</button>
                    <button type="button" onclick="hideAddressForm()" class="btn btn-secondary">Cancelar</button>
                </div>
            </form>
        </div>
    `;
}

function selectAddress(addressId) {
    selectedAddress = addresses.find(addr => addr.idDireccion === addressId);
    renderAddresses();
}

function showAddressForm() {
    document.getElementById('address-form').style.display = 'block';
}

function hideAddressForm() {
    document.getElementById('address-form').style.display = 'none';
}

async function saveAddress(event) {
    event.preventDefault();
    
    const addressInput = document.getElementById('address-input');
    const addressText = addressInput.value.trim();

    if (!addressText) {
        showMessage('Por favor ingresa una dirección', 'error');
        return;
    }

    try {
        // Aquí integrarías Google Maps para geocodificación
        const newAddress = {
            direccionTexto: addressText,
            ciudad: 'Bogotá',
            latitud: 4.6097, // Ejemplo
            longitud: -74.0817, // Ejemplo
            esPredeterminada: addresses.length === 0
        };

        const savedAddress = await apiRequest(API_ENDPOINTS.ADDRESSES, {
            method: 'POST',
            body: newAddress
        });

        addresses.push(savedAddress);
        selectedAddress = savedAddress;
        
        hideAddressForm();
        renderAddresses();
        showMessage('Dirección guardada exitosamente', 'success');

    } catch (error) {
        showMessage('Error guardando dirección: ' + error.message, 'error');
    }
}

async function loadOrderSummary() {
    const container = document.getElementById('order-summary');
    if (!container) return;

    try {
        const cart = await apiRequest(API_ENDPOINTS.CART);
        let total = 0;

        container.innerHTML = `
            <h3>Resumen del Pedido</h3>
            <div class="order-items">
                ${cart.items.map(item => {
                    const subtotal = item.producto.precio * item.cantidad;
                    total += subtotal;
                    return `
                        <div class="order-item">
                            <span>${item.producto.nombre} x ${item.cantidad}</span>
                            <span>${formatPrice(subtotal)}</span>
                        </div>
                    `;
                }).join('')}
            </div>
            <div class="order-total">
                <strong>Total: ${formatPrice(total)}</strong>
            </div>
        `;

    } catch (error) {
        showMessage('Error cargando resumen: ' + error.message, 'error');
    }
}

async function processPayment(method) {
    if (!selectedAddress) {
        showMessage('Por favor selecciona una dirección de entrega', 'error');
        return;
    }

    try {
        const orderData = {
            direccionId: selectedAddress.idDireccion,
            metodoPago: method
        };

        const result = await apiRequest(API_ENDPOINTS.ORDERS, {
            method: 'POST',
            body: orderData
        });

        if (method === 'mercadopago') {
            // Integración con MercadoPago
            await processMercadoPagoPayment(result);
        } else {
            // Pago en efectivo
            showMessage('Pedido realizado exitosamente. Te contactaremos para coordinar el pago.', 'success');
            setTimeout(() => {
                window.location.href = `order-confirmation.html?id=${result.idVenta}`;
            }, 2000);
        }

    } catch (error) {
        showMessage('Error procesando pedido: ' + error.message, 'error');
    }
}

async function processMercadoPagoPayment(order) {
    // Integración básica con MercadoPago
    const mp = new MercadoPago(CONFIG.MERCADOPAGO_PUBLIC_KEY);
    
    try {
        const preference = await apiRequest(`${API_ENDPOINTS.ORDERS}/${order.idVenta}/preference`, {
            method: 'POST'
        });

        mp.checkout({
            preference: {
                id: preference.id
            },
            autoOpen: true
        });

    } catch (error) {
        showMessage('Error iniciando pago: ' + error.message, 'error');
    }
}

function initMap() {
    // Inicialización básica del mapa - necesitarías la API key de Google Maps
    if (typeof google !== 'undefined') {
        const map = new google.maps.Map(document.getElementById('map'), {
            center: { lat: 4.6097, lng: -74.0817 },
            zoom: 12
        });
    }
}