# Matriz de Rastreabilidade de Requisitos (RTM)

Abaixo está o mapeamento dos requisitos da aplicação com os casos de teste implementados para assegurar a cobertura de código e regras de negócio.

| ID Req | Descrição do Requisito | ID Caso de Teste | Tipo de Teste | Status | Classe de Teste Mapeada |
| :--- | :--- | :--- | :--- | :--- | :--- |
| REQ-01 | O sistema deve permitir o cadastro de um novo usuário | CT-01 | Teste de Integração (Caixa Preta) | ✅ Realizado | `UsuarioControllerTest` |
| REQ-02 | O sistema não deve cadastrar usuário com email já existente | CT-02 | Teste Parametrizado (Caixa Branca)| ✅ Realizado | `UsuarioServiceTest` |
| REQ-03 | O sistema deve validar regras de criação de Livro (não permitir ISBN duplicado) | CT-03 | Teste Unitário (Caixa Branca) | ✅ Realizado | `LivroServiceTest` |
| REQ-04 | O sistema deve permitir a busca de livros por múltiplos critérios (título, autor, isbn) | CT-04 | Teste Parametrizado (Caixa Branca)| ✅ Realizado | `LivroServiceTest` |
| REQ-05 | O sistema deve realizar operações de CRUD completas na API de Livros usando MongoDB real | CT-05 | Teste de Integração (Caixa Preta) | ✅ Realizado | `LivroControllerTest` |
| REQ-06 | O sistema não deve expor a senha do usuário após o cadastro com sucesso | CT-06 | Teste Unitário (Caixa Branca) | ✅ Realizado | `UsuarioServiceTest` / `UsuarioControllerTest` |
| REQ-07 | O sistema deve garantir resiliência e suporte para simulações de API Externa | CT-07 | Teste de Configuração (VCR/WireMock)| ✅ Realizado | `WireMockVcrTest` |

---
**Notas sobre a Qualidade:**
- **Cobertura Geral:** Acima de 80% (verificado via JaCoCo no Maven Pipeline).
- **Caixa Preta:** Utilizado `TestRestTemplate` combinado com MongoDB via `Testcontainers`.
- **Caixa Branca:** Utilizado JUnit 5 e `Mockito` cobrindo fluxos de sucesso e exceção (`IllegalArgumentException` e `UsernameNotFoundException`).
- **CI/CD:** Configurado com GitHub Actions (`.github/workflows/ci.yml`).