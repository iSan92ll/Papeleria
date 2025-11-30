// Funciones de autenticación

async function login(event) {
    if (event) event.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    if (!validateEmail(email)) {
        showMessage('Por favor ingresa un email válido', 'error');
        return;
    }

    try {
        const data = await apiRequest(API_ENDPOINTS.AUTH.LOGIN, {
            method: 'POST',
            body: { correo: email, contra: password }
        });

        localStorage.setItem('authToken', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
        
        showMessage('Inicio de sesión exitoso', 'success');
        
        // Redirigir según el rol
        setTimeout(() => {
            if (data.user.rol === 'ADMIN') {
                window.location.href = 'admin.html';
            } else {
                window.location.href = 'index.html';
            }
        }, 1000);

    } catch (error) {
        showMessage(error.message, 'error');
    }
}

async function register(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const userData = {
        nombre: formData.get('nombre'),
        apellido: formData.get('apellido'),
        correo: formData.get('correo'),
        contra: formData.get('contra'),
        telefono: formData.get('telefono')
    };

    // Validaciones
    if (!validateEmail(userData.correo)) {
        showMessage('Por favor ingresa un email válido', 'error');
        return;
    }

    if (userData.contra.length < 6) {
        showMessage('La contraseña debe tener al menos 6 caracteres', 'error');
        return;
    }

    try {
        const data = await apiRequest(API_ENDPOINTS.AUTH.REGISTER, {
            method: 'POST',
            body: userData
        });

        showMessage('Registro exitoso. Revisa tu correo para verificar tu cuenta.', 'success');
        
        // Redirigir a página de verificación
        setTimeout(() => {
            window.location.href = `verify.html?email=${encodeURIComponent(userData.correo)}`;
        }, 2000);

    } catch (error) {
        showMessage(error.message, 'error');
    }
}

async function verifyAccount() {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');

    if (!token) {
        showMessage('Token de verificación no proporcionado', 'error');
        return;
    }

    try {
        await apiRequest(API_ENDPOINTS.AUTH.VERIFY, {
            method: 'POST',
            body: { token }
        });

        showMessage('Cuenta verificada exitosamente', 'success');
        
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);

    } catch (error) {
        showMessage(error.message, 'error');
    }
}

async function resendVerification() {
    const urlParams = new URLSearchParams(window.location.search);
    const email = urlParams.get('email');

    if (!email) {
        showMessage('Email no proporcionado', 'error');
        return;
    }

    try {
        await apiRequest(API_ENDPOINTS.AUTH.RESEND_VERIFICATION, {
            method: 'POST',
            body: { email }
        });

        showMessage('Correo de verificación reenviado', 'success');
    } catch (error) {
        showMessage(error.message, 'error');
    }
}

async function forgotPassword(event) {
    event.preventDefault();
    
    const email = document.getElementById('email').value;

    if (!validateEmail(email)) {
        showMessage('Por favor ingresa un email válido', 'error');
        return;
    }

    try {
        await apiRequest(API_ENDPOINTS.AUTH.FORGOT_PASSWORD, {
            method: 'POST',
            body: { email }
        });

        showMessage('Se ha enviado un enlace de recuperación a tu correo', 'success');
    } catch (error) {
        showMessage(error.message, 'error');
    }
}

async function resetPassword(event) {
    event.preventDefault();
    
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword !== confirmPassword) {
        showMessage('Las contraseñas no coinciden', 'error');
        return;
    }

    if (newPassword.length < 6) {
        showMessage('La contraseña debe tener al menos 6 caracteres', 'error');
        return;
    }

    try {
        await apiRequest(API_ENDPOINTS.AUTH.RESET_PASSWORD, {
            method: 'POST',
            body: { token, newPassword }
        });

        showMessage('Contraseña restablecida exitosamente', 'success');
        
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);

    } catch (error) {
        showMessage(error.message, 'error');
    }
}

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    localStorage.removeItem('cart');
    showMessage('Sesión cerrada', 'success');
    setTimeout(() => window.location.href = 'index.html', 1000);
}

// Inicializar tel input
function initPhoneInput() {
    const phoneInput = document.getElementById('telefono');
    if (phoneInput && window.intlTelInput) {
        window.intlTelInput(phoneInput, {
            initialCountry: 'co',
            utilsScript: 'https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js'
        });
    }
}