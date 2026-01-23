import { fetchData } from '../utils/api.js';
import { navigateTo } from '../routes.js';

export async function render(container) {
    container.innerHTML = `
        <h1 class="page-title">Dashboard</h1>

        <div class="dashboard-actions">
            <a id="action-cad-cliente" href="#" class="action-card">
                <h2>Cadastro de Cliente</h2>
                <p>Adicionar um novo cliente ao sistema.</p>
            </a>
            <a id="action-cad-pet" href="#" class="action-card">
                <h2>Cadastro de Pet</h2>
                <p>Adicionar um novo pet para um cliente existente.</p>
            </a>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <h3>Total de Clientes</h3>
                <p id="stat-total-clientes">...</p>
            </div>
            <div class="stat-card">
                <h3>Total de Pets</h3>
                <p id="stat-total-pets">...</p>
            </div>
            <div class="stat-card">
                <h3>Agendamentos</h3>
                <p id="stat-agendamentos-hoje">...</p>
            </div>
        </div>

        <div class="table-container dashboard-table">
            <h2>Próximos Agendamentos</h2>
            <table class="content-table">
                <thead>
                <tr>
                    <th>Serviço</th>
                    <th>Data/Hora</th>
                    <th>Cliente</th>
                    <th>Pet</th>
                </tr>
                </thead>
                <tbody id="tabela-proximos-agendamentos">
                    <tr><td colspan="4">Carregando...</td></tr>
                </tbody>
            </table>
        </div>
    `;

    document.getElementById('action-cad-cliente').addEventListener('click', (e) => {
        e.preventDefault();
        navigateTo('/clientes');
    });
    document.getElementById('action-cad-pet').addEventListener('click', (e) => {
        e.preventDefault();
        navigateTo('/pets');
    });

    try {
        // Busca estatísticas
        const stats = await fetchData('/api/v1/dashboard/stats');
        // Ajustado para os nomes que o backend retorna
        document.getElementById('stat-total-clientes').textContent = stats.clientes || 0;
        document.getElementById('stat-total-pets').textContent = stats.pets || 0;
        document.getElementById('stat-agendamentos-hoje').textContent = stats.agendamentos || 0;

        // Busca agendamentos (usando a rota geral por enquanto, já que /proximos não existe no backend ainda)
        const agendamentos = await fetchData('/api/v1/agendamentos');
        const tabelaAgendamentos = document.getElementById('tabela-proximos-agendamentos');

        if (agendamentos && agendamentos.length > 0) {
            // Pega apenas os 5 primeiros para o dashboard
            const ultimosAgendamentos = agendamentos.slice(0, 5);
            
            tabelaAgendamentos.innerHTML = ultimosAgendamentos.map(ag => `
                <tr>
                    <td>${ag.servico || 'N/A'}</td>
                    <td>${ag.data || 'N/A'}</td>
                    <td>${ag.pet || 'N/A'}</td> <!-- O backend retorna o nome do pet direto no campo 'pet' -->
                    <td>${ag.pet || 'N/A'}</td>
                </tr>
            `).join('');
        } else {
            tabelaAgendamentos.innerHTML = '<tr><td colspan="4">Nenhum agendamento encontrado.</td></tr>';
        }

    } catch (error) {
        console.error("Erro ao carregar dados do dashboard:", error);
        // Não exibe erro na tela para não quebrar o layout, apenas loga
    }
}
