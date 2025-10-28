package com.flessak.estoque_fios.service;
// package → palavra-chave que define em qual "pasta lógica" do projeto essa classe vive.
// com.flessak.estoque_fios.service → caminho/namespace. Ajuda o Java a localizar a classe.

import com.flessak.estoque_fios.model.Fio;
// import → traz uma classe de outro lugar para usar aqui.
// com.flessak...Fio → caminho completo da classe Fio.

import com.flessak.estoque_fios.repository.FioRepository;
// importa o repositório para podermos falar com o banco via JPA.

import org.springframework.beans.factory.annotation.Autowired;
// importa a anotação @Autowired, que permite o Spring "injetar" dependências.

import org.springframework.stereotype.Service;
// importa a anotação @Service, que marca essa classe como camada de serviço.

import java.util.List;
// importa a interface List (coleção ordenada).

import java.util.Optional;
// importa Optional, que representa "um valor que pode existir ou não".

/**
 * Camada de serviço responsável pela regra de negócio dos fios.
 */
@Service
// @Service → anotação que diz ao Spring: "essa classe é um serviço (business logic)".
// o Spring registra essa classe nos seus "beans" para poder injetá-la em outras partes.

public class FioService {
    // public → visibilidade: qualquer outra classe pode usar essa classe.
    // class → define que estamos criando uma classe.
    // FioService → nome da classe.

    private final FioRepository fioRepository;
    // private → visibilidade: só esta classe pode acessar diretamente essa variável.
    // final → significa que a variável só recebe valor uma vez (imutável depois de inicializada).
    // FioRepository fioRepository → declaração do tipo e do nome da variável.

    @Autowired
    // @Autowired → dizendo pro Spring: "quando criar essa classe, por favor me entregue
    // uma instância do FioRepository no construtor abaixo".
    public FioService(FioRepository fioRepository) {
        // public → construtor público para permitir injeção.
        // FioService(...) → construtor (método chamado ao criar a classe).
        // FioRepository fioRepository → parâmetro que o Spring vai passar automaticamente.
        this.fioRepository = fioRepository;
        // this → referência ao objeto atual (variável da classe).
        // this.fioRepository = fioRepository → atribuição: guarda o parâmetro na variável final.
    }

    // =============== MÉTODOS DE CONSULTA ===============

    /** Lista todos os fios cadastrados. */
    public List<Fio> listarTodos() {
        // public → método acessível de outras classes.
        // List<Fio> → retorna uma lista de objetos Fio.
        // listarTodos() → nome do método, sem parâmetros.
        return fioRepository.findAll();
        // return → devolve o resultado ao chamador.
        // fioRepository.findAll() → chama o método findAll() do repositório,
        // que retorna todos os registros da tabela "fios".
    }

    /** Busca um fio pelo ID. */
    public Optional<Fio> buscarPorId(Long id) {
        // Optional<Fio> → pode retornar um Fio ou nada (Optional vazio).
        // Long id → parâmetro que representa o id que queremos buscar.
        return fioRepository.findById(id);
        // findById(id) → busca no banco pelo id; devolve Optional<Fio>.
    }

    /** Busca um fio pelo código. */
    public Optional<Fio> buscarPorCodigo(String codigo) {
        // String codigo → o código que será procurado.
        return fioRepository.findByCodigo(codigo);
        // findByCodigo → método criado no repositório; retorna Optional<Fio>.
    }

    /** Busca um fio pela descrição. */
    public Optional<Fio> buscarPorDescricao(String descricao) {
        return fioRepository.findByDescricao(descricao);
    }

    // =============== MÉTODOS DE CRIAÇÃO E ATUALIZAÇÃO ===============

