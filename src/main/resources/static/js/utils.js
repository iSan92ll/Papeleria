// Utilidades generales

// Manejo de API
async function apiRequest(endpoint, options = {}) {
    const url = `${CONFIG.API_BASE_URL}${endpoint}`;
    const token = localStorage.getItem('authToken');
    
    const config = {
        headers: {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` })
        },
        ...options
    };

    if (options.body) {
        config.body = JSON.stringify(options.body);
    }

    try {
        const response = await fetch(url, config);
        
        if (response.status === 401) {
            handleUnauthorized();
            throw new Error('Sesión expirada');
        }

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || 'Error en la petición');
        }

        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

function handleUnauthorized() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}

// Formatear precios
function formatPrice(price) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP'
    }).format(price);
}

// Mostrar mensajes
function showMessage(message, type = 'info') {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message message-${type}`;
    messageDiv.textContent = message;
    
    // Estilos básicos para mensajes
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 5px;
        color: white;
        z-index: 1000;
        max-width: 300px;
        ${type === 'error' ? 'background: #dc3545;' : ''}
        ${type === 'success' ? 'background: #28a745;' : ''}
        ${type === 'info' ? 'background: #17a2b8;' : ''}
    `;
    
    document.body.appendChild(messageDiv);
    
    setTimeout(() => {
        messageDiv.remove();
    }, 5000);
}

// Cargar navegación
async function loadNavigation() {
    const navElement = document.getElementById('main-nav');
    if (!navElement) return;

    const user = JSON.parse(localStorage.getItem('user') || 'null');
    const cart = JSON.parse(localStorage.getItem('cart') || 'null');
    const cartItemCount = cart ? cart.items.reduce((total, item) => total + item.cantidad, 0) : 0;

    let navHTML = '';

    if (user) {
        navHTML = `
            <a href="catalog.html">Catálogo</a>
            <a href="cart.html" class="cart-link">
                Carrito <span class="cart-count">${cartItemCount}</span>
            </a>
            <a href="profile.html">Mi Cuenta</a>
            ${user.rol === 'ADMIN' ? '<a href="admin.html">Administración</a>' : ''}
            <button onclick="logout()" class="btn btn-outline">Cerrar Sesión</button>
        `;
    } else {
        navHTML = `
            <a href="catalog.html">Catálogo</a>
            <a href="login.html" class="btn btn-outline">Iniciar Sesión</a>
            <a href="register.html" class="btn btn-primary">Registrarse</a>
        `;
    }

    navElement.innerHTML = navHTML;
}

// Validaciones
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

function validatePhone(phone) {
    // Validación básica de teléfono colombiano
    const re = /^(\+57|57)?[1-9]\d{8}$/;
    return re.test(phone.replace(/\s/g, ''));
}

// Cargar productos destacados
async function loadFeaturedProducts() {
    const container = document.getElementById('featured-products');
    if (!container) return;

    try {
        const products = await apiRequest(`${API_ENDPOINTS.PRODUCTS}?page=0&size=4`);
        container.innerHTML = products.content.map(product => `
            <div class="product-card">
                <div class="product-image">
                    <img src="${product.imagenUrl || 'assets/images/placeholder.jpg'}" alt="${product.nombre}">
                </div>
                <div class="product-info">
                    <h4>${product.nombre}</h4>
                    <p class="price">${formatPrice(product.precio)}</p>
                    <p class="stock ${product.stock === 0 ? 'out-of-stock' : product.stock < 10 ? 'low-stock' : 'in-stock'}">
                        ${getStockMessage(product.stock)}
                    </p>
                    <a href="product-detail.html?id=${product.idProducto}" class="btn btn-primary">Ver Detalle</a>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error loading featured products:', error);
    }
}

function getStockMessage(stock) {
    if (stock === 0) return 'Sin stock';
    if (stock < 10) return `Quedan ${stock} unidades`;
    return 'Disponible';
}