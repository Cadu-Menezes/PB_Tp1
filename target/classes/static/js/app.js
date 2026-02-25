/*
    Módulo principal da aplicação web.
    Gerencia a comunicação com a API REST e manipula a interface.
*/

const API_URL = '/api';

// Estado da aplicação
let produtosCache = [];
let categoriasCache = [];
let idProdutoExcluir = null;

// ===== INICIALIZAÇÃO =====

document.addEventListener('DOMContentLoaded', () => {
    carregarCategorias();
    carregarProdutos();
    carregarEstatisticas();
});

// ===== NAVEGAÇÃO =====

/*
    Alterna a exibição entre as páginas da aplicação.
    Gerencia os links ativos da navbar e as seções visíveis.
*/
function navegarPara(pagina) {
    // Esconder todas as páginas
    document.getElementById('pagina-produtos').classList.add('d-none');
    document.getElementById('pagina-categorias').classList.add('d-none');
    document.getElementById('pagina-estoque').classList.add('d-none');

    // Remover classe active de todos os links
    document.getElementById('nav-produtos').classList.remove('active');
    document.getElementById('nav-categorias').classList.remove('active');
    document.getElementById('nav-estoque').classList.remove('active');

    // Mostrar página selecionada e ativar link correspondente
    switch (pagina) {
        case 'produtos':
            document.getElementById('pagina-produtos').classList.remove('d-none');
            document.getElementById('nav-produtos').classList.add('active');
            carregarProdutos();
            break;
        case 'categorias':
            document.getElementById('pagina-categorias').classList.remove('d-none');
            document.getElementById('nav-categorias').classList.add('active');
            preencherSelectCategorias('select-categoria-filtro');
            break;
        case 'estoque':
            document.getElementById('pagina-estoque').classList.remove('d-none');
            document.getElementById('nav-estoque').classList.add('active');
            carregarEstoqueBaixo();
            break;
    }

    carregarEstatisticas();
}

// ===== CATEGORIAS =====

/*
    Carrega todas as categorias da API e armazena no cache.
    Preenche os selects de categoria no formulário e nos filtros.
*/
async function carregarCategorias() {
    try {
        const resposta = await fetch(`${API_URL}/categorias`);
        categoriasCache = await resposta.json();
        preencherSelectCategorias('produto-categoria');
        preencherSelectCategorias('filtro-categoria');
        preencherSelectCategorias('select-categoria-filtro');
    } catch (erro) {
        exibirAlerta('Erro ao carregar categorias: ' + erro.message, 'danger');
    }
}

/*
    Preenche um elemento <select> com as categorias do cache.
*/
function preencherSelectCategorias(idSelect) {
    const select = document.getElementById(idSelect);
    if (!select) return;

    const valorAtual = select.value;
    // Manter a primeira opção (placeholder)
    const primeiraOpcao = select.options[0];
    select.innerHTML = '';
    select.appendChild(primeiraOpcao);

    categoriasCache.forEach(cat => {
        const option = document.createElement('option');
        option.value = cat.valor;
        option.textContent = cat.nome;
        select.appendChild(option);
    });

    // Restaurar valor selecionado se existia
    if (valorAtual) {
        select.value = valorAtual;
    }
}

// ===== PRODUTOS - LISTAGEM =====

/*
    Carrega todos os produtos da API e renderiza na tabela principal.
*/
async function carregarProdutos() {
    try {
        const resposta = await fetch(`${API_URL}/produtos`);
        produtosCache = await resposta.json();
        renderizarTabelaProdutos(produtosCache);
    } catch (erro) {
        exibirAlerta('Erro ao carregar produtos: ' + erro.message, 'danger');
    }
}

