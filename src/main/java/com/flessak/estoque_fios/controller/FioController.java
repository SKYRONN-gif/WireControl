package com.flessak.estoque_fios.controller;
// package = pacote/pasta onde fica o código
// controller = pasta dos controladores (recebem requisições HTTP)

import com.flessak.estoque_fios.model.Fio;
// import = importar/trazer
// traz a classe Fio (entidade do banco)

import com.flessak.estoque_fios.service.FioService;
// traz o serviço (lógica de negócio)

import org.springframework.beans.factory.annotation.Autowired;
// traz @Autowired (injeção de dependência automática)

import org.springframework.http.HttpStatus;
// traz HttpStatus (códigos HTTP: 200, 404, 500, etc)

import org.springframework.http.ResponseEntity;
// traz ResponseEntity (resposta HTTP completa: status + corpo)

import org.springframework.web.bind.annotation.*;
// * = importa TODAS as anotações REST:
// @RestController, @GetMapping, @PostMapping, @PutMapping, @DeleteMapping, @PatchMapping
// @PathVariable, @RequestBody, @RequestParam, @RequestMapping, @CrossOrigin

import java.util.List;
// List = interface de lista (coleção ordenada de objetos)

import java.util.Optional;
// Optional = classe que pode conter um valor ou estar vazia (evita null)

@RestController
// @RestController = anotação que marca essa classe como controlador REST
// REST = API que usa HTTP pra comunicação
// essa classe vai receber requisições HTTP e devolver respostas JSON

@RequestMapping("/api/fios")
// @RequestMapping = define o caminho BASE de todas as rotas dessa classe
// "/api/fios" = todas as URLs começam com /api/fios
// exemplo: GET /api/fios, POST /api/fios, GET /api/fios/5

public class FioController {
// public = visível pra qualquer classe
// class = palavra-chave que declara uma classe
// FioController = nome da classe
// { = abre o corpo da classe

    @Autowired
    // @Autowired = injeção de dependência
    // Spring cria automaticamente uma instância do FioService
    // e "injeta" (passa) pra essa variável
    private FioService fioService;
    // private = só essa classe acessa diretamente
    // FioService = tipo da variável
    // fioService = nome da variável

    // ========================================
    // LISTAR TODOS OS FIOS
    // ========================================

    @GetMapping
    // @GetMapping = mapeia requisições HTTP GET
    // como não tem path aqui, usa o base: GET /api/fios
    public ResponseEntity<List<Fio>> listarTodos() {
        // public = método acessível de fora
        // ResponseEntity<List<Fio>> = tipo de retorno
        //   - ResponseEntity = envelope da resposta HTTP (status + corpo)
        //   - <List<Fio>> = o corpo contém uma lista de objetos Fio
        // listarTodos() = nome do método
        // () = sem parâmetros

        List<Fio> fios = fioService.listarTodos();
        // List<Fio> fios = cria variável local
        // fioService.listarTodos() = chama o método do serviço
        // serviço busca todos os fios no banco e retorna lista

        return ResponseEntity.ok(fios);
        // return = retorna/devolve
        // ResponseEntity.ok(...) = cria resposta com status 200 OK
        // (fios) = corpo da resposta (Spring converte pra JSON automaticamente)
    }

    // ========================================
    // BUSCAR FIO POR ID
    // ========================================

    @GetMapping("/{id}")
    // @GetMapping("/{id}") = mapeia GET com variável na URL
    // completo: GET /api/fios/{id}
    // {id} = placeholder (variável dinâmica)
    // exemplo: GET /api/fios/5 → id = 5
    public ResponseEntity<Fio> buscarPorId(@PathVariable Long id) {
        // ResponseEntity<Fio> = resposta com UM objeto Fio
        // @PathVariable = pega o valor do {id} da URL
        // Long id = parâmetro que recebe o ID

        Optional<Fio> fio = fioService.buscarPorId(id);
        // Optional<Fio> = pode ter um Fio ou estar vazio
        // fioService.buscarPorId(id) = busca no banco pelo ID

        return fio.map(ResponseEntity::ok)
                // .map(ResponseEntity::ok) = se Optional TEM valor:
                //   - pega o Fio
                //   - aplica ResponseEntity::ok nele
                // ResponseEntity::ok = referência de método (method reference)
                // é o mesmo que: fio -> ResponseEntity.ok(fio)
                .orElse(ResponseEntity.notFound().build());
        // .orElse(...) = senão (se Optional VAZIO):
        //   - executa essa parte
        // ResponseEntity.notFound() = cria resposta 404 Not Found
        // .build() = constrói a resposta (sem corpo)
    }

    // ========================================
    // BUSCAR FIO POR CÓDIGO
    // ========================================

    @GetMapping("/codigo/{codigo}")
    // GET /api/fios/codigo/{codigo}
    // exemplo: GET /api/fios/codigo/ABC123
    public ResponseEntity<Fio> buscarPorCodigo(@PathVariable String codigo) {
        // @PathVariable String codigo = pega {codigo} da URL

        Optional<Fio> fio = fioService.buscarPorCodigo(codigo);
        // busca pelo código único

        return fio.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        // mesma lógica: achou = 200 OK, não achou = 404
    }

