# WireControl

Sistema web de controle de estoque de fios elétricos. Permite cadastrar, consultar, editar, excluir e ajustar a quantidade de fios em estoque por código, descrição e bitola.

> ⚠️ **Projeto de estudo.** O deploy na Vercel não está funcional (veja [Limitações conhecidas](#limitações-conhecidas)). Para testar, rode localmente.

## Funcionalidades

- Cadastro de fios com código único, descrição, bitola e quantidade
- Listagem e busca por ID ou código
- Edição e exclusão de registros
- Ajuste rápido de quantidade (entrada/saída de estoque)
- API REST consumida por um frontend em HTML, CSS e JavaScript puro

## Tecnologias

| Camada    | Stack |
|-----------|-------|
| Backend   | Java 17, Spring Boot 3.5, Spring Web, Spring Data JPA |
| Banco     | PostgreSQL |
| Frontend  | HTML, CSS, JavaScript (Fetch API) |
| Build     | Maven (wrapper incluso) |

## Estrutura

```
WireControl/
├── src/main/java/com/flessak/estoque_fios/
│   ├── controller/   # Endpoints REST
│   ├── service/      # Regras de negócio
│   ├── repository/   # Acesso ao banco (JPA)
│   ├── model/        # Entidades (Fio, Estoque)
│   └── config/       # Configuração de CORS
├── src/main/resources/application.properties
└── frontend/         # index.html, style.css, script.js
```

## Endpoints da API

Base: `/api/fios`

| Método | Rota                     | Descrição                    |
|--------|--------------------------|------------------------------|
| GET    | `/api/fios`              | Lista todos os fios          |
| GET    | `/api/fios/{id}`         | Busca por ID                 |
| GET    | `/api/fios/codigo/{cod}` | Busca por código             |
| POST   | `/api/fios/novo`         | Cadastra um novo fio         |
| PUT    | `/api/fios/{id}`         | Atualiza um fio              |
| DELETE | `/api/fios/{id}`         | Remove um fio                |
| PATCH  | `/api/fios/{id}/quantidade` | Ajusta a quantidade       |

Exemplo de corpo (POST/PUT):

```json
{
  "codigo": "F-2.5-AZ",
  "descricao": "Cabo flexível azul",
  "bitola": "2,5 mm²",
  "quantidade": 100
}
```

## Como rodar localmente

**Pré-requisitos:** Java 17+ e PostgreSQL rodando.

1. Crie o banco:
   ```sql
   CREATE DATABASE estoque_fios;
   ```
2. Ajuste usuário e senha do banco em `src/main/resources/application.properties`.
3. Suba o backend:
   ```bash
   ./mvnw spring-boot:run        # Linux/macOS
   mvnw.cmd spring-boot:run      # Windows
   ```
   A API fica em `http://localhost:8080/api/fios`. As tabelas são criadas automaticamente pelo Hibernate.
4. Abra o `frontend/index.html` no navegador (ou use a extensão Live Server do VS Code).

## Limitações conhecidas

Pontos identificados em revisão e ainda não corrigidos:

- **Deploy incompleto:** a Vercel hospeda só arquivos estáticos e não executa o backend Spring Boot. Além disso, o `index.html` está em `/frontend`, então o deploy precisaria usar essa pasta como Root Directory.
- **URL da API fixa:** o `script.js` aponta para `http://localhost:8080`, então o frontend só funciona com o backend rodando na mesma máquina.
- **Rota de cadastro divergente:** o backend expõe `POST /api/fios/novo`, mas o frontend envia para `POST /api/fios`. Para o cadastro funcionar pela interface, um dos dois precisa ser ajustado.
- **Credenciais no código:** o `application.properties` foi versionado com uma senha do banco. Ela já foi alterada e não é mais válida; o ideal seria ler as credenciais de variáveis de ambiente.
- **Classe `EstoqueController` vazia**, sem endpoints implementados.

## Autor

Feito por **Nahan Miguel Barbosa** · [GitHub](https://github.com/SKYRONN-gif)