/*
    Renderiza a lista de produtos na tabela HTML.
    Exibe mensagem caso não haja produtos.
*/
function renderizarTabelaProdutos(produtos) {
    const corpo = document.getElementById('corpo-tabela-produtos');
    const msgVazia = document.getElementById('msg-lista-vazia');
    const tabela = document.getElementById('tabela-produtos');

    corpo.innerHTML = '';

    if (produtos.length === 0) {
        tabela.classList.add('d-none');
        msgVazia.classList.remove('d-none');
        return;
    }

    tabela.classList.remove('d-none');
    msgVazia.classList.add('d-none');

    produtos.forEach(produto => {
        const tr = document.createElement('tr');
        tr.setAttribute('data-id', produto.id);

        tr.innerHTML = `
            <td class="fw-semibold">${escapeHtml(produto.nome)}</td>
            <td>${escapeHtml(produto.descricao || '-')}</td>
            <td class="preco-valor">R$ ${formatarPreco(produto.preco)}</td>
            <td><span class="badge bg-primary">${escapeHtml(produto.categoriaNome)}</span></td>
            <td>${criarBadgeEstoque(produto.quantidadeEstoque)}</td>
            <td>
                <div class="btn-group-acoes">
                    <button class="btn btn-sm btn-outline-primary btn-acoes" 
                            title="Editar" onclick="abrirModalEditar('${produto.id}')">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-info btn-acoes" 
                            title="Estoque" onclick="abrirModalEstoque('${produto.id}', '${escapeHtml(produto.nome)}', ${produto.quantidadeEstoque})">
                        <i class="bi bi-boxes"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger btn-acoes" 
                            title="Excluir" onclick="abrirModalExcluir('${produto.id}', '${escapeHtml(produto.nome)}')">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        `;
        corpo.appendChild(tr);
    });
}

/*
    Filtra produtos na tabela com base no campo de busca e filtro de categoria.
*/
function filtrarProdutos() {
    const textoBusca = document.getElementById('campo-busca').value.toLowerCase();
    const categoriaFiltro = document.getElementById('filtro-categoria').value;

    const filtrados = produtosCache.filter(produto => {
        const correspondeNome = produto.nome.toLowerCase().includes(textoBusca);
        const correspondeCategoria = categoriaFiltro === '' || produto.categoria === categoriaFiltro;
        return correspondeNome && correspondeCategoria;
    });

    renderizarTabelaProdutos(filtrados);
}

// ===== PRODUTOS - CRIAR =====

/*
    Abre o modal em modo de criação, limpando todos os campos.
*/
function abrirModalCriar() {
    document.getElementById('modal-titulo').innerHTML = '<i class="bi bi-plus-circle"></i> Novo Produto';
    document.getElementById('form-produto').reset();
    document.getElementById('produto-id').value = '';
    limparValidacao();
    new bootstrap.Modal(document.getElementById('modal-produto')).show();
}

// ===== PRODUTOS - EDITAR =====

/*
    Abre o modal em modo de edição, preenchendo os campos com dados do produto.
*/
async function abrirModalEditar(id) {
    try {
        const resposta = await fetch(`${API_URL}/produtos/${id}`);
        if (!resposta.ok) {
            const erro = await resposta.json();
            throw new Error(erro.mensagem);
        }
        const produto = await resposta.json();

        document.getElementById('modal-titulo').innerHTML = '<i class="bi bi-pencil"></i> Editar Produto';
        document.getElementById('produto-id').value = produto.id;
        document.getElementById('produto-nome').value = produto.nome;
        document.getElementById('produto-descricao').value = produto.descricao;
        document.getElementById('produto-preco').value = produto.preco;
        document.getElementById('produto-categoria').value = produto.categoria;
        document.getElementById('produto-estoque').value = produto.quantidadeEstoque;

        limparValidacao();
        new bootstrap.Modal(document.getElementById('modal-produto')).show();

    } catch (erro) {
        exibirAlerta('Erro ao carregar produto: ' + erro.message, 'danger');
    }
}

// ===== PRODUTOS - SALVAR (CRIAR OU ATUALIZAR) =====

/*
    Salva o produto (cria novo ou atualiza existente).
    Valida os campos do formulário antes do envio.
*/
async function salvarProduto() {
    if (!validarFormulario()) return;

    const id = document.getElementById('produto-id').value;
    const dados = {
        nome: document.getElementById('produto-nome').value.trim(),
        descricao: document.getElementById('produto-descricao').value.trim(),
        preco: document.getElementById('produto-preco').value,
        categoria: document.getElementById('produto-categoria').value,
        quantidadeEstoque: document.getElementById('produto-estoque').value
    };

    const editando = id !== '';
    const url = editando ? `${API_URL}/produtos/${id}` : `${API_URL}/produtos`;
    const metodo = editando ? 'PUT' : 'POST';

    try {
        const resposta = await fetch(url, {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });

        const resultado = await resposta.json();

        if (!resposta.ok) {
            throw new Error(resultado.mensagem || 'Erro ao salvar produto');
        }

        // Fechar modal e recarregar dados
        bootstrap.Modal.getInstance(document.getElementById('modal-produto')).hide();
        exibirAlerta(editando ? 'Produto atualizado com sucesso!' : 'Produto criado com sucesso!', 'success');
        carregarProdutos();
        carregarEstatisticas();

    } catch (erro) {
        exibirAlerta(erro.message, 'danger');
    }
}

