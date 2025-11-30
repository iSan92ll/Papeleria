// Configuración de la aplicación
const CONFIG = {
    // Backend API
    API_BASE_URL: 'http://localhost:8080/api',
    
    // EmailJS - REEMPLAZAR CON TUS CREDENCIALES
    EMAILJS: {
        SERVICE_ID: 'TU_SERVICE_ID',
        TEMPLATE_VERIFICATION: 'template_verificacion',
        TEMPLATE_PASSWORD_RESET: 'template_password_reset', 
        PUBLIC_KEY: 'TU_PUBLIC_KEY'
    },
    
    // Google Maps - REEMPLAZAR CON TU API KEY
    GOOGLE_MAPS_API_KEY: 'TU_GOOGLE_MAPS_API_KEY',
    
    // MercadoPago - REEMPLAZAR CON TUS CREDENCIALES
    MERCADOPAGO_PUBLIC_KEY: 'APP_USR-TU_PUBLIC_KEY',
    
    // Bounds de Bogotá
    BOGOTA_BOUNDS: {
        north: 4.850,
        south: 4.480, 
        west: -74.230,
        east: -73.980
    }
};

// Servicios de API
const API_ENDPOINTS = {
    AUTH: {
        LOGIN: '/auth/login',
        REGISTER: '/auth/register',
        VERIFY: '/auth/verify',
        RESEND_VERIFICATION: '/auth/resend-verification',
        FORGOT_PASSWORD: '/auth/forgot-password',
        RESET_PASSWORD: '/auth/reset-password'
    },
    PRODUCTS: '/products',
    CART: '/cart',
    ORDERS: '/orders',
    ADDRESSES: '/addresses',
    USERS: '/users'
};

// Estado global de la aplicación
let APP_STATE = {
    user: null,
    cart: null,
    currentPage: 1
};

// Inicializar EmailJS
if (typeof emailjs !== 'undefined') {
    emailjs.init(CONFIG.EMAILJS.PUBLIC_KEY);
}