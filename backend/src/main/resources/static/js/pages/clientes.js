import { fetchData, postData, deleteData } from '../utils/api.js';

let listaClientes = [];

const Clientes = {
    async render() {
        const userRole = localStorage.getItem('role'); // Pega a role do usuário

        return `
            <div class="table-container">
                <div class="table-header">
                    <h2 class="table-title">Lista de Clientes</h2>
                    <button id="btn-novo-cliente" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Novo Cliente
                    </button>
                </div>
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>CPF</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody id="tabela-clientes">
                            <tr><td colspan="5" style="text-align: center;">Carregando...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Modal de Cadastro/Edição -->
            <div id="modal-cliente" class="modal-overlay">
                <div class="modal-container">
                    <div class="modal-header">
                        <h3 class="modal-title" id="modal-title">Novo Cliente</h3>
                        <button class="modal-close" id="modal-close">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                    <form id="form-cliente">
                        <div class="modal-content">
                            <input type="hidden" id="cliente-id" name="id">
                            
                            <div class="form-group-grid">
                                <label for="nome" class="form-label">Nome Completo</label>
                                <input type="text" id="nome" name="nome" class="form-input" required>
                            </div>

                            <div class="form-row" style="margin-top: 16px;">
                                <div class="form-group-grid">
                                    <label for="email" class="form-label">E-mail</label>
                                    <input type="email" id="email" name="email" class="form-input" required>
                                </div>
                                <div class="form-group-grid">
                                    <label for="cpf" class="form-label">CPF</label>
                                    <input type="text" id="cpf" name="cpf" class="form-input" required>
                                </div>
                            </div>

                            <div class="form-group-grid" style="margin-top: 16px;">
                                <label for="endereco" class="form-label">Endereço</label>
                                <input type="text" id="endereco" name="endereco" class="form-input">
                            </div>

                            <div class="form-group-grid" style="margin-top: 16px;">
                                <label for="sexo" class="form-label">Sexo</label>
                                <select id="sexo" name="sexo" class="form-input">
                                    <option value="M">Masculino</option>
                                    <option value="F">Feminino</option>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" id="btn-cancelar">Cancelar</button>
                            <button type="submit" class="btn btn-primary">Salvar</button>
                        </div>
                    </form>
                </div>
            </div>
        `;
    },

    async afterRender() {
        const userRole = localStorage.getItem('role');
        const modal = document.getElementById('modal-cliente');
        const form = document.getElementById('form-cliente');
        
        // Funções do Modal
        const openModal = (cliente = null) => {
            const title = document.getElementById('modal-title');
            form.reset();
            
            if (cliente) {
                title.textContent = 'Editar Cliente';
                document.getElementById('cliente-id').value = cliente.id;
                document.getElementById('nome').value = cliente.nome;
                document.getElementById('email').value = cliente.email;
                document.getElementById('cpf').value = cliente.cpf;
                document.getElementById('endereco').value = cliente.endereco || '';
                document.getElementById('sexo').value = cliente.sexo || 'M';
            } else {
                title.textContent = 'Novo Cliente';
                document.getElementById('cliente-id').value = '';
            }
            
            modal.classList.add('open');
        };

        const closeModal = () => {
            modal.classList.remove('open');
        };

        document.getElementById('btn-novo-cliente').addEventListener('click', () => openModal());
        document.getElementById('modal-close').addEventListener('click', closeModal);
        document.getElementById('btn-cancelar').addEventListener('click', closeModal);

        // Carregar Clientes
        const loadClientes = async () => {
            try {
                listaClientes = await fetchData('/api/v1/clientes');
                const tabela = document.getElementById('tabela-clientes');

                if (listaClientes && listaClientes.length > 0) {
                    tabela.innerHTML = listaClientes.map(cliente => `
                        <tr>
                            <td>#${cliente.id}</td>
                            <td>
                                <div style="font-weight: 500;">${cliente.nome}</div>
                            </td>
                            <td>${cliente.email}</td>
                            <td>${cliente.cpf}</td>
                            <td>
                                <button class="action-btn btn-edit" data-id="${cliente.id}" title="Editar">
                                    <i class="fas fa-edit"></i>
                                </button>
                                ${userRole === 'ADMIN' ? `
                                <button class="action-btn delete btn-delete" data-id="${cliente.id}" title="Excluir">
                                    <i class="fas fa-trash-alt"></i>
                                </button>` : ''}
                            </td>
                        </tr>
                    `).join('');

                    // Listeners de Ação
                    document.querySelectorAll('.btn-edit').forEach(btn => {
                        btn.addEventListener('click', () => {
                            const id = btn.getAttribute('data-id');
                            const cliente = listaClientes.find(c => c.id == id);
                            openModal(cliente);
                        });
                    });

                    if (userRole === 'ADMIN') {
                        document.querySelectorAll('.btn-delete').forEach(btn => {
                            btn.addEventListener('click', async () => {
                                const id = btn.getAttribute('data-id');
                                if (confirm('Tem certeza que deseja excluir este cliente?')) {
                                    try {
                                        await deleteData(`/api/v1/clientes?id=${id}`);
                                        loadClientes();
                                    } catch (error) {
                                        alert('Erro ao excluir cliente: ' + error.message);
                                    }
                                }
                            });
                        });
                    }

                } else {
                    tabela.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 32px;">Nenhum cliente cadastrado.</td></tr>';
                }
            } catch (error) {
                document.getElementById('tabela-clientes').innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--danger-color);">Erro ao carregar clientes.</td></tr>`;
            }
        };

        // Salvar Cliente
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const formData = new FormData(form);
            const clienteData = Object.fromEntries(formData.entries());
            const id = clienteData.id;
            
            // Remove ID vazio para não enviar string vazia no JSON se for novo
            if (!id) delete clienteData.id;

            const method = id ? 'PUT' : 'POST';
            const endpoint = id ? `/api/v1/clientes/${id}` : '/api/v1/clientes';

            try {
                await postData(endpoint, clienteData, method);
                closeModal();
                loadClientes();
            } catch (error) {
                alert(`Erro ao salvar: ${error.message}`);
            }
        });

        // Inicializa
        await loadClientes();
    }
};

export default Clientes;
