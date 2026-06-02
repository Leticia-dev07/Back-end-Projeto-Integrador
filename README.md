# 📚 Back-end — Projeto Integrador (SENAC)

## 👥 Equipe

| Nome |
|---|
| André Costa |
| Caio Victor |
| Leticia Gabrielle |
| Luciana |
| Priscila |

**Disciplina:** Desenvolvimento Mobile  
**Professor:** Geraldo Gomes

---

API REST desenvolvida com **Spring Boot** para gerenciamento de alunos, cursos, submissões e certificados. O sistema conta com autenticação JWT, controle de acesso por perfis, upload de arquivos no Supabase (S3) e envio de e-mails automáticos.

---

## 🛠️ Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.5 |
| Spring Security | (incluso no Boot) |
| Spring Data JPA | (incluso no Boot) |
| MySQL | 8+ |
| JWT (Auth0) | 4.4.0 |
| Spring Mail | (incluso no Boot) |
| AWS SDK S3 (Supabase) | (incluso no Boot) |
| Spring Actuator | (incluso no Boot) |
| SpringDoc OpenAPI (Swagger) | (incluso no Boot) |
| Maven | Wrapper incluso |

---

## 📋 Pré-requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

- **Java 25** (ou versão compatível)
- **Maven** (ou use o wrapper `./mvnw` incluso no projeto)
- **MySQL 8+** rodando localmente

---

## ⚙️ Configuração do Banco de Dados

1. Acesse o MySQL e crie o banco de dados:

```sql
CREATE DATABASE pi;
```

2. O arquivo `src/main/resources/application.properties` usa variáveis de ambiente. Configure-as antes de rodar:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/pi
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=root
```

> ⚠️ O JPA está configurado com `ddl-auto=update`, então as tabelas serão **criadas automaticamente** na primeira execução.

---

## 🔑 Variáveis de Ambiente

Todas as configurações sensíveis são injetadas via variáveis de ambiente. Configure todas antes de iniciar:

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL do banco MySQL (ex: `jdbc:mysql://localhost:3306/pi`) |
| `SPRING_DATASOURCE_USERNAME` | Usuário do MySQL |
| `SPRING_DATASOURCE_PASSWORD` | Senha do MySQL |
| `JWT_SECRET` | Chave secreta para assinar os tokens JWT |
| `MAIL_USERNAME` | E-mail Gmail para envio de notificações |
| `MAIL_PASSWORD` | Senha de app do Gmail (não a senha normal) |
| `S3_URL` | Endpoint do Supabase Storage (ex: `https://<project>.supabase.co/storage/v1/s3`) |
| `S3_ACCESS_KEY` | Chave de acesso S3 do Supabase |
| `S3_SECRET_KEY` | Chave secreta S3 do Supabase |
| `S3_PROJECT_ID` | ID do projeto no Supabase |

---

## ▶️ Como Rodar

### Opção 1 — Maven Wrapper (recomendado, sem instalar Maven)

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Opção 2 — Maven instalado globalmente

```bash
mvn spring-boot:run
```

### Opção 3 — Gerar o JAR e executar

```bash
./mvnw clean package
java -jar target/pi-0.0.1-SNAPSHOT.jar
```

### Opção 4 — Docker

```bash
docker build -t pi-backend .
docker run -p 8080:8080 --env-file .env pi-backend
```

A aplicação sobe em: **`http://localhost:8080`**

---

## 🔐 Autenticação

O sistema usa **JWT (JSON Web Token)**. Para acessar os endpoints protegidos, é necessário fazer login primeiro.

### Login

**`POST /auth/login`** — Público (não requer token)

```json
{
  "email": "usuario@email.com",
  "password": "suaSenha"
}
```

**Resposta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "ALUNO"
}
```

O token JWT contém os claims: `sub` (e-mail), `role`, `userId` e expira em **2 horas** (fuso de Brasília).

Inclua o token em todas as requisições subsequentes no header:

```
Authorization: Bearer <token>
```

### Perfis de Acesso (Roles)

| Role | Descrição |
|---|---|
| `ADMIN` | Acesso total |
| `COORDENADOR` | Gerencia alunos, cursos e aprova/rejeita submissões |
| `ALUNO` | Envia submissões e consulta seus dados |

---

## 📡 Endpoints da API

### 🔑 Autenticação
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/auth/login` | Público | Retorna token JWT e role |

### 👨‍🎓 Alunos (`/alunos`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/alunos` | ADMIN, COORDENADOR, ALUNO | Lista todos os alunos |
| GET | `/alunos/{id}` | ADMIN, COORDENADOR, ALUNO | Busca aluno por ID |
| GET | `/alunos/curso/{cursoId}` | ADMIN, COORDENADOR, ALUNO | Lista alunos de um curso |
| POST | `/alunos` | ADMIN, COORDENADOR | Cadastra novo aluno sem vínculo de curso |
| POST | `/alunos/curso/{cursoId}` | ADMIN, COORDENADOR | Cadastra aluno e vincula a um curso (ou rematricula se já existir) |
| POST | `/alunos/{alunoId}/cursos/{cursoId}` | ADMIN, COORDENADOR | Matricula aluno existente em um curso |
| PUT | `/alunos/{id}` | Autenticado | Atualiza dados do aluno |
| DELETE | `/alunos/{id}` | ADMIN | Remove aluno |
| DELETE | `/alunos/{alunoId}/cursos/{cursoId}` | ADMIN, COORDENADOR | Remove vínculo do aluno com um curso |

> ⚠️ **Regra de duplicidade**: ao cadastrar via `/alunos/curso/{cursoId}`, se o e-mail já existir no banco, o sistema apenas rematricula o aluno no novo curso sem criar um novo registro.

### 📖 Cursos (`/cursos`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/cursos` | Autenticado | Lista todos os cursos |
| GET | `/cursos/{id}` | Autenticado | Busca curso por ID |
| POST | `/cursos` | Autenticado | Cria novo curso |
| PUT | `/cursos/{id}` | Autenticado | Atualiza curso |
| DELETE | `/cursos/{id}` | Autenticado | Remove curso |

### 🏷️ Categorias (`/categorias`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/categorias` | Autenticado | Lista todas as categorias (aceita `?cursoId=` para filtrar) |
| GET | `/categorias/{id}` | Autenticado | Busca categoria por ID |
| POST | `/categorias` | Autenticado | Cria nova categoria |
| POST | `/categorias/curso/{cursoId}` | Autenticado | Cria categoria já vinculada a um curso |
| PUT | `/categorias/{id}` | Autenticado | Atualiza categoria |
| DELETE | `/categorias/{id}` | Autenticado | Remove categoria |

### 👩‍💼 Coordenadores (`/coordenadores`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/coordenadores` | ADMIN, COORDENADOR | Lista todos os coordenadores |
| GET | `/coordenadores/{id}` | ADMIN, COORDENADOR | Busca coordenador por ID |
| POST | `/coordenadores` | ADMIN | Cadastra novo coordenador |
| POST | `/coordenadores/{coordId}/cursos/{cursoId}` | ADMIN | Vincula coordenador a um curso |
| PUT | `/coordenadores/{id}` | ADMIN | Atualiza coordenador |
| DELETE | `/coordenadores/{id}` | ADMIN | Remove coordenador |
| DELETE | `/coordenadores/{coordId}/cursos/{cursoId}` | ADMIN | Desvincula coordenador de um curso |

### 📝 Submissões (`/submissoes`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/submissoes` | Autenticado | Lista todas as submissões (aceita `?cursoId=` para filtrar por curso) |
| GET | `/submissoes/{id}` | Autenticado | Busca submissão por ID |
| POST | `/submissoes` | Autenticado | Cria submissão com upload de arquivo (`multipart/form-data`) |
| PUT | `/submissoes/{id}/aprovar` | Autenticado | Aprova submissão e acumula horas no aluno |
| PUT | `/submissoes/{id}/rejeitar` | Autenticado | Rejeita submissão com observação |

> 📎 **Upload**: envie o campo `submissao` (JSON) e o campo `file` (arquivo) como `multipart/form-data`. O arquivo é armazenado no **Supabase Storage** e a URL pública é salva no banco.

> 📅 **Regra de semestre**: o sistema valida automaticamente o limite de submissões por categoria por semestre (1º: jan–jun / 2º: jul–dez) com base no fuso de Brasília.

> 📧 **E-mails automáticos**: ao aprovar ou rejeitar, um e-mail é enviado ao aluno em segundo plano (assíncrono).

### 🎓 Certificados (`/certificados`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/certificados` | Autenticado | Lista todos os certificados |
| GET | `/certificados/{id}` | Autenticado | Busca certificado por ID |

> Os arquivos de certificado ficam acessíveis publicamente via `/certificados/**` (sem autenticação).

### 🛡️ Super Admins (`/admins`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/admins` | Autenticado | Lista todos os admins |
| GET | `/admins/{id}` | Autenticado | Busca admin por ID |
| POST | `/admins` | Autenticado | Cadastra novo admin |
| PUT | `/admins/{id}` | Autenticado | Atualiza admin |
| DELETE | `/admins/{id}` | Autenticado | Remove admin |

### 👥 Usuários (`/users`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/users` | Autenticado | Lista todos os usuários |
| GET | `/users/{id}` | Autenticado | Busca usuário por ID |

### 📧 Notificações de E-mail (`/notificacaoEmail`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/notificacaoEmail` | Autenticado | Lista todas as notificações |
| GET | `/notificacaoEmail/{id}` | Autenticado | Busca notificação por ID |

---

## ☁️ Armazenamento de Arquivos (Supabase S3)

O upload de certificados usa o **Supabase Storage** via protocolo S3 compatível:

- Os arquivos são enviados com nome UUID gerado automaticamente
- O bucket utilizado é `certificados`
- A URL pública segue o padrão: `https://<project-id>.supabase.co/storage/v1/object/public/certificados/<arquivo>`
- O campo `urlArquivo` no `SubmissaoDTO` contém a URL direta para exibição no front-end

---

## 📊 Monitoramento

O Spring Actuator está habilitado para monitoramento (usado no deploy no Render):

| Endpoint | Descrição |
|---|---|
| `GET /actuator/health` | Status da aplicação |
| `GET /actuator/info` | Informações gerais |

---

## 📖 Documentação da API (Swagger)

A API usa **SpringDoc OpenAPI** (`springdoc-openapi-starter-webmvc-ui`) para gerar a documentação interativa automaticamente.

Com a aplicação rodando, acesse pelos links:

| Interface | URL |
|---|---|
| Swagger UI (interface visual) | `http://localhost:8080/swagger-ui.html` |
| JSON da especificação OpenAPI | `http://localhost:8080/v3/api-docs` |

> Ambas as rotas são **públicas** — não precisam de token para acessar.

### Como usar o Swagger com autenticação

1. Acesse `http://localhost:8080/swagger-ui.html`
2. Clique em **Authorize** (ícone de cadeado 🔒 no topo da página)
3. No campo `bearerAuth`, cole o token JWT obtido no `POST /auth/login` (sem o prefixo `Bearer`)
4. Clique em **Authorize** e feche o modal
5. Agora todos os endpoints autenticados podem ser testados diretamente pelo Swagger

### Detalhes da configuração

- **Título**: API Projeto Integrador - Senac
- **Versão**: 1.0
- **Esquema de segurança**: Bearer JWT (HTTP)
- O Swagger já vem configurado com suporte a token JWT via `bearerAuth` definido no `OpenApiConfig.java`

---

## 🌐 CORS

A API permite requisições de **qualquer origem** (`*`) com credenciais habilitadas. Métodos permitidos: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`.

---

## 📁 Estrutura do Projeto

```
src/main/java/com/senac/pi/
├── config/          # Segurança (JWT, CORS, BCrypt), S3 e Web
├── DTO/             # Objetos de transferência de dados
├── entities/        # Entidades JPA (modelos do banco)
│   └── enums/       # UserRole (ALUNO, COORDENADOR, ADMIN) e StatusSubmissao (PENDENTE, APROVADO, REJEITADO)
├── repositories/    # Interfaces de acesso ao banco (Spring Data)
├── resources/       # Controllers (endpoints REST)
├── services/        # Regras de negócio, upload S3 e envio de e-mail
└── PiApplication.java
```

---

## ⚠️ Observações

- Senhas são armazenadas com **BCrypt** — nunca em texto puro.
- O campo `urlArquivo` no `SubmissaoDTO` substitui o antigo endpoint `/arquivo`. Use-o diretamente no front-end para exibir o certificado.
- O envio de e-mails é **assíncrono** — falhas no SMTP não bloqueiam a aprovação/rejeição da submissão.
- Logs são salvos em `logs/projeto_integrador.log` e exibidos no nível `DEBUG` para o pacote `com.senac.pi`.
- Limite de upload: **10MB** por arquivo.
