document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const showRegister = document.getElementById('showRegister');
    const showLogin = document.getElementById('showLogin');
    const loginCard = document.querySelector('.login-card');
    const registerCard = document.getElementById('registerCard');

    if (localStorage.getItem('token')) {
        const role = localStorage.getItem('role');
        if (role === 'ADMIN') window.location.href = '/admin/dashboard.html';
        else if (role === 'LIVREUR') window.location.href = '/livreur/livraison.html';
        else window.location.href = '/client/commander.html';
    }

    if (showRegister) {
        showRegister.addEventListener('click', (e) => {
            e.preventDefault();
            loginCard.style.display = 'none';
            registerCard.style.display = 'block';
        });
    }

    if (showLogin) {
        showLogin.addEventListener('click', (e) => {
            e.preventDefault();
            registerCard.style.display = 'none';
            loginCard.style.display = 'block';
        });
    }

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const errorEl = document.getElementById('loginError');
            errorEl.style.display = 'none';

            try {
                const res = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        email: document.getElementById('email').value,
                        password: document.getElementById('password').value
                    })
                });

                const data = await res.json();
                if (res.ok) {
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('userId', data.userId);
                    localStorage.setItem('email', data.email);
                    localStorage.setItem('role', data.role);

                    if (data.role === 'ADMIN') window.location.href = '/admin/dashboard.html';
                    else if (data.role === 'LIVREUR') window.location.href = '/livreur/livraison.html';
                    else window.location.href = '/client/commander.html';
                } else {
                    errorEl.textContent = data.error || 'Email ou mot de passe incorrect';
                    errorEl.style.display = 'block';
                }
            } catch (err) {
                errorEl.textContent = 'Erreur de connexion au serveur';
                errorEl.style.display = 'block';
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const errorEl = document.getElementById('registerError');
            errorEl.style.display = 'none';

            try {
                const res = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        email: document.getElementById('regEmail').value,
                        password: document.getElementById('regPassword').value,
                        telephone: document.getElementById('regTelephone').value,
                        role: document.getElementById('regRole').value
                    })
                });

                const data = await res.json();
                if (res.ok) {
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('userId', data.userId);
                    localStorage.setItem('role', data.role);

                    if (data.role === 'LIVREUR') window.location.href = '/livreur/livraison.html';
                    else window.location.href = '/client/commander.html';
                } else {
                    errorEl.textContent = data.error || "Erreur lors de l'inscription";
                    errorEl.style.display = 'block';
                }
            } catch (err) {
                errorEl.textContent = 'Erreur de connexion au serveur';
                errorEl.style.display = 'block';
            }
        });
    }
});
