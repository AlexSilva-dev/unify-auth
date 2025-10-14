# Guia de Arquitetura e Padrões para o Gemini-CLI

Este documento serve como um guia para o assistente de IA (Gemini) para garantir que todas as sugestões e modificações de código sigam os padrões de arquitetura estabelecidos neste projeto.

## 1. Estrutura de Módulos

O projeto é um aplicativo Kotlin Multiplatform (KMP) com uma estrutura de monólito modular.

- **`featuresServer/`**: Este **não** é um módulo, mas um **diretório** que agrupa os módulos de feature do backend. Cada submódulo aqui (ex: `featuresServer/authentication`, `featuresServer/user`) representa uma capacidade de negócio vertical e autônoma.
- **`:server`**: O backend da aplicação, escrito em Ktor. Ele atua como o **orquestrador**, integrando os diversos módulos de feature contidos em `featuresServer/` e expondo a API pública.
- **`:shared`**: Contém lógica e modelos de dados (ex: DTOs) que são compartilhados entre **todos** os alvos, incluindo os módulos de `featuresServer/` e o `:composeApp`.
- **`:composeApp`**: Contém a **lógica de negócio do cliente** (UseCases, Repositories) e toda a **camada de UI** (Telas, ViewModels). A lógica aqui é compartilhada entre todos os alvos de front-end (Android, iOS, Desktop, Wasm), mas não é usada pelo `:server`.
- **`:iosApp`**: O host da aplicação para a plataforma iOS.

## 2. Arquitetura de Camadas (Clean Architecture)

O projeto segue um padrão de Arquitetura Limpa. O fluxo de dados do cliente é estritamente unidirecional:

`UI (Compose) -> ViewModel -> UseCase -> Repository (Interface) -> RepositoryImpl -> DataSource -> Backend`

- **UI (em `:composeApp`)**: Apenas exibe o estado vindo do ViewModel e captura interações do usuário.
- **ViewModel (em `:composeApp`)**: Gerencia o estado da UI (usando `StateFlow` e `BaseState`). **Não contém regras de negócio**. Apenas chama os `UseCases`.
- **UseCase (em `:shared`)**: Encapsula uma única regra de negócio (ex: `GetAllTopicsUseCase`). Depende de interfaces de `Repository`.
- **Repository (em `:shared`)**: A interface fica no `domain` e a implementação no `infrastructure`. Media o acesso aos dados, abstraindo a origem (API, cache, etc.).
- **DataSource (em `:shared`)**: A implementação fica no `infrastructure`. É a única camada que conhece o Ktor e faz as chamadas HTTP de fato.

## 3. Padrão de Retorno e Tratamento de Erros

Este é o padrão **obrigatório** para comunicação entre as camadas.

**Regra Principal:** Todas as funções em `DataSource`, `Repository` e `UseCase` que podem falhar (ex: chamadas de rede) **DEVEM** retornar o tipo `Result<Success, Failure>`.

- **A Classe `Result`**: Usamos a classe customizada `Result<T, E>` localizada em `shared/src/commonMain/kotlin/com/example/template/app/domain/entities/Result.kt`.

- **A Classe `Failure`**: Usamos a classe `sealed class Failure` padronizada, localizada em `shared/src/commonMain/kotlin/com/example/template/app/domain/entities/Failure.kt`. Ela contém os seguintes tipos:
    - `NetworkError`: Para falhas de conectividade.
    - `ServerError(code, message)`: Para erros de servidor (HTTP 5xx, 4xx).
    - `UnauthorizedError`: Para erros HTTP 401, que devem acionar um fluxo de re-autenticação.
    - `UnknownError(exception)`: Para exceções não esperadas.

- **Fluxo do Padrão**:
    1. O **`DataSource`** cria o `Result`, tratando exceções de rede e status HTTP.
    2. O **`Repository`** e o **`UseCase`** apenas repassam o `Result` para a camada superior.
    3. O **`ViewModel`** é o consumidor final. Ele usa um `when` para inspecionar o `Result` e atualizar o estado da UI (`BaseState`).

**NÃO** use exceções para controlar o fluxo de erros esperados (como falhas de API). Use o `Result`.

## 4. Injeção de Dependência

- Usamos **Koin** para injeção de dependência.
- A configuração é modularizada:
    - `:moduleCore` para a lógica de negócio no `:shared`.
    - `:moduleCompose` para ViewModels e dependências de UI no `:composeApp`.
    - `:moduleTarget` para implementações específicas de cada plataforma (`expect`/`actual`).

## 5. Outras Convenções

- **Estilo de Código**: Siga o estilo e formatação do código existente.
- **Plataformas Específicas**: Use o padrão `expect`/`actual` para acessar APIs nativas (ex: `GoogleAuthHandlerViewModel`, `CreateSettingsLibrary`).
- **Navegação**: A navegação é type-safe, usando uma `sealed class Screen` serializável.

## 6. Governança de Documentação e Tarefas

A pasta `docs/` é a fonte central da verdade para a arquitetura e os requisitos técnicos do projeto. O objetivo é manter o código, as tarefas e a documentação sempre sincronizados.

- **Manutenção de Contexto:** Para garantir o alinhamento, é fundamental que você mantenha o contexto do projeto em mente. Se, em qualquer momento, você sentir que não tem o contexto necessário sobre um requisito ou o funcionamento de uma feature, **consulte os casos de uso relevantes em `docs/use-cases/` para (re)estabelecer o entendimento** antes de prosseguir. A leitura inicial dos documentos é essencial para formar essa base de conhecimento, que deve ser recarregada sempre que o contexto se perder.
- **Fonte da Verdade:** A documentação de caso de uso (padrão UML textual) define o comportamento esperado de uma feature. As tarefas do YouTrack devem ser guiadas por ela.
- **Gestão de Mudanças:** Após uma modificação, avalie o impacto. Se a mudança afetar o comportamento descrito no caso de uso, a documentação **deve** ser atualizada para refletir o novo estado.
- **Interação com Tarefas em Andamento:** Não modifique diretamente uma tarefa que já está em uma sprint ativa. Em vez disso, adicione comentários na tarefa no YouTrack para registrar decisões, sugerir melhorias ou esclarecer pontos.

## Instrução para comunicação comigo:
- Sempre leia o arquivo no qual eu citei, busque ele e leia para entender o cenário, e se for necessário leia os relacionados para entender como tudo ta funcionando para não da resposta inútes.
- De preferencia a me explicar e mostrar o código e não para fazer ele.