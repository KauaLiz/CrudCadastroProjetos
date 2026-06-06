# Sistema de Gerenciamento de Projetos

* Sistema desenvolvido com Java e Spring Boot para o gerenciamento completo de um projeto em um ambiente corporativo, contendo uma API
externa mockada para simular a criação de membros

* A aplicação permite controle de projetos, equipes, orçamento, risco e status.

## Tecnologias

* Java
* Spring Boot
* Spring Security
* JPA
* PostGreSQL
* Swagger
* Testes Unitários com JUnit

## Autenticação

* Primeiramente, é necessário registrar um usuário e posteriormente realizar login para obter o token de acesso

* Os perfis disponíveis são:

  * ***ADMINISTRADOR***
  * ***MEMBRO***

## Cadastro de Projetos

* Para adicionar membros ao projeto, é necessário criá-los na API externa mockada e depois incluí-los no endpoint de criação de projetos

* Cada projeto deve ter entre um a dez membros e um gerente

* Um membro não pode estar em mais de 3 projetos ativos simultaneamente

* Não é permitido adicionar membros repetidos no mesmo projeto

* Não é permitido associar um membro como gerente.

* Ao gerar o projeto, é definido automaticamente seu risco com base no orçamento e prazo.

* Os campos para preencher são:
  * Nome
  * Data de Início
  * Previsão de término
  * Data real de término
  * Orçamento total
  * Descrição
  * Gerente Responsável
  * Risco
  * Status

## Descrição dos Endpoints

Por meio dos endpoints, é possível:

* Cancelar um projeto a qualquer momento

* Modificar o status do projeto para a próxima etapa

  Assim que o projeto é criado, o status fica como 'em análise' e segue a seguinte ordem:  

  * Em análise
  * Análise Realizada
  * Análise Aprovada
  * Iniciado
  * Planejado
  * Em andamento
  * Encerrado

  Não é possível pular etapas. A transição é feita sequencialmente
  
* Associar novos membros
  
  * É possível associar novos membros, desde que não sejam repetidas, não estejam em mais de 3 projetos que não forem encerrados ou cancelados e que respeito o limite máximo de 10 membros.
  
* Listar todos os projetos

  * Gera uma lista de todos os projetos com todas as informações disponíveis
  
* Relatório
  
  Gera relatório contendo:
  
  * Quantidade de projetos por status
  * Total de orçamento por status
  * Média de duração dos projetos encerrados
  * Quantiade de membros únicos em cada projeto
  
* Deletar um projeto

  * Somente não é possível deletar um projeto quando seu status é de 'Iniciado', 'Em andamento' ou 'Encerrado'

## Como executar

1. Clone o repositório
```
  git clone https://github.com/KauaLiz/CrudCadastroProjetos.git
```

2. Acesse a pasta cadastroProjetos
```
  cd cadastroProjetos
```

3. Configure o banco de dados por meio do script disponibilizado no repositório

```
  dbProjects.sql
```

4. Escreva esse comando no terminal para rodar a aplicação

No Windows:

```
  mvnw spring-boot:run
```

No macOS:

```
  ./mvnw spring-boot:run
```

## Swagger

Assim que o projeto estiver rodando, acesse este link para ter acesso ao Swagger da aplicação: 
```
  http://127.0.0.1:8080/swagger-ui/index.html
```
