package com.flessak.estoque_fios.model;
// package = "pacote", organiza as classes do projeto em pastas/grupos
// com.flessak.estoque_fios.model = caminho/endereço onde essa classe fica

import jakarta.persistence.*;
// import = "importar", traz código de outras bibliotecas
// jakarta.persistence.* = biblioteca do JPA (padrão Java pra banco de dados)
// * = importa TUDO dessa biblioteca

@Entity
// @ = anotação (instruções especiais pro Spring/Hibernate)
// Entity = diz "essa classe é uma ENTIDADE do banco de dados"

@Table(name = "fios")
// Table = configura a tabela no banco
// name = nome da tabela
// "fios" = a tabela vai se chamar "fios" no PostgreSQL

public class Fio {
// public = qualquer classe pode usar essa classe
// class = palavra-chave que declara uma classe
// Fio = nome da classe (sempre começa com maiúscula)
// { = abre o corpo da classe

    @Id
    // Id = marca esse campo como CHAVE PRIMÁRIA (identificador único)

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // GeneratedValue = "valor gerado automaticamente"
    // strategy = estratégia/forma de gerar
    // GenerationType.IDENTITY = deixa o banco gerar (auto_increment)

    private Long id;
    // private = só essa classe acessa diretamente
    // Long = tipo de dado (número inteiro grande)
    // id = nome da variável
    // ; = termina a instrução

    @Column(nullable = false, unique = true)
    // Column = configura a coluna no banco
    // nullable = pode ser nulo/vazio?
    // false = NÃO pode ser nulo
    // unique = valor único?
    // true = SIM, não pode repetir na tabela

    private String codigo;
    // String = tipo texto
    // codigo = nome do campo

    @Column(nullable = false)
    private String descricao;
    // descricao = campo texto obrigatório

    @Column(nullable = false)
    private String bitola;
    // bitola = campo texto obrigatório

    @Column(nullable = false)
    private Double quantidade;
    // Double = número decimal (com vírgula)
    // quantidade = campo numérico obrigatório

    public Fio() {}
    // public = qualquer um pode chamar
    // Fio() = construtor vazio (sem parâmetros)
    // {} = corpo vazio
    // OBRIGATÓRIO pro JPA criar objetos

    public Fio(String codigo, String descricao, String bitola, Double quantidade){
        // construtor COM parâmetros (quando VOCÊ cria um Fio)
        // (String codigo, ...) = recebe esses valores
        this.codigo = codigo;
        // this.codigo = o campo da classe
        // = = atribui o valor
        // codigo = parâmetro recebido

        this.descricao = descricao;
        this.bitola = bitola;
        this.quantidade = quantidade;
    }

    public Long getId(){
        // public = qualquer um pode chamar
        // Long = tipo que o método RETORNA
        // getId = nome do método (getter = pegar valor)
        // () = sem parâmetros
        return id;
        // return = devolve/retorna
        // id = valor do campo
    }

    public void setId(Long id){
        // void = NÃO retorna nada
        // setId = setter (define/altera valor)
        // (Long id) = recebe um Long chamado id
        this.id = id;
        // this.id = campo da classe
        // id = parâmetro recebido
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getBitola() {
        return bitola;
    }

    public void setBitola(String bitola) {
        this.bitola = bitola;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }


    // Os outros getters e setters seguem a MESMA lógica:
    // get = pega o valor
    // set = define o valor

    @Override
    // Override = "sobrescrever" um método que já existe na classe Object

    public String toString() {
        // toString = converte o objeto em texto (pra imprimir)
        return "Fio {"+
                // return = retorna uma String montada
                // + = concatena (junta) textos
                "id=" + id +
                ", codigo=" + codigo + "'" +
                // \' = aspas simples (caractere especial)
                ", descricao=" + descricao + "'" +
                ", bitola=" + bitola + "'" +
                ", quantidade=" + quantidade +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        // equals = compara se dois objetos são iguais
        // boolean = retorna true ou false
        // Object o = recebe qualquer objeto pra comparar
        if (this == o) return true;
        // if = se
        // this == o = se for o MESMO objeto na memória
        // return true = são iguais

        if (o == null || getClass() != o.getClass()) return false;
        // || = ou
        // o == null = se o objeto for nulo
        // getClass() = pega a classe do objeto
        // != = diferente
        // return false = são diferentes

        Fio fio = (Fio) o;
        // (Fio) = cast (converte) o objeto pra tipo Fio
        // Fio fio = cria variável local

        return codigo.equals(fio.codigo);
        // compara os CÓDIGOS dos dois fios
        // se os códigos forem iguais, os fios são iguais
    }

    @Override
    public int hashCode() {
        // hashCode = gera um número único pro objeto
        // int = retorna número inteiro
        // usado em coleções como HashMap, HashSet
        return codigo.hashCode();
        // usa o hashCode do código
    }
}