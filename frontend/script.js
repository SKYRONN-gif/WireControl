// ===== CONFIGURAÇÃO DA API =====
// URL base da API REST do Spring Boot
const API_URL = 'http://localhost:8080/api/fios';
// localhost = este computador | 8080 = porta | /api/fios = endpoint

// ===== ELEMENTOS DO DOM =====
// DOM = Document Object Model (estrutura HTML que JS manipula)
// document.getElementById = busca elemento pelo atributo id=""
const formFio = document.getElementById('form-fio'); // Formulário de adicionar
const tabelaBody = document.querySelector('#tabela-fios tbody'); // Corpo da tabela
// querySelector = busca usando seletores CSS (#id, .class, tag)
const dataHoraElement = document.getElementById('data-hora'); // Span da hora
const toastContainer = document.getElementById('toast-container'); // Container das notificações

// ===== SISTEMA DE NOTIFICAÇÕES (TOAST) =====

/**
 * Mostra uma notificação toast customizada
 * @param {string} type - Tipo: 'success', 'error', 'warning'
 * @param {string} title - Título da notificação
 * @param {string} message - Mensagem da notificação
 */
function showToast(type, title, message) {
    // Cria o elemento toast
    const toast = document.createElement('div');
    // createElement = cria elemento HTML novo (não existe no DOM ainda)
    toast.className = `toast ${type}`;
    // className = define as classes CSS | template string com variável

    // Define o ícone baseado no tipo
    let iconName = 'check-circle'; // padrão success (ícone de check ✓)
    if (type === 'error') iconName = 'alert-circle'; // Ícone de ! dentro de círculo
    if (type === 'warning') iconName = 'alert-triangle'; // Ícone de ! dentro de triângulo

    // Monta o HTML do toast
    toast.innerHTML = `
        <i class="toast-icon" data-lucide="${iconName}"></i>
        <div class="toast-content">
            <div class="toast-title">${title}</div>
            <div class="toast-message">${message}</div>
        </div>
        <button class="toast-close" onclick="this.parentElement.remove()">
            <i data-lucide="x"></i>
        </button>
    `;
    // innerHTML = define conteúdo HTML interno
    // ${variavel} = interpolação (insere valor da variável)
    // this.parentElement.remove() = remove o elemento pai (o toast)

    // Adiciona ao container
    toastContainer.appendChild(toast);
    // appendChild = adiciona como filho (insere no final)

    // Inicializa os ícones do Lucide
    lucide.createIcons();
    // Transforma <i data-lucide="..."> em ícones SVG

    // Remove automaticamente após 5 segundos
    setTimeout(() => {
        // setTimeout = executa função depois de X milissegundos
        toast.style.animation = 'slideOut 0.3s ease'; // Animação de saída
        setTimeout(() => toast.remove(), 300); // Remove após animação (300ms)
    }, 5000); // 5000ms = 5 segundos
}

// ===== FUNÇÕES =====

/**
 * Atualiza a data e hora no cabeçalho
 */
function atualizarDataHora() {
    const agora = new Date(); // Cria objeto Date com data/hora atual
    const opcoes = {
        // Objeto com opções de formatação
        day: '2-digit',    // Dia com 2 dígitos (01, 02, 15...)
        month: '2-digit',  // Mês com 2 dígitos (01, 02, 12...)
        year: 'numeric',   // Ano completo (2025)
        hour: '2-digit',   // Hora com 2 dígitos (09, 14, 23...)
        minute: '2-digit'  // Minuto com 2 dígitos (00, 30, 59...)
    };
    const dataFormatada = agora.toLocaleString('pt-BR', opcoes);
    // toLocaleString = formata data/hora no padrão brasileiro (DD/MM/YYYY HH:MM)
    dataHoraElement.textContent = dataFormatada;
    // textContent = define o texto interno do elemento
}

/**
 * Carrega todos os fios do banco e exibe na tabela
 */
