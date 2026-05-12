📚 Gerenciador de Biblioteca Pessoal
Projeto desenvolvido em Java com Spring Boot para gerenciamento de uma biblioteca pessoal.

🚀 Tecnologias Utilizadas
Java
Spring Boot
MongoDB
Docker
Spring Security
HTML
CSS

📁 Estrutura do Projeto
src
 ┣ config
 ┣ controller
 ┣ model
 ┣ repository
 ┣ service
 ┣ resources
 ┃ ┣ static
 ┃ ┃ ┣ cadastro.html
 ┃ ┃ ┣ login.html
 ┃ ┃ ┣ index.html
 ┃ ┃ ┗ style.css

⚙️ Funcionalidades Implementadas
👤 Usuários
Cadastro de usuários
Login

📚 Livros
Cadastro de livros
Listagem de livros
Edição de livros
Exclusão de livros

🐳 MongoDB com Docker
O MongoDB está sendo executado com Docker.
Executar container MongoDB
docker run -d ^
--name mongodb ^
-p 27017:27017 ^
mongo

🛢️ Configuração do Banco
Arquivo application.properties:
spring.data.mongodb.uri=mongodb://localhost:27017/biblioteca

▶️ Como Executar o Projeto
Clonar o repositório
git clone https://github.com/rangelisa/biblioteca_pessoal.git
Rodar aplicação: ./mvnw spring-boot:run

🌐 Acesso
http://localhost:8080/index.html