    /**
     * Salva um novo fio no banco de dados com validações.
     */
    public Fio salvar(Fio fio) {
        // Fio fio → parâmetro: o objeto Fio que queremos salvar.

        // VALIDAÇÃO 1: código obrigatório
        if (fio.getCodigo() == null || fio.getCodigo().isBlank()) {
            // if → condição. Avalia se o código é nulo ou vazio.
            // fio.getCodigo() → chama o getter do código no objeto fio.
            // == null → verifica se não existe valor.
            // .isBlank() → método de String que verifica se a string é vazia ou só tem espaços.
            throw new IllegalArgumentException("O código do fio é obrigatório.");
            // throw → lança uma exceção; para a execução normal e sinaliza erro.
            // IllegalArgumentException → tipo de exceção que indica argumento inválido.
        }

        // VALIDAÇÃO 2: código único
        if (fioRepository.findByCodigo(fio.getCodigo()).isPresent()) {
            // fioRepository.findByCodigo(...) → busca Optional<Fio>
            // .isPresent() → retorna true se o Optional tem valor (ou seja, já existe).
            throw new IllegalArgumentException("Já existe um fio cadastrado com esse código.");
        }

        // VALIDAÇÃO 3: descrição obrigatória
        if (fio.getDescricao() == null || fio.getDescricao().isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }

        // VALIDAÇÃO 4: bitola obrigatória
        if (fio.getBitola() == null || fio.getBitola().isBlank()) {
            throw new IllegalArgumentException("A bitola é obrigatória.");
        }

        // VALIDAÇÃO 5: quantidade não nula e não negativa
        if (fio.getQuantidade() == null || fio.getQuantidade() < 0) {
            // < 0 → compara valores numéricos (menor que zero).
            throw new IllegalArgumentException("A quantidade deve ser informada e não pode ser negativa.");
        }

        // se passou em todas as validações, salva no banco
        return fioRepository.save(fio);
        // save(fio) → salva o objeto: se não tiver id, insere; se tiver id, atualiza.
        // retorna o objeto salvo (possivelmente com id gerado).
    }

    /**
     * Atualiza a quantidade de um fio existente (entrada ou saída de estoque).
     */
    public Fio atualizarQuantidade(Long id, Double quantidadeAlterar) {
        // busca o fio por id; se não encontrar, lança uma exceção
        Fio fio = fioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fio não encontrado com ID: " + id));
        // fioRepository.findById(id) → Optional<Fio>
        // .orElseThrow(...) → se Optional vazio, executa a função que cria e lança a exceção.
        // () -> new RuntimeException(...) → expressão lambda que cria a exceção.
        // atribuimos o Fio encontrado à variável 'fio'.

        Double novaQuantidade = fio.getQuantidade() + quantidadeAlterar;
        // pega a quantidade atual (fio.getQuantidade()), soma a quantidadeAlterar e guarda em novaQuantidade.
        // se quantidadeAlterar for negativa, isso representa retirar do estoque.

        if (novaQuantidade < 0) {
            // garante que estoque não fique negativo
            throw new IllegalArgumentException("Quantidade insuficiente em estoque.");
        }

        fio.setQuantidade(novaQuantidade);
        // atualiza o objeto em memória com a nova quantidade.

        return fioRepository.save(fio);
        // persiste a alteração no banco e retorna o objeto atualizado.
    }

    /**
     * Atualiza os dados de um fio existente.
     */
    public Fio atualizar(Long id, Fio dadosAtualizados) {
        // busca o fio atual, ou lança exceção se não existir
        Fio fioExistente = fioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fio não encontrado com ID: " + id));

        // atualiza só campos válidos (se vierem não nulos e não vazios)
        if (dadosAtualizados.getDescricao() != null && !dadosAtualizados.getDescricao().isBlank()) {
            // != null → existe valor; !...isBlank() → não é vazio ou só espaços
            fioExistente.setDescricao(dadosAtualizados.getDescricao());
        }

        if (dadosAtualizados.getBitola() != null && !dadosAtualizados.getBitola().isBlank()) {
            fioExistente.setBitola(dadosAtualizados.getBitola());
        }

        if (dadosAtualizados.getQuantidade() != null && dadosAtualizados.getQuantidade() >= 0) {
            // >= 0 → aceita zero ou positivo (não aceita negativo)
            fioExistente.setQuantidade(dadosAtualizados.getQuantidade());
        }

        return fioRepository.save(fioExistente);
    }

    // =============== MÉTODOS DE EXCLUSÃO E VERIFICAÇÃO ===============

    /** Deleta um fio pelo ID. */
    public void deletar(Long id) {
        // existsById(id) → verifica se existe um registro com esse id (retorna boolean)
        if (!fioRepository.existsById(id)) {
            // ! → negação; se não existe, lança exceção
            throw new RuntimeException("Fio não encontrado para exclusão com ID: " + id);
        }
        fioRepository.deleteById(id);
        // deleteById(id) → remove o registro no banco.
    }

    /** Verifica se já existe um fio com o código informado. */
    public boolean codigoJaExiste(String codigo) {
        // findByCodigo(codigo) → Optional<Fio>
        // .isPresent() → true se já existe, false se não
        return fioRepository.findByCodigo(codigo).isPresent();
    }
}
