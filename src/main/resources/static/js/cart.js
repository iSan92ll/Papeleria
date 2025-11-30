// Gestión del carrito

async function loadCart() {
    const container = document.getElementById('cart-container');
    if (!container) return;

    try {
        const cart = await apiRequest(API_ENDPOINTS.CART);
        localStorage.setItem('cart', JSON.stringify(cart));

        if (!cart.items || cart.items.length === 0) {
            container.innerHTML = `
                <div class="empty-cart">
                    <h2>Tu carrito está vacío</h2>
                    <p>Agrega algunos productos para continuar</p>
                    <a href="catalog.html" class="btn btn-primary">Ir al Catálogo</a>
                </div>
            `;
            return;
        }

        let total = 0;
        container.innerHTML = `
            <div class="cart-items">
                <h2>Tu Carrito</h2>
                ${cart.items.map(item => {
                    const subtotal = item.producto.precio * item.cantidad;
                    total += subtotal;
                    return `
                        <div class="cart-item">
                            <div class="item-image">
                                <img src="${item.producto.imagenUrl || 'assets/images/placeholder.jpg'}" alt="${item.producto.nombre}">
                            </div>
                            <div class="item-details">
                                <h4>${item.producto.nombre}</h4>
                                <p class="price">${formatPrice(item.producto.precio)} c/u</p>
                                <p class="stock ${item.producto.stock === 0 ? 'out-of-stock' : item.producto.stock < 10 ? 'low-stock' : 'in-stock'}">
                                    ${getStockMessage(item.producto.stock)}
                                </p>
                                ${item.cantidad > item.producto.stock ? 
                                    '<p class="stock-warning">Stock insuficiente</p>' : ''}
                            </div>
                            <div class="item-controls">
                                <div class="quantity-controls">
                                    <button onclick="updateQuantity(${item.idCarritoItem}, ${item.cantidad - 1})" 
                                            ${item.cantidad <= 1 ? 'disabled' : ''}>-</button>
                                    <span>${item.cantidad}</span>
                                    <button onclick="updateQuantity(${item.idCarritoItem}, ${item.cantidad + 1})"
                                            ${item.cantidad >= item.producto.stock ? 'disabled' : ''}>+</button>
                                </div>
                                <p class="subtotal">${formatPrice(subtotal)}</p>
                                <button onclick="removeFromCart(${item.idCarritoItem})" class="btn btn-danger">Eliminar</button>
                            </div>
                        </div>
                    `;
                }).join('')}
            </div>
            <div class="cart-summary">
                <h3>Resumen</h3>
                <div class="summary-line">
                    <span>Subtotal:</span>
                    <span>${formatPrice(total)}</span>
                </div>
                <div class="summary-line total">
                    <span>Total:</span>
                    <span>${formatPrice(total)}</span>
                </div>
                <button onclick="proceedToCheckout()" class="btn btn-primary btn-large">Proceder al Checkout</button>
            </div>
        `;

    } catch (error) {
        showMessage('Error cargando carrito: ' + error.message, 'error');
    }
}

async function updateQuantity(itemId, newQuantity) {
    if (newQuantity < 1) return;

    try {
        await apiRequest(`${API_ENDPOINTS.CART}/items/${itemId}`, {
            method: 'PUT',
            body: { quantity: newQuantity }
        });

        await loadCart();
        await updateCartCount();

    } catch (error) {
        showMessage('Error actualizando cantidad: ' + error.message, 'error');
    }
}

async function removeFromCart(itemId) {
    if (!confirm('¿Estás seguro de que quieres eliminar este producto del carrito?')) {
        return;
    }

    try {
        await apiRequest(`${API_ENDPOINTS.CART}/items/${itemId}`, {
            method: 'DELETE'
        });

        showMessage('Producto eliminado del carrito', 'success');
        await loadCart();
        await updateCartCount();

    } catch (error) {
        showMessage('Error eliminando producto: ' + error.message, 'error');
    }
}

function proceedToCheckout() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user.verificado) {
        showMessage('Debes verificar tu cuenta para realizar compras', 'error');
        return;
    }

    window.location.href = 'checkout.html';
}