    // ========================================
    // CRIAR NOVO FIO
    // ========================================

    @PostMapping (value = "novo")
    // @PostMapping = mapeia requisições HTTP POST
    // POST /api/fios
    // POST = usado pra CRIAR novos recursos
    public ResponseEntity<Fio> criar(@RequestBody Fio fio) {
        // @RequestBody = pega o JSON do corpo da requisição HTTP
        //   - Spring converte JSON automaticamente em objeto Fio
        // Fio fio = objeto criado a partir do JSON recebido

        if (fioService.codigoJaExiste(fio.getCodigo())) {
            // if = se
            // fioService.codigoJaExiste(...) = verifica se código já existe
            // fio.getCodigo() = pega o código do objeto
            // retorna true se já existe, false se não existe
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
            // ResponseEntity.status(...) = define status customizado
            // HttpStatus.CONFLICT = código 409 (conflito/duplicado)
            // .build() = constrói resposta sem corpo
        }

        Fio fioSalvo = fioService.salvar(fio);
        // fioService.salvar(fio) = valida e salva o fio no banco
        // retorna o fio salvo (com ID gerado pelo banco)
        // Fio fioSalvo = guarda o resultado

        return ResponseEntity.status(HttpStatus.CREATED).body(fioSalvo);
        // ResponseEntity.status(...) = define status
        // HttpStatus.CREATED = código 201 (criado com sucesso)
        // .body(fioSalvo) = adiciona o fio no corpo da resposta
    }

    // ========================================
    // ATUALIZAR FIO EXISTENTE (CORRIGIDO)
    // ========================================

    @PutMapping("/{id}")
    // @PutMapping = mapeia requisições HTTP PUT
    // PUT /api/fios/{id}
    // PUT = usado pra ATUALIZAR recurso completo
    public ResponseEntity<Fio> atualizar(@PathVariable Long id, @RequestBody Fio fioAtualizado) {
        // @PathVariable Long id = ID vem da URL
        // @RequestBody Fio fioAtualizado = dados novos vêm do JSON do corpo

        try {
            // try = tenta executar esse bloco
            // se der erro, vai pro catch
            Fio fioSalvo = fioService.atualizar(id, fioAtualizado);
            // chama o método atualizar do service
            // service busca o fio existente e atualiza os campos
            // retorna o fio atualizado

            return ResponseEntity.ok(fioSalvo);
            // retorna 200 OK + fio atualizado no corpo

        } catch (RuntimeException e) {
            // catch = captura exceções/erros
            // RuntimeException = tipo de erro
            // e = variável que contém os detalhes do erro
            // se o service lançar erro (ex: ID não encontrado)
            return ResponseEntity.notFound().build();
            // retorna 404 Not Found
        }
    }

    // ========================================
    // DELETAR FIO
    // ========================================

    @DeleteMapping("/{id}")
    // @DeleteMapping = mapeia requisições HTTP DELETE
    // DELETE /api/fios/{id}
    // DELETE = usado pra DELETAR recursos
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // ResponseEntity<Void> = resposta SEM corpo
        // Void = vazio (tipo que não retorna nada)
        // @PathVariable Long id = ID da URL

        if (fioService.buscarPorId(id).isEmpty()) {
            // busca o fio pelo ID
            // .isEmpty() = retorna true se Optional vazio (não achou)
            return ResponseEntity.notFound().build();
            // retorna 404 Not Found
        }

        fioService.deletar(id);
        // chama o service pra deletar do banco
        // void = não retorna nada

        return ResponseEntity.noContent().build();
        // ResponseEntity.noContent() = código 204 No Content
        // significa: operação bem-sucedida, mas sem corpo na resposta
        // .build() = constrói a resposta
    }

    // ========================================
    // ATUALIZAR QUANTIDADE (ESTOQUE)
    // ========================================

    @PatchMapping("/{id}/quantidade")
    // @PatchMapping = mapeia requisições HTTP PATCH
    // PATCH = usado pra atualização PARCIAL (só alguns campos)
    // /api/fios/{id}/quantidade
    // exemplo: PATCH /api/fios/5/quantidade?quantidade=100
    public ResponseEntity<Fio> atualizarQuantidade(
            @PathVariable Long id,
            // @PathVariable = pega {id} da URL
            // Long id = parâmetro que recebe o ID

            @RequestParam Double quantidade
            // @RequestParam = pega parâmetro da query string
            // query string = parte depois do ? na URL
            // exemplo: ?quantidade=100
            // Double quantidade = valor recebido
    ) {
        try {
            // try = tenta executar
            Fio fioAtualizado = fioService.atualizarQuantidade(id, quantidade);
            // chama service pra atualizar só a quantidade
            // service faz: quantidade_atual + quantidade_recebida
            // valida se não fica negativo

            return ResponseEntity.ok(fioAtualizado);
            // retorna 200 OK + fio atualizado

        } catch (RuntimeException e) {
            // catch = captura erros
            // ex: ID não existe, quantidade insuficiente
            return ResponseEntity.badRequest().build();
            // ResponseEntity.badRequest() = código 400 Bad Request
            // significa: requisição inválida/erro nos dados
        }
    }
}