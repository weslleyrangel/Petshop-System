// Importa os módulos de cada "página" da nova pasta
import * as Dashboard from './pages/dashboard.js';
import * as Clientes from './pages/clientes.js';
import * as Pets from './pages/pets.js';
import * as Agendamentos from './pages/agendamentos.js';
import * as Produtos from './pages/produtos.js';
import * as Vendas from './pages/vendas.js';

// Mapeia as rotas (o hash da URL) para o módulo JS correspondente
const routes = {
    '/': Dashboard,
    '/clientes': Clientes,
    '/pets': Pets,
    '/agendamentos': Agendamentos,
    '/produtos': Produtos,
    '/vendas': Vendas
};

/**
 * Navega para um novo caminho, atualizando o hash da URL.
 * @param {string} path - O caminho para navegar (ex: '/clientes').
 */
export const navigateTo = (path) => {
    window.location.hash = path;
};

// Função principal do roteador
const handleNavigation = () => {
    // Pega o hash da URL (ex: #/clientes) ou define a raiz ('#/') como padrão
    let path = window.location.hash.replace('#', '');
    if (path === '') {
        path = '/';
    }

    // Encontra o módulo JS correspondente ao caminho
    const pageModule = routes[path];

    // Encontra o container principal onde o conteúdo será injetado
    const mainContent = document.getElementById('app-content');

    if (pageModule && typeof pageModule.render === 'function') {
        // Se o módulo for encontrado, chama a função 'render' dele
        // A função 'render' é responsável por gerar o HTML da página
        pageModule.render(mainContent);
        updateActiveLink(path);
    } else {
        // Página não encontrada
        mainContent.innerHTML = `<h1 class="page-title">Erro 404</h1><p>Página não encontrada.</p>`;
    }
};

// Função para atualizar o link ativo no menu lateral
const updateActiveLink = (path) => {
    const navLinks = document.querySelectorAll('.sidebar .nav-link');

    // Mapeia o caminho para o href (ex: '/' -> '#/')
    const targetHref = `#${path}`;

    navLinks.forEach(link => {
        const linkHref = link.getAttribute('href');

        if (linkHref === targetHref) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
};

// --- EVENT LISTENERS ---

// 1. Ouve o evento 'hashchange' (quando o usuário clica em um link <a href="#/...">)
window.addEventListener('hashchange', handleNavigation);

// 2. Ouve o 'DOMContentLoaded' (quando a página é carregada pela primeira vez)
window.addEventListener('DOMContentLoaded', handleNavigation);

// 3. Opcional: Lidar com o botão de logout para limpar (ex: tokens)
document.getElementById('logout-button')?.addEventListener('click', (e) => {
    // Aqui você pode limpar o localStorage ou sessionStorage se estiver usando tokens
    // ex: localStorage.removeItem('authToken');
    console.log('Usuário deslogado.');
});

window.addEventListener('DOMContentLoaded', () => {
    const menuToggle = document.getElementById('mobile-menu-toggle');
    const menuOverlay = document.getElementById('mobile-menu-overlay');
    const sidebar = document.querySelector('.sidebar');
    const navLinks = document.querySelectorAll('.sidebar .nav-link');

    // Função para fechar o menu
    const closeMenu = () => {
        sidebar.classList.remove('active');
        menuOverlay.classList.remove('active');
    };

    // Função para abrir o menu
    const openMenu = () => {
        sidebar.classList.add('active');
        menuOverlay.classList.add('active');
    };

    // 1. Ouve o clique no botão hambúrguer
    menuToggle?.addEventListener('click', (e) => {
        e.stopPropagation(); // Impede que o clique se propague
        if (sidebar.classList.contains('active')) {
            closeMenu();
        } else {
            openMenu();
        }
    });

    // 2. Ouve o clique no overlay (para fechar)
    menuOverlay?.addEventListener('click', closeMenu);

    // 3. Ouve o clique em QUALQUER link do menu
    // (para fechar o menu após a navegação)
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            // Só fecha se o menu estiver no modo "mobile" (ativo)
            if (sidebar.classList.contains('active')) {
                closeMenu();
            }
        });
    });
});
