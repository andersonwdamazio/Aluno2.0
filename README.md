# Aluno2.0 — API de alunos com PostgreSQL

Esta versão continua o projeto do Trabalho I. Mantém o modelo `Aluno` (`id`, `nome`, `idade`, `curso`, `cidade`), as rotas CRUD e os filtros combináveis, agora com persistência via Spring Data JPA e PostgreSQL.

## Preparar o banco

Crie um banco chamado `alunosdb` no PostgreSQL, por exemplo:

```sql
CREATE DATABASE alunosdb;
```

A aplicação espera PostgreSQL em `localhost:5432`. Configure usuário e senha no ambiente antes de iniciar:

```powershell
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "sua_senha"
```

Opcionalmente, configure `DB_URL` se o endereço do banco for diferente. Os valores padrão estão em `src/main/resources/application.properties`. O Hibernate cria/atualiza a tabela `aluno` com `spring.jpa.hibernate.ddl-auto=update`.

## Executar

Abra esta pasta no IntelliJ e execute `MinhaApiApplication`, ou use o Maven Wrapper:

```powershell
./mvnw.cmd spring-boot:run
```

A API fica em `http://localhost:8080/alunos`.

## Rotas

| Método | Rota | Ação |
|---|---|---|
| GET | `/alunos` | Lista alunos; aceita `nome`, `curso` e `cidade` como filtros combináveis |
| GET | `/alunos/{id}` | Busca pelo ID |
| POST | `/alunos` | Cadastra; o PostgreSQL gera o ID |
| PUT | `/alunos/{id}` | Atualiza os campos do aluno |
| DELETE | `/alunos/{id}` | Exclui pelo ID |

Os filtros fazem busca parcial sem diferenciar maiúsculas/minúsculas ou acentos. Quando mais de um é informado, todos precisam corresponder. A filtragem continua no controller para preservar o comportamento do Trabalho I; os registros vêm de `AlunoRepository.findAll()` no PostgreSQL.

## Demonstrar persistência no Postman

1. Importe `postman/Aluno2.0.postman_collection.json` no Postman e inicie a aplicação.
2. Envie **Cadastrar aluno**. O JSON não contém `id`; a resposta `201 Created` deve mostrar o ID gerado.
3. Copie esse número para a variável `id` da coleção. Rode **Buscar por ID** ou **Listar alunos** para mostrar o registro.
4. Pare e inicie a aplicação novamente. Rode **Buscar por ID** ou **Listar alunos** de novo: o registro deve continuar presente.
5. Execute no pgAdmin ou `psql` a consulta de `sql/consultar_alunos.sql` para mostrar a mesma linha diretamente no banco.

## Modelo e repositório

`Aluno` é uma entidade JPA. `@Id` marca a chave primária e `@GeneratedValue(strategy = GenerationType.IDENTITY)` delega a geração do ID ao PostgreSQL. `AlunoRepository` estende `JpaRepository<Aluno, Integer>` e o controller usa `save`, `findAll`, `findById`, `existsById` e `deleteById`.
