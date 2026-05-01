# 📚 Back-end — Projeto Integrador (SENAC)

API REST desenvolvida com **Spring Boot** para gerenciamento de alunos, cursos, submissões e certificados. O sistema conta com autenticação JWT, controle de acesso por perfis e envio de e-mails automáticos.

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

2. O arquivo `src/main/resources/application.properties` já está configurado com as credenciais padrão:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pi
spring.datasource.username=root
spring.datasource.password=root
```

> ⚠️ Se suas credenciais do MySQL forem diferentes, edite o `application.properties` antes de iniciar.

O JPA está configurado com `ddl-auto=update`, então as tabelas serão **criadas automaticamente** na primeira execução.

---

## ▶️ Como Rodar

### Opção 1 — Maven Wrapper (recomendado, sem instalar Maven)

No terminal, dentro da pasta do projeto:

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
  "role": "ADMIN"
}
```

Inclua o token em todas as requisições subsequentes no header:

```
Authorization: Bearer <token>
```

### Perfis de Acesso (Roles)

| Role | Descrição |
|---|---|
| `ADMIN` | Acesso total |
| `COORDENADOR` | Gerencia alunos e cursos |
| `ALUNO` | Acesso básico |

---

## 📡 Endpoints da API

### 🔑 Autenticação
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/auth/login` | Público | Retorna token JWT e role |

### 👨‍🎓 Alunos (`/alunos`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/alunos` | Autenticado | Lista todos os alunos |
| GET | `/alunos/{id}` | Autenticado | Busca aluno por ID |
| GET | `/alunos/curso/{cursoId}` | Autenticado | Lista alunos de um curso |
| POST | `/alunos` | ADMIN ou COORDENADOR | Cadastra novo aluno |
| POST | `/alunos/curso/{cursoId}` | ADMIN ou COORDENADOR | Cadastra aluno e vincula a um curso |
| POST | `/alunos/{alunoId}/cursos/{cursoId}` | ADMIN ou COORDENADOR | Matricula aluno existente em um curso |
| PUT | `/alunos/{id}` | Autenticado | Atualiza dados do aluno |
| DELETE | `/alunos/{id}` | Autenticado | Remove aluno |

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
| GET | `/categorias` | Autenticado | Lista todas as categorias |
| GET | `/categorias/{id}` | Autenticado | Busca categoria por ID |
| POST | `/categorias` | Autenticado | Cria nova categoria |
| POST | `/categorias/curso/{cursoId}` | Autenticado | Cria categoria vinculada a um curso |
| PUT | `/categorias/{id}` | Autenticado | Atualiza categoria |
| DELETE | `/categorias/{id}` | Autenticado | Remove categoria |

### 👩‍💼 Coordenadores (`/coordenadores`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/coordenadores` | Autenticado | Lista todos os coordenadores |
| GET | `/coordenadores/{id}` | Autenticado | Busca coordenador por ID |
| POST | `/coordenadores` | ADMIN | Cadastra novo coordenador |
| POST | `/coordenadores/{coordId}/cursos/{cursoId}` | ADMIN | Vincula coordenador a um curso |
| PUT | `/coordenadores/{id}` | ADMIN | Atualiza coordenador |
| DELETE | `/coordenadores/{id}` | ADMIN | Remove coordenador |

### 📝 Submissões (`/submissoes`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/submissoes` | Autenticado | Lista todas as submissões |
| GET | `/submissoes/{id}` | Autenticado | Busca submissão por ID |
| POST | `/submissoes` | Autenticado | Cria submissão com upload de arquivo (multipart) |
| PUT | `/submissoes/{id}/aprovar` | Autenticado | Aprova uma submissão |
| PUT | `/submissoes/{id}/rejeitar` | Autenticado | Rejeita uma submissão com observação |

### 🎓 Certificados (`/certificados`)
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/certificados` | Autenticado | Lista todos os certificados |
| GET | `/certificados/{id}` | Autenticado | Busca certificado por ID |

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

## 🌐 CORS

A API permite requisições das seguintes origens (configurado no `SecurityConfig`):

- `http://127.0.0.1:5500`
- `http://localhost:5500`

> Caso o front-end esteja em outro endereço, adicione a origem na lista em `SecurityConfig.java`.

---

## 📁 Estrutura do Projeto

```
src/main/java/com/senac/pi/
├── config/          # Configurações de segurança e filtros JWT
├── DTO/             # Objetos de transferência de dados
├── entities/        # Entidades JPA (modelos do banco)
│   └── enums/       # Enumerações (roles, status)
├── repositories/    # Interfaces de acesso ao banco (Spring Data)
├── resources/       # Controllers (endpoints REST)
├── services/        # Regras de negócio
└── PiApplication.java  # Classe principal
```

---

## 🔧 Variáveis de Ambiente (Opcional)

O segredo JWT pode ser sobrescrito via variável de ambiente:

```bash
export JWT_SECRET=minha-chave-secreta-super-segura
```

Se não for definida, o sistema usa o valor padrão configurado no `application.properties`.

---

## ⚠️ Observações

- O envio de e-mails está configurado com uma conta Gmail de desenvolvimento. Em produção, substitua as credenciais no `application.properties`.
- O upload de arquivos nas submissões usa `multipart/form-data` — envie o campo `submissao` (JSON) e o campo `file` (arquivo) separadamente.
