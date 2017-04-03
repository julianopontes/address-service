# Address API

[![Circle CI](https://circleci.com/gh/julianopontes/address-service/tree/master.svg?style=shield&circle-token=420b5bf5b06bab9d49705d65200ee776d9a3ff25)](https://circleci.com/gh/julianopontes/address-service/tree/master)
[![Coverage Status](https://coveralls.io/repos/julianopontes/address-service/badge.svg?branch=master&service=github)](https://coveralls.io/github/julianopontes/address-service?branch=master)

## Estrutura do projeto

A aplicação foi elaborada utilzando padrão de arquitetura MVC _(Model View Controller)_, sendo dividida da seguinte forma:
- Model - Mapeamento de objetos do domínio _([domain](https://github.com/julianopontes/address-service/tree/master/src/main/java/com/jop/address/domain))_
- View - API REST _([controllers](https://github.com/julianopontes/address-service/tree/master/src/main/java/com/jop/address/controller))_
- Controller - Centralização da lógica de negócio _([service](https://github.com/julianopontes/address-service/tree/master/src/main/java/com/jop/address/service))_

Foi baseada na utilização do _Framework Spring_, utilizando os módulos:
- [Spring Boot](http://projects.spring.io/spring-boot/)
 - Fácil configuração da aplicação
 - Servidor web incorporado (Tomcat), facilitando a execução da aplicação
 - Gerenciamento da maioria das versões de dependências
- [Spring Framework](http://projects.spring.io/spring-framework/)
 - Injeção de dependências e Inverção de Controle (Padrões de desenvolvimento para baixo acoplamento de módulos da aplicação)
 - Geração da API REST
 - Gerenciamento de transações
 - Testes unitários
- [Spring Data JPA](http://projects.spring.io/spring-data-jpa/)
 - Repositório de acesso a dados utilizando a especificação _JPA_

### Testes
Foram desenvolvidos testes unitários para validação, testes e garantia de mudanças, prevenindo problemas e ajudando no desenvolvimento _([tests](https://github.com/julianopontes/address-service/tree/master/src/test/java/com/jop/address/controller))_

### Banco de dados
A aplicação pode ser executada utilizando 2 bancos de dados: [HSQLDB](http://hsqldb.org/) e [MySQL](https://www.mysql.com/)

Quando a aplicação é executada localmente utilizando _Java (java -jar)_, ou na execução dos testes, o banco de dados utilizado é o HSQLDB

Caso a aplicação seja executada através do _docker_ (descrito mais abaixo), o banco de dados utilizando é o MySQL

## Compilação e execução

### Ferramentas necessárias para compilação e execução:
- [Apache Maven](http://maven.apache.org/)
- [Oracle JDK 8](http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html)

###Empacotando o projeto:
Na pasta do projeto executar:
- `mvn clean package`

Será gerado o arquivo _**target/address-service.jar**_.

### Executando o projeto:
- `java -jar target/address-service.jar`

A API ficará disponível no endereço `http://localhost:8080`

### Testando a API:
Para testes da API é possível acessar o endereço: 
- `http://localhost:8080/swagger-ui.html`


## Docker (docker-compose)
Também é possível executar a aplicação utilizando docker-compose.

Neste formato de execuçãom a aplicação utiliza um banco de dados MySQL, através de um container docker.

Para ambientes de desenvolvimento é necessário a instalação das ferramentas:
- [Docker Machine](https://docs.docker.com/machine/)
- [Docker Compose](https://docs.docker.com/compose/) (não disponível para Windows)
- [VirtualBox](https://www.virtualbox.org/)

###Empacotando o projeto:
Na pasta do projeto executar:
- `mvn clean package`

### Executando o docker-compose:
- `docker-compose up`

Após esse processos, a aplicação ficará disponível no pela url:
- `http://{IP_VM_DOCKER}:8080`

\* Para obter o ip da VM:
- `docker-machine ip dev`

## Documentação da API
A documentação da API REST desenvolvida foi disponibilizada utilizando o [Swagger](http://swagger.io/)

Com a aplicação executando, podemos acessar o endereço:
- Aplicação executada via Java: `http://localhost:8080/swagger-ui.html`
- Aplicação executada via Docker: `http://{IP_VM_DOCKER}:8080/swagger-ui.html`
