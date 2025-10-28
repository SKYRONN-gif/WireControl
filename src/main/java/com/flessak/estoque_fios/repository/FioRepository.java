package com.flessak.estoque_fios.repository;
// package = pacote/pasta onde fica o código
// repository = pasta específica pra classes de acesso ao banco

import com.flessak.estoque_fios.model.Fio;
// import = importar/trazer
// com.flessak.estoque_fios.model.Fio = traz a classe Fio que você criou

import org.springframework.data.jpa.repository.JpaRepository;
// org.springframework = biblioteca do Spring Framework
// data.jpa.repository = parte do Spring Data JPA
// JpaRepository = interface que JÁ VEM PRONTA com métodos de banco

import org.springframework.stereotype.Repository;
// stereotype = estereótipo/tipo de componente
// Repository = anotação que marca "isso é um repositório"

import java.util.Optional;
// java.util = pacote utilitário do Java
// Optional = classe que representa "pode ter valor ou não"
// tipo uma caixinha que pode estar vazia ou cheia

@Repository
// @ = anotação
// Repository = avisa o Spring "essa classe acessa banco de dados"
// Spring vai gerenciar essa classe automaticamente

public interface FioRepository extends JpaRepository<Fio, Long> {
// public = todo mundo pode usar
// interface = tipo especial de "classe" que só declara métodos
// FioRepository = nome da interface
// extends = "herda de" / "extende"
// JpaRepository = interface pai que já tem métodos prontos
// <Fio, Long> = genéricos (tipos específicos):
//   - Fio = tipo da entidade que essa interface gerencia
//   - Long = tipo do ID da entidade

    Optional<Fio> findByCodigo(String codigo);
    // Optional<Fio> = tipo de retorno
    //   - Optional = pode retornar um Fio ou nada (vazio)
    //   - <Fio> = quando encontrar, retorna um objeto Fio
    // findByCodigo = nome do método
    //   - find = procurar/buscar
    //   - By = "por"/"usando"
    //   - Codigo = campo da entidade Fio
    // (String codigo) = parâmetro que recebe o código pra buscar
    // ; = termina a declaração (sem corpo porque é interface)

    // SPRING FAZ MÁGICA: lê "findByCodigo" e cria automaticamente:
    // SELECT * FROM fios WHERE codigo = ?

    Optional<Fio> findByDescricao(String name);
    // Optional<Fio> = pode retornar Fio ou vazio
    // findByDescricao = busca pela descrição
    //   - find = buscar
    //   - By = por
    //   - Descricao = campo "descricao" da entidade Fio
    // (String name) = parâmetro (nome poderia ser qualquer coisa)
    //   - name = só o nome da variável (podia ser "desc", "texto", etc)

    // SPRING FAZ MÁGICA: lê "findByDescricao" e cria:
    // SELECT * FROM fios WHERE descricao = ?
}