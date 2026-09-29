# Help Desk TI — Back-end

API RESTful desenvolvida em Java com Spring Boot para suportar a plataforma de Help Desk TI. Responsável pela autenticação de utilizadores, regras de negócio e gestão de chamados no banco de dados.

🌐 **Aplicação Front-end Conectada:**  
[Help Desk TI — Web App](https://projeto-manu-front-production.up.railway.app/login)

---

## 🛠️ Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot 3**
- **Spring Security + JWT** (Autenticação e Autorização)
- **Spring Data JPA** (Persistência de Dados)
- **PostgreSQL / MySQL** (Banco de Dados)
- **Lombok**

---

## 🚀 Endpoints Principais

### Autenticação (`/auth`)
- `POST /auth/login` — Autentica o utilizador e retorna o Token JWT e os dados de perfil.

### Chamados (`/tickets`)
- `POST /tickets` — Regista um novo chamado (espera DTO em formato JSON).
- `GET /tickets` — Retorna a lista de chamados registados.

---

## 📦 Como Executar o Projeto Localmente

1. **Clonar o repositório:**
   ```bash
   git clone [https://github.com/AlvaroCoe/projeto-manu-back.git](https://github.com/AlvaroCoe/projeto-manu-back.git)
   cd projeto-manu-back
