// Funciones de administración

async function loadAdminDashboard() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user || user.rol !== 'ADMIN') {
        window.location.href = 'index.html';
        return;
    }

    await loadStats();
    await loadRecentOrders();
}

async function loadStats() {
    try {
        // Cargar estadísticas básicas
        const stats = await apiRequest('/admin/stats');
        
        document.getElementById('total-products').textContent = stats.totalProducts || '0';
        document.getElementById('total-orders').textContent = stats.totalOrders || '0';
        document.getElementById('total-users').textContent = stats.totalUsers || '0';

    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

async function loadRecentOrders() {
    try {
        const orders = await apiRequest(`${API_ENDPOINTS.ORDERS}?page=0&size=5`);
        
        const container = document.getElementById('recent-orders');
        if (container) {
            container.innerHTML = orders.content.map(order => `
                <div class="order-item">
                    <div class="order-info">
                        <strong>Orden #${order.idVenta}</strong>
                        <span>${formatPrice(order.total)}</span>
                    </div>
                    <div class="order-details">
                        <span>${order.usuario.nombre} ${order.usuario.apellido}</span>
                        <span class="status ${order.estado.toLowerCase()}">${order.estado}</span>
                    </div>
                </div>
            `).join('');
        }

    } catch (error) {
        console.error('Error loading orders:', error);
    }
}

async function loadProductsManagement() {
    try {
        const products = await apiRequest(API_ENDPOINTS.PRODUCTS);
        
        const container = document.getElementById('products-management');
        if (!container) return;

        container.innerHTML = `
            <div class="management-header">
                <h3>Gestión de Productos</h3>
                <button onclick="showProductForm()" class="btn btn-primary">Nuevo Producto</button>
            </div>
            <div class="products-list">
                ${products.content.map(product => `
                    <div class="product-management-item">
                        <div class="product-info">
                            <img src="${product.imagenUrl || 'assets/images/placeholder.jpg'}" alt="${product.nombre}">
                            <div>
                                <h4>${product.nombre}</h4>
                                <p>${formatPrice(product.precio)} - Stock: ${product.stock}</p>
                            </div>
                        </div>
                        <div class="product-actions">
                            <button onclick="editProduct(${product.idProducto})" class="btn btn-secondary">Editar</button>
                            <button onclick="toggleProduct(${product.idProducto}, ${!product.activo})" 
                                    class="btn ${product.activo ? 'btn-warning' : 'btn-success'}">
                                ${product.activo ? 'Desactivar' : 'Activar'}
                            </button>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;

    } catch (error) {
        showMessage('Error cargando productos: ' + error.message, 'error');
    }
}

function showProductForm(product = null) {
    const formHTML = `
        <div class="modal" id="product-modal">
            <div class="modal-content">
                <h3>${product ? 'Editar' : 'Nuevo'} Producto</h3>
                <form onsubmit="saveProduct(event, ${product ? product.idProducto : null})">
                    <div class="form-group">
                        <label for="product-name">Nombre:</label>
                        <input type="text" id="product-name" value="${product?.nombre || ''}" required>
                    </div>
                    <div class="form-group">
                        <label for="product-description">Descripción:</label>
                        <textarea id="product-description" required>${product?.descripcion || ''}</textarea>
                    </div>
                    <div class="form-group">
                        <label for="product-price">Precio:</label>
                        <input type="number" id="product-price" step="0.01" value="${product?.precio || ''}" required>
                    </div>
                    <div class="form-group">
                        <label for="product-stock">Stock:</label>
                        <input type="number" id="product-stock" value="${product?.stock || ''}" required>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Guardar</button>
                        <button type="button" onclick="closeModal()" class="btn btn-secondary">Cancelar</button>
                    </div>
                </form>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', formHTML);
}

async function saveProduct(event, productId = null) {
    event.preventDefault();
    
    const productData = {
        nombre: document.getElementById('product-name').value,
        descripcion: document.getElementById('product-description').value,
        precio: parseFloat(document.getElementById('product-price').value),
        stock: parseInt(document.getElementById('product-stock').value)
    };

    try {
        if (productId) {
            await apiRequest(`${API_ENDPOINTS.PRODUCTS}/${productId}`, {
                method: 'PUT',
                body: productData
            });
            showMessage('Producto actualizado', 'success');
        } else {
            await apiRequest(API_ENDPOINTS.PRODUCTS, {
                method: 'POST',
                body: productData
            });
            showMessage('Producto creado', 'success');
        }

        closeModal();
        await loadProductsManagement();

    } catch (error) {
        showMessage('Error guardando producto: ' + error.message, 'error');
    }
}

function closeModal() {
    const modal = document.getElementById('product-modal');
    if (modal) {
        modal.remove();
    }
}