# Projeto API de Wishlist
## Descrição
API RESTful para gerenciamento de Wishlist de clientes, desenvolvida com Spring Boot e MongoDB.
## Tecnologias Utilizadas
- Java 17
- Spring Boot
- Spring Data MongoDB
- Jakarta EE
- Lombok
- Docker e Docker Compose
- Gradle
- SonarQube
- JaCoCo (cobertura de testes)
- Swagger (documentação da API)

## Configuração e Execução
### Pré-requisitos
- Docker e Docker Compose instalados
- Java 17

### Passo a passo para execução
#### 1. Configuração do SonarQube
1. Inicie o container do SonarQube:
``` 
   docker run --name sonarqube -p 9000:9000 sonarqube:community
```
1. Após a inicialização do container, acesse a interface web do SonarQube:
``` 
   http://host.docker.internal:9000/
```
1. Faça login com as credenciais padrão:
   - Usuário: `admin`
   - Senha: `admin`

Siga as instruções para alterar a senha padrão por uma mais segura.

2. Configure um novo projeto:
   - Clique em "Create Project"
   - Dê o nome "wishlist" ao projeto
   - Use as configurações globais padrão

3. Configure o método de análise:
   - Selecione "Locally"
   - Gere um token de acesso
   - Guarde o token gerado para uso posterior

4. Adicione o token ao arquivo na raiz do projeto: `gradle.properties`
``` properties
   systemProp.sonar.token=seu_token_gerado
```
Exemplo:
``` properties
   systemProp.sonar.token=sqp_230accfcbef8ad975899107b284738a59e4334f5
```
#### 2. Execução da aplicação
1. Inicie o ambiente completo com Docker Compose:
```
   docker compose up -d
```
Isso iniciará:
- MongoDB
- A aplicação Wishlist API

1. Após a inicialização, você pode acessar a documentação da API através do Swagger:
``` 
   http://localhost:8080/swagger-ui/index.html#/
```
## Endpoints da API
A API oferece os seguintes endpoints para gerenciamento da wishlist:
- `POST /api/wishlist/{customerId}`: Adiciona produtos à wishlist do cliente
- `DELETE /api/wishlist/{customerId}/{productId}`: Remove um produto da wishlist
- `GET /api/wishlist/{customerId}`: Obtém todos os produtos da wishlist do cliente
- `GET /api/wishlist/{customerId}/{productId}`: Verifica se um produto específico está na wishlist

## Regras de Negócio
- Cada cliente pode ter no máximo 20 produtos em sua wishlist
- Não é possível adicionar produtos duplicados na mesma wishlist
- Quando o limite é excedido, a operação de adição falha por completo

## Análise de Qualidade de Código
Para executar a análise de qualidade no SonarQube:
   ``` 
   ./gradlew sonarqube
   ```
Para executar os testes e gerar relatórios de cobertura:
   ``` 
   ./gradlew test jacocoTestReport
   ```
## Desenvolvimento
### Estrutura do Projeto
O projeto segue uma arquitetura em camadas:
- **Controller**: Endpoints REST
- **Service**: Lógica de negócios
- **Repository**: Acesso ao MongoDB
- **Model**: Entidades e DTOs

### Modelo de Dados

#### CustomerWishlistEntity
A principal entidade do sistema é `CustomerWishlistEntity`, que armazena:
- `customerId` (UUID): Identificador único do cliente (chave primária)
- `wishlist` (List<ProductEntity>): Lista de produtos adicionados pelo cliente

Esta entidade é persistida no MongoDB na coleção `customer_wishlist`.

#### ProductEntity
A entidade `ProductEntity` representa os produtos que podem ser adicionados à wishlist:
- `productId` (UUID): Identificador único do produto
- `name` (String): Nome do produto
- `description` (String): Descrição detalhada do produto
- `price` (Long): Preço do produto em centavos