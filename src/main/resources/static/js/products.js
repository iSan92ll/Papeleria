// Gestión de productos

let currentProducts = [];
let currentFilters = {
    category: '',
    minPrice: '',
    maxPrice: '',
    search: '',
    sort: 'nombre_asc'
};

async function loadProducts(page = 0) {
    const container = document.getElementById('products-container');
    if (!container) return;

    try {
        // Construir query parameters
        const params = new URLSearchParams({
            page: page,
            size: 12,
            ...currentFilters
        });

        const products = await apiRequest(`${API_ENDPOINTS.PRODUCTS}?${params}`);
        currentProducts = products.content;

        container.innerHTML = products.content.map(product => `
            <div class="product-card">
                <div class="product-image">
                    <img src="${product.imagenUrl || 'assets/images/placeholder.jpg'}" alt="${product.nombre}">
                    ${product.stock === 0 ? '<div class="stock-badge out-of-stock">Sin stock</div>' : ''}
                    ${product.stock > 0 && product.stock < 10 ? `<div class="stock-badge low-stock">Quedan ${product.stock}</div>` : ''}
                </div>
                <div class="product-info">
                    <h4>${product.nombre}</h4>
                    <p class="description">${product.descripcion || 'Sin descripción'}</p>
                    <p class="price">${formatPrice(product.precio)}</p>
                    <div class="product-actions">
                        <a href="product-detail.html?id=${product.idProducto}" class="btn btn-outline">Ver Detalle</a>
                        <button onclick="addToCart(${product.idProducto})" 
                                class="btn btn-primary" 
                                ${product.stock === 0 ? 'disabled' : ''}>
                            Añadir al Carrito
                        </button>
                    </div>
                </div>
            </div>
        `).join('');

        // Actualizar paginación
        updatePagination(products.totalPages, page);

    } catch (error) {
        showMessage('Error cargando productos: ' + error.message, 'error');
    }
}

function updatePagination(totalPages, currentPage) {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;

    let paginationHTML = '';

    if (currentPage > 0) {
        paginationHTML += `<button onclick="loadProducts(${currentPage - 1})" class="btn btn-outline">Anterior</button>`;
    }

    for (let i = 0; i < totalPages; i++) {
        paginationHTML += `
            <button onclick="loadProducts(${i})" 
                    class="btn ${i === currentPage ? 'btn-primary' : 'btn-outline'}">
                ${i + 1}
            </button>
        `;
    }

    if (currentPage < totalPages - 1) {
        paginationHTML += `<button onclick="loadProducts(${currentPage + 1})" class="btn btn-outline">Siguiente</button>`;
    }

    pagination.innerHTML = paginationHTML;
}

function applyFilters() {
    const search = document.getElementById('search').value;
    const category = document.getElementById('category').value;
    const minPrice = document.getElementById('minPrice').value;
    const maxPrice = document.getElementById('maxPrice').value;
    const sort = document.getElementById('sort').value;

    currentFilters = { search, category, minPrice, maxPrice, sort };
    loadProducts(0);
}

async function loadProductDetail() {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    if (!productId) {
        showMessage('Producto no encontrado', 'error');
        return;
    }

    try {
        const product = await apiRequest(`${API_ENDPOINTS.PRODUCTS}/${productId}`);
        
        const container = document.getElementById('product-detail');
        if (!container) return;

        container.innerHTML = `
            <div class="product-detail-container">
                <div class="product-images">
                    <img src="${product.imagenUrl || 'assets/images/placeholder.jpg'}" alt="${product.nombre}">
                </div>
                <div class="product-info">
                    <h1>${product.nombre}</h1>
                    <p class="description">${product.descripcion || 'Sin descripción'}</p>
                    <p class="price">${formatPrice(product.precio)}</p>
                    <p class="stock ${product.stock === 0 ? 'out-of-stock' : product.stock < 10 ? 'low-stock' : 'in-stock'}">
                        ${getStockMessage(product.stock)}
                    </p>
                    
                    <div class="quantity-selector">
                        <label for="quantity">Cantidad:</label>
                        <div class="quantity-controls">
                            <button type="button" onclick="decreaseQuantity()">-</button>
                            <input type="number" id="quantity" value="1" min="1" max="${product.stock}">
                            <button type="button" onclick="increaseQuantity(${product.stock})">+</button>
                        </div>
                    </div>

                    <div class="product-actions">
                        <button onclick="addToCartFromDetail(${product.idProducto})" 
                                class="btn btn-primary btn-large" 
                                ${product.stock === 0 ? 'disabled' : ''}>
                            Añadir al Carrito
                        </button>
                    </div>
                </div>
            </div>
        `;

    } catch (error) {
        showMessage('Error cargando producto: ' + error.message, 'error');
    }
}

function increaseQuantity(maxStock) {
    const quantityInput = document.getElementById('quantity');
    let quantity = parseInt(quantityInput.value);
    if (quantity < maxStock) {
        quantityInput.value = quantity + 1;
    }
}

function decreaseQuantity() {
    const quantityInput = document.getElementById('quantity');
    let quantity = parseInt(quantityInput.value);
    if (quantity > 1) {
        quantityInput.value = quantity - 1;
    }
}

async function addToCartFromDetail(productId) {
    const quantity = parseInt(document.getElementById('quantity').value);
    await addToCart(productId, quantity);
}

async function addToCart(productId, quantity = 1) {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) {
        showMessage('Debes iniciar sesión para añadir productos al carrito', 'error');
        window.location.href = 'login.html';
        return;
    }

    if (!user.verificado) {
        showMessage('Debes verificar tu cuenta para realizar compras', 'error');
        return;
    }

    try {
        await apiRequest(API_ENDPOINTS.CART, {
            method: 'POST',
            body: { productId, quantity }
        });

        showMessage('Producto añadido al carrito', 'success');
        await updateCartCount();

    } catch (error) {
        showMessage('Error añadiendo al carrito: ' + error.message, 'error');
    }
}

async function updateCartCount() {
    try {
        const cart = await apiRequest(API_ENDPOINTS.CART);
        localStorage.setItem('cart', JSON.stringify(cart));
        
        const cartCount = cart.items.reduce((total, item) => total + item.cantidad, 0);
        const cartCountElements = document.querySelectorAll('.cart-count');
        cartCountElements.forEach(el => {
            el.textContent = cartCount;
        });
    } catch (error) {
        console.error('Error updating cart count:', error);
    }
}