async function carregarFios() {
    // async = função assíncrona (pode usar await)
    try {
        // try/catch = tratamento de erros
        const response = await fetch(API_URL);
        // fetch = faz requisição HTTP (busca dados da API)
        // await = espera a resposta antes de continuar
        // response = objeto com a resposta (status, headers, body)

        if (!response.ok) {
            // response.ok = true se status 200-299, false se erro
            throw new Error('Erro ao carregar fios');
            // throw = lança erro (vai pro catch)
        }

        const fios = await response.json();
        // response.json() = converte resposta de JSON pra array JavaScript
        exibirFios(fios); // Chama função pra exibir na tabela

    } catch (error) {
        // catch = executado se der erro no try
        console.error('Erro:', error);
        // console.error = mostra erro no console (F12)
        showToast('error', 'Erro ao Carregar', 'Não foi possível conectar ao servidor. Verifique se está rodando.');
    }
}

/**
 * Exibe os fios na tabela
 * @param {Array} fios - Array com os objetos Fio
 */
function exibirFios(fios) {
    tabelaBody.innerHTML = ''; // Limpa tbody (remove linhas antigas)

    if (fios.length === 0) {
        // Se array está vazio (sem fios)
        tabelaBody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 3rem; color: var(--text-secondary);">
                    <i data-lucide="inbox" style="width: 48px; height: 48px; margin-bottom: 1rem; opacity: 0.5;"></i>
                    <div style="font-size: 1rem; font-weight: 600; margin-bottom: 0.5rem;">Nenhum fio cadastrado</div>
                    <div style="font-size: 0.875rem;">Adicione um novo fio usando o formulário acima</div>
                </td>
            </tr>
        `;
        // colspan="6" = célula ocupa 6 colunas (largura total)
        lucide.createIcons(); // Inicializa ícone
        return; // Para execução aqui
    }

    // Loop: percorre cada fio do array
    fios.forEach(fio => {
        // forEach = executa função pra cada elemento
        // fio = elemento atual (objeto Fio)
        const tr = document.createElement('tr'); // Cria linha <tr>
        tr.innerHTML = `
            <td>#${fio.id}</td>
            <td><strong>${fio.codigo}</strong></td>
            <td>${fio.descricao}</td>
            <td>${fio.bitola}</td>
            <td><strong>${fio.quantidade}m</strong></td>
            <td>
                <button class="btn-action btn-edit" onclick="editarFio(${fio.id})" style="margin-right: 0.5rem;">
                    <i data-lucide="edit" style="width: 14px; height: 14px; margin-right: 4px;"></i>
                    Editar
                </button>
                <button class="btn-action btn-delete" onclick="deletarFio(${fio.id}, '${fio.codigo}')">
                    <i data-lucide="trash-2" style="width: 14px; height: 14px; margin-right: 4px;"></i>
                    Deletar
                </button>
            </td>
        `;
        // 🆕 ADICIONADO: Botão Editar (azul) antes do Deletar
        // onclick="editarFio(${fio.id})" = chama função passando ID quando clica

        tabelaBody.appendChild(tr); // Adiciona linha no tbody
    });

    // Inicializa os ícones do Lucide
    lucide.createIcons();
}

/**
 * Adiciona um fio novo
 * @param {Event} event - Evento do submit do formulário
 */
async function adicionarFio(event) {
    event.preventDefault();
    // preventDefault = previne comportamento padrão do form (recarregar página)

    console.log('========== ADICIONANDO FIO ==========');

    // Pega os valores dos campos
    const codigo = document.getElementById('codigo').value.trim();
    // .value = valor digitado no input
    // .trim() = remove espaços em branco no início/fim
    const descricao = document.getElementById('descricao').value.trim();
    const bitola = document.getElementById('bitola').value.trim();
    const quantidade = parseFloat(document.getElementById('quantidade').value);
    // parseFloat = converte string em número decimal ("100.50" → 100.50)

    // Validação básica
    if (!codigo || !descricao || !bitola || isNaN(quantidade)) {
        // isNaN = is Not a Number (verifica se não é número)
        showToast('warning', 'Atenção', 'Por favor, preencha todos os campos corretamente.');
        return; // Para aqui
    }

    if (quantidade <= 0) {
        showToast('warning', 'Atenção', 'A quantidade deve ser maior que zero.');
        return;
    }

    // Cria o objeto fio
    const novoFio = {
        codigo: codigo,
        descricao: descricao,
        bitola: bitola,
        quantidade: quantidade
    };
    // Objeto JavaScript: { chave: valor }

    console.log('Enviando:', novoFio);

    try {
        const response = await fetch(API_URL, {
            method: 'POST', // Método HTTP POST (criar)
            headers: {
                'Content-Type': 'application/json' // Informa que envia JSON
            },
            body: JSON.stringify(novoFio) // Converte objeto pra JSON string
        });

        console.log('Status da resposta:', response.status);

        if (response.status === 409) {
            // 409 Conflict = código já existe
            showToast('error', 'Código Duplicado', `O código "${codigo}" já existe no sistema.`);
            return;
        }

        if (!response.ok) {
            throw new Error('Erro ao adicionar fio');
        }

        const fioSalvo = await response.json(); // Converte resposta pra objeto
        console.log('✅ Fio salvo:', fioSalvo);

        // Limpa o formulário
        limparFormulario();

        // Recarrega a tabela
        carregarFios();

        // Mostra notificação de sucesso
        showToast('success', 'Fio Adicionado', `${codigo} - ${descricao} foi adicionado com sucesso!`);

    } catch (error) {
        console.error('❌ Erro:', error);
        showToast('error', 'Erro ao Adicionar', 'Ocorreu um erro ao salvar o fio. Tente novamente.');
    }
}

/**
 * Limpa todos os campos do formulário
 */
function limparFormulario() {
    formFio.reset(); // reset() = limpa todos os inputs
    // Foca no primeiro campo
    document.getElementById('codigo').focus();
    // focus() = coloca cursor no campo
}

/**
 * Deleta um fio
 * @param {number} id - ID do fio a ser deletado
 * @param {string} codigo - Código do fio (pra exibir na confirmação)
 */
async function deletarFio(id, codigo) {
    // Notificação de confirmação
    const confirma = confirm(`Tem certeza que deseja deletar o fio "${codigo}"?\n\nEsta ação não pode ser desfeita.`);
    // confirm = popup com OK/Cancelar
    // \n\n = duas quebras de linha
    // retorna true se OK, false se Cancelar

    if (!confirma) {
        return; // Se cancelou, para aqui
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            // Template string: http://localhost:8080/api/fios/1
            method: 'DELETE' // Método HTTP DELETE
        });

        if (!response.ok) {
            throw new Error('Erro ao deletar fio');
        }

        // Recarrega a tabela
        carregarFios();

        // Mostra notificação de sucesso
        showToast('success', 'Fio Deletado', `O fio "${codigo}" foi removido do estoque.`);

    } catch (error) {
        console.error('Erro:', error);
        showToast('error', 'Erro ao Deletar', 'Não foi possível deletar o fio. Tente novamente.');
    }
}

// =========================================
// 🆕 NOVO: FUNÇÕES DE EDIÇÃO
// =========================================

/**
 * Abre o modal de edição e carrega os dados do fio
 * @param {number} id - ID do fio a ser editado
 */
async function editarFio(id) {
    // Função chamada quando clica no botão Editar na tabela
    try {
        // Busca os dados do fio na API
        const response = await fetch(`${API_URL}/${id}`);
        // Faz GET em /api/fios/1 (busca fio por ID)

        if (!response.ok) {
            throw new Error('Erro ao buscar fio');
        }

        const fio = await response.json();
        // Converte resposta pra objeto Fio

        // Preenche os campos do modal com os dados do fio
        document.getElementById('edit-id').value = fio.id;
        // Campo hidden (invisível) que guarda o ID
        document.getElementById('edit-codigo').value = fio.codigo;
        document.getElementById('edit-descricao').value = fio.descricao;
        document.getElementById('edit-bitola').value = fio.bitola;
        document.getElementById('edit-quantidade').value = fio.quantidade;

        // Abre o modal
        abrirModalEditar();

    } catch (error) {
        console.error('Erro:', error);
        showToast('error', 'Erro ao Carregar', 'Não foi possível carregar os dados do fio.');
    }
}

/**
 * Abre o modal de edição
 */
function abrirModalEditar() {
    const modal = document.getElementById('modal-editar');
    // Busca o modal pelo ID
    modal.classList.add('active');
    // classList.add = adiciona classe CSS
    // Quando adiciona "active", modal fica visível (display: flex)

    document.body.style.overflow = 'hidden';
    // overflow = 'hidden' = esconde scroll da página
    // Previne rolar a página enquanto modal está aberto

    // Inicializa ícones do Lucide (caso tenha algum novo)
    lucide.createIcons();
}

/**
 * Fecha o modal de edição
 */
function fecharModalEditar() {
    const modal = document.getElementById('modal-editar');
    modal.classList.remove('active');
    // classList.remove = remove classe CSS
    // Remove "active", modal fica escondido (display: none)

    document.body.style.overflow = '';
    // Restaura scroll da página (remove a propriedade)

    // Limpa o formulário
    document.getElementById('form-editar').reset();
    // reset() = limpa todos os campos do formulário
}

/**
 * Salva as alterações do fio editado
 * @param {Event} event - Evento do submit do formulário
 */
async function salvarEdicao(event) {
    // Função chamada quando clica em "Salvar Alterações"
    event.preventDefault();
    // Previne reload da página

    // Pega os valores dos campos do modal
    const id = document.getElementById('edit-id').value;
    // Pega o ID do campo hidden
    const codigo = document.getElementById('edit-codigo').value.trim();
    const descricao = document.getElementById('edit-descricao').value.trim();
    const bitola = document.getElementById('edit-bitola').value.trim();
    const quantidade = parseFloat(document.getElementById('edit-quantidade').value);

    // Validação
    if (!codigo || !descricao || !bitola || isNaN(quantidade)) {
        showToast('warning', 'Atenção', 'Por favor, preencha todos os campos corretamente.');
        return;
    }

    if (quantidade <= 0) {
        showToast('warning', 'Atenção', 'A quantidade deve ser maior que zero.');
        return;
    }

    // Cria objeto com dados atualizados
    const fioAtualizado = {
        id: parseInt(id), // parseInt = converte string pra número inteiro
        codigo: codigo,
        descricao: descricao,
        bitola: bitola,
        quantidade: quantidade
    };

    try {
        // Envia requisição PUT pra atualizar
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'PUT', // PUT = atualizar
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(fioAtualizado)
            // Converte objeto pra JSON
        });

        if (!response.ok) {
            throw new Error('Erro ao atualizar fio');
        }

        const fioSalvo = await response.json();
        console.log('✅ Fio atualizado:', fioSalvo);

        // Fecha o modal
        fecharModalEditar();

        // Recarrega a tabela (pra mostrar dados atualizados)
        carregarFios();

        // Mostra notificação de sucesso
        showToast('success', 'Fio Atualizado', `${codigo} foi atualizado com sucesso!`);

    } catch (error) {
        console.error('❌ Erro:', error);
        showToast('error', 'Erro ao Atualizar', 'Ocorreu um erro ao salvar as alterações.');
    }
}

// ===== EVENTOS =====

// Adiciona evento de submit no formulário de ADICIONAR
if (formFio) {
    console.log('✅ Formulário de adicionar encontrado! Adicionando evento...');
    formFio.addEventListener('submit', adicionarFio);
    // addEventListener = registra ouvinte de evento
    // 'submit' = quando formulário é enviado
    // adicionarFio = função a executar
} else {
    console.error('❌ ERRO: Formulário não encontrado!');
}

// 🆕 NOVO: Adiciona evento de submit no formulário de EDITAR
const formEditar = document.getElementById('form-editar');
if (formEditar) {
    console.log('✅ Formulário de editar encontrado! Adicionando evento...');
    formEditar.addEventListener('submit', salvarEdicao);
    // Quando submeter form de edição, chama salvarEdicao
} else {
    console.error('❌ ERRO: Formulário de editar não encontrado!');
}

// 🆕 NOVO: Fecha modal ao pressionar ESC
document.addEventListener('keydown', (e) => {
    // keydown = quando pressiona tecla
    // e = event (objeto com info do evento)
    if (e.key === 'Escape') {
        // e.key = qual tecla foi pressionada
        // 'Escape' = tecla ESC
        fecharModalEditar();
        // Fecha modal se estiver aberto
    }
});

// ===== INICIALIZAÇÃO =====

// Atualiza data/hora a cada 1 segundo
setInterval(atualizarDataHora, 1000);
// setInterval = executa função repetidamente
// 1000ms = 1 segundo

// Atualiza data/hora imediatamente (sem esperar 1 segundo)
atualizarDataHora();

// Carrega os fios quando a página carrega
carregarFios();

console.log('✅ Sistema inicializado com sucesso!');