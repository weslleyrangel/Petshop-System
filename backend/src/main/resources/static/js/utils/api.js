/**
 * Este arquivo centraliza a lógica de comunicação com seu back-end (API REST).
 * Isso evita repetir código 'fetch' em todas as páginas.
 */

// URL base da sua API (ex: http://localhost:8080)
const API_BASE_URL = window.location.origin;

/**
 * Função genérica para buscar dados (GET)
 * @param {string} endpoint - O caminho da API (ex: "/api/v1/clientes")
 * @returns {Promise<any>} - Os dados em JSON
 */
export async function fetchData(endpoint) {
    try {
        const token = localStorage.getItem('token');
        const headers = {};
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            headers: headers
        });

        if (!response.ok) {
            if (response.status === 403 || response.status === 401) {
                // Redirecionar para login se não autorizado
                // window.location.href = '/login';
            }
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.error || errorData.message || `Erro ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Falha ao buscar dados de ${endpoint}:`, error);
        throw error;
    }
}

/**
 * Função genérica para enviar dados (POST, PUT, DELETE)
 * @param {string} endpoint - O caminho da API (ex: "/api/v1/clientes")
 * @param {object} data - O objeto a ser enviado como JSON
 * @param {string} method - O método HTTP (POST ou PUT)
 * @returns {Promise<any>} - A resposta em JSON
 */
export async function postData(endpoint, data, method = 'POST') {
    try {
        const token = localStorage.getItem('token');
        const headers = {
            'Content-Type': 'application/json'
        };
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: method,
            headers: headers,
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            // Prioriza a mensagem de erro vinda do backend
            throw new Error(errorData.error || errorData.message || `Erro ${response.status}`);
        }

        // Retorna JSON se houver, ou um objeto de sucesso se não houver
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.indexOf("application/json") !== -1) {
            return await response.json();
        } else {
            return { success: true, status: response.status };
        }

    } catch (error) {
        console.error(`Falha ao enviar dados para ${endpoint}:`, error);
        throw error;
    }
}

/**
 * Função genérica para deletar dados (DELETE)
 * @param {string} endpoint - O caminho da API (ex: "/api/v1/clientes/1")
 * @returns {Promise<any>} - A resposta em JSON ou status
 */
export async function deleteData(endpoint) {
    try {
        const token = localStorage.getItem('token');
        const headers = {};
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE',
            headers: headers
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.error || errorData.message || `Erro ${response.status}`);
        }

        return { success: true };
    } catch (error) {
        console.error(`Falha ao deletar dados de ${endpoint}:`, error);
        throw error;
    }
}

/**
 * Formata um número para moeda BRL
 * @param {number} value - O valor numérico
 * @returns {string} - O valor formatado (ex: "R$ 89,90")
 */
export function formatCurrency(value) {
    if (typeof value !== 'number') {
        value = parseFloat(value) || 0;
    }
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

/**
 * Formata uma data (string ou Date) para o padrão dd/MM/yyyy
 * @param {string | Date} dateString - A data
 * @returns {string} - A data formatada
 */
export function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString);
        // Adiciona 1 dia (timezone fix) se necessário
        if (dateString.length <= 10) {
            date.setMinutes(date.getMinutes() + date.getTimezoneOffset());
        }
        return date.toLocaleDateString('pt-BR');
    } catch (e) {
        return dateString; // Retorna o original se falhar
    }
}