// ===== PRODUTOS - EXCLUIR =====

/*
    Abre o modal de confirmação de exclusão.
*/
function abrirModalExcluir(id, nome) {
    idProdutoExcluir = id;
    document.getElementById('nome-produto-excluir').textContent = nome;
    new bootstrap.Modal(document.getElementById('modal-excluir')).show();
}

/*
    Confirma e executa a exclusão do produto.
*/
async function confirmarExclusao() {
    if (!idProdutoExcluir) return;

    try {
        const resposta = await fetch(`${API_URL}/produtos/${idProdutoExcluir}`, {
            method: 'DELETE'
        });

        const resultado = await resposta.json();

        if (!resposta.ok) {
            throw new Error(resultado.mensagem || 'Erro ao excluir produto');
        }

        bootstrap.Modal.getInstance(document.getElementById('modal-excluir')).hide();
        exibirAlerta('Produto excluído com sucesso!', 'success');
        idProdutoExcluir = null;
        carregarProdutos();
        carregarEstatisticas();

    } catch (erro) {
        exibirAlerta(erro.message, 'danger');
    }
}

// ===== ESTOQUE =====

/*
    Abre o modal de atualização de estoque para um produto.
*/
function abrirModalEstoque(id, nome, estoqueAtual) {
    document.getElementById('estoque-produto-id').value = id;
    document.getElementById('nome-produto-estoque').textContent = nome;
    document.getElementById('estoque-atual').textContent = estoqueAtual;
    document.getElementById('nova-quantidade').value = estoqueAtual;
    new bootstrap.Modal(document.getElementById('modal-estoque')).show();
}

/*
    Salva a nova quantidade de estoque do produto.
*/
async function salvarEstoque() {
    const id = document.getElementById('estoque-produto-id').value;
    const quantidade = parseInt(document.getElementById('nova-quantidade').value);

    if (isNaN(quantidade) || quantidade < 0) {
        document.getElementById('nova-quantidade').classList.add('is-invalid');
        return;
    }

    try {
        const resposta = await fetch(`${API_URL}/produtos/${id}/estoque`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ quantidade: quantidade })
        });

        const resultado = await resposta.json();

        if (!resposta.ok) {
            throw new Error(resultado.mensagem || 'Erro ao atualizar estoque');
        }

        bootstrap.Modal.getInstance(document.getElementById('modal-estoque')).hide();
        exibirAlerta('Estoque atualizado com sucesso!', 'success');
        carregarProdutos();
        carregarEstatisticas();

    } catch (erro) {
        exibirAlerta(erro.message, 'danger');
    }
}

/*
    Carrega produtos com estoque baixo na página de estoque.
*/
async function carregarEstoqueBaixo() {
    const limite = document.getElementById('input-limite-estoque').value || 5;

    try {
        const resposta = await fetch(`${API_URL}/produtos/estoque/baixo?limite=${limite}`);
        const produtos = await resposta.json();
        const corpo = document.getElementById('corpo-tabela-estoque');
        corpo.innerHTML = '';

        if (produtos.length === 0) {
            corpo.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center text-muted py-4">
                        <i class="bi bi-check-circle text-success fs-4"></i><br>
                        Nenhum produto com estoque baixo.
                    </td>
                </tr>`;
            return;
        }

        produtos.forEach(produto => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="fw-semibold">${escapeHtml(produto.nome)}</td>
                <td><span class="badge bg-primary">${escapeHtml(produto.categoriaNome)}</span></td>
                <td class="preco-valor">R$ ${formatarPreco(produto.preco)}</td>
                <td>${criarBadgeEstoque(produto.quantidadeEstoque)}</td>
                <td class="text-center">
                    <button class="btn btn-sm btn-info text-white" 
                            onclick="abrirModalEstoque('${produto.id}', '${escapeHtml(produto.nome)}', ${produto.quantidadeEstoque})">
                        <i class="bi bi-boxes"></i> Repor
                    </button>
                </td>`;
            corpo.appendChild(tr);
        });

    } catch (erro) {
        exibirAlerta('Erro ao carregar estoque baixo: ' + erro.message, 'danger');
    }
}

/*
    Carrega produtos de uma categoria específica.
*/
async function carregarPorCategoria() {
    const categoria = document.getElementById('select-categoria-filtro').value;
    const corpo = document.getElementById('corpo-tabela-categorias');

    if (!categoria) {
        corpo.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-muted py-4">
                    Selecione uma categoria para visualizar os produtos.
                </td>
            </tr>`;
        return;
    }

    try {
        const resposta = await fetch(`${API_URL}/produtos/categoria/${categoria}`);
        const produtos = await resposta.json();
        corpo.innerHTML = '';

        if (produtos.length === 0) {
            corpo.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center text-muted py-4">
                        Nenhum produto encontrado nesta categoria.
                    </td>
                </tr>`;
            return;
        }

        produtos.forEach(produto => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="fw-semibold">${escapeHtml(produto.nome)}</td>
                <td>${escapeHtml(produto.descricao || '-')}</td>
                <td class="preco-valor">R$ ${formatarPreco(produto.preco)}</td>
                <td>${criarBadgeEstoque(produto.quantidadeEstoque)}</td>`;
            corpo.appendChild(tr);
        });

    } catch (erro) {
        exibirAlerta('Erro ao carregar produtos por categoria: ' + erro.message, 'danger');
    }
}

// ===== ESTATÍSTICAS =====

/*
    Carrega e exibe as estatísticas nos cards do topo da página.
*/
async function carregarEstatisticas() {
    try {
        const resposta = await fetch(`${API_URL}/estatisticas`);
        const stats = await resposta.json();

        document.getElementById('stat-total').textContent = stats.totalProdutos;
        document.getElementById('stat-sem-estoque').textContent = stats.produtosSemEstoque;
        document.getElementById('stat-estoque-baixo').textContent = stats.produtosEstoqueBaixo;

    } catch (erro) {
        console.error('Erro ao carregar estatísticas:', erro);
    }
}

// ===== VALIDAÇÃO =====

/*
    Valida todos os campos do formulário de produto.
    Retorna true se todos os campos são válidos.
*/
function validarFormulario() {
    let valido = true;
    limparValidacao();

    const nome = document.getElementById('produto-nome');
    if (!nome.value.trim()) {
        nome.classList.add('is-invalid');
        valido = false;
    }

    const preco = document.getElementById('produto-preco');
    if (!preco.value || parseFloat(preco.value) < 0) {
        preco.classList.add('is-invalid');
        valido = false;
    }

    const categoria = document.getElementById('produto-categoria');
    if (!categoria.value) {
        categoria.classList.add('is-invalid');
        valido = false;
    }

    const estoque = document.getElementById('produto-estoque');
    if (estoque.value === '' || parseInt(estoque.value) < 0) {
        estoque.classList.add('is-invalid');
        valido = false;
    }

    return valido;
}

/*
    Remove as classes de validação de todos os campos do formulário.
*/
function limparValidacao() {
    const campos = ['produto-nome', 'produto-preco', 'produto-categoria', 'produto-estoque'];
    campos.forEach(id => {
        document.getElementById(id).classList.remove('is-invalid');
        document.getElementById(id).classList.remove('is-valid');
    });
}

// ===== UTILITÁRIOS =====

/*
    Exibe um alerta Bootstrap temporário no canto superior direito.
*/
function exibirAlerta(mensagem, tipo) {
    const container = document.getElementById('alertas-container');
    const alerta = document.createElement('div');
    alerta.className = `alert alert-${tipo} alert-dismissible fade show`;
    alerta.setAttribute('role', 'alert');

    const icone = tipo === 'success' ? 'check-circle' : 
                  tipo === 'danger' ? 'exclamation-triangle' : 'info-circle';

    alerta.innerHTML = `
        <i class="bi bi-${icone}"></i> ${escapeHtml(mensagem)}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
    `;

    container.appendChild(alerta);

    // Remover automaticamente após 4 segundos
    setTimeout(() => {
        if (alerta.parentNode) {
            alerta.classList.remove('show');
            setTimeout(() => alerta.remove(), 300);
        }
    }, 4000);
}

/*
    Formata um número como preço em formato brasileiro.
*/
function formatarPreco(valor) {
    return parseFloat(valor).toFixed(2).replace('.', ',');
}

/*
    Cria o badge HTML de estoque com cor baseada na quantidade.
*/
function criarBadgeEstoque(quantidade) {
    let classe;
    if (quantidade === 0) {
        classe = 'badge-estoque-zero';
    } else if (quantidade <= 5) {
        classe = 'badge-estoque-baixo';
    } else {
        classe = 'badge-estoque-ok';
    }
    return `<span class="badge ${classe}">${quantidade}</span>`;
}

/*
    Sanitiza texto para evitar injeção de HTML (XSS).
*/
function escapeHtml(texto) {
    if (!texto) return '';
    const div = document.createElement('div');
    div.textContent = texto;
    return div.innerHTML;
}
