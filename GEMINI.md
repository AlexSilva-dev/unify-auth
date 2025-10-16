#s Guia de Arquitetura e Padrões para o Gemini-CLI

Este documento serve como um guia para o assistente de IA (Gemini) para garantir que todas as sugestões e modificações
de código sigam os padrões de arquitetura estabelecidos neste projeto.

## 1. Estrutura de Módulos

O projeto é um aplicativo Kotlin Multiplatform (KMP) com uma estrutura de monólito modular.

- **`featuresServer/`**: Este **não** é um módulo, mas um **diretório** que agrupa os módulos de feature do backend.
  Cada submódulo aqui (ex: `featuresServer/authentication`, `featuresServer/user`) representa uma capacidade de negócio
  vertical e autônoma.
- **`:server`**: O backend da aplicação, escrito em Ktor. Ele atua como o **orquestrador**, integrando os diversos
  módulos de feature contidos em `featuresServer/` e expondo a API pública.
- **`:shared`**: Contém lógica e modelos de dados (ex: DTOs) que são compartilhados entre **todos** os alvos, incluindo
  os módulos de `featuresServer/` e o `:composeApp`.
- **`:composeApp`**: Contém a **lógica de negócio do cliente** (UseCases, Repositories) e toda a **camada de UI** (
  Telas, ViewModels). A lógica aqui é compartilhada entre todos os alvos de front-end (Android, iOS, Desktop, Wasm), mas
  não é usada pelo `:server`.
- **`:iosApp`**: O host da aplicação para a plataforma iOS.

## 2. Arquitetura de Camadas (Clean Architecture)

O projeto segue um padrão de Arquitetura Limpa. O fluxo de dados do cliente é estritamente unidirecional:

`UI (Compose) -> ViewModel -> UseCase -> Repository (Interface) -> RepositoryImpl -> DataSource -> Backend`

- **UI (em `:composeApp`)**: Apenas exibe o estado vindo do ViewModel e captura interações do usuário.
- **ViewModel (em `:composeApp`)**: Gerencia o estado da UI (usando `StateFlow` e `BaseState`). **Não contém regras de
  negócio**. Apenas chama os `UseCases`.
- **UseCase (em `:shared`)**: Encapsula uma única regra de negócio (ex: `GetAllTopicsUseCase`). Depende de interfaces de
  `Repository`.
- **Repository (em `:shared`)**: A interface fica no `domain` e a implementação no `infrastructure`. Media o acesso aos
  dados, abstraindo a origem (API, cache, etc.).
- **DataSource (em `:shared`)**: A implementação fica no `infrastructure`. É a única camada que conhece o Ktor e faz as
  chamadas HTTP de fato.

## 3. Padrão de Retorno e Tratamento de Erros

Este é o padrão **obrigatório** para comunicação entre as camadas.

**Regra Principal:** Todas as funções em `DataSource`, `Repository` e `UseCase` que podem falhar (ex: chamadas de rede)
**DEVEM** retornar o tipo `Result<Success, Failure>`.

- **A Classe `Result`**: Usamos a classe customizada `Result<T, E>` localizada em
  `shared/src/commonMain/kotlin/com/example/template/app/domain/entities/Result.kt`.

- **A Classe `Failure`**: Usamos a classe `sealed class Failure` padronizada, localizada em
  `shared/src/commonMain/kotlin/com/example/template/app/domain/entities/Failure.kt`. Ela contém os seguintes tipos:
    - `NetworkError`: Para falhas de conectividade.
    - `ServerError(code, message)`: Para erros de servidor (HTTP 5xx, 4xx).
    - `UnauthorizedError`: Para erros HTTP 401, que devem acionar um fluxo de re-autenticação.
    - `UnknownError(exception)`: Para exceções não esperadas.

- **Fluxo do Padrão**:
    1. O **`DataSource`** cria o `Result`, tratando exceções de rede e status HTTP.
    2. O **`Repository`** e o **`UseCase`** apenas repassam o `Result` para a camada superior.
    3. O **`ViewModel`** é o consumidor final. Ele usa um `when` para inspecionar o `Result` e atualizar o estado da
       UI (`BaseState`).

**NÃO** use exceções para controlar o fluxo de erros esperados (como falhas de API). Use o `Result`.

## 4. Injeção de Dependência

- Usamos **Koin** para injeção de dependência.
- A configuração é modularizada:
    - `:moduleCore` para a lógica de negócio no `:shared`.
    - `:moduleCompose` para ViewModels e dependências de UI no `:composeApp`.
    - `:moduleTarget` para implementações específicas de cada plataforma (`expect`/`actual`).

## 5. Outras Convenções

- **Estilo de Código**: Siga o estilo e formatação do código existente.
- **Plataformas Específicas**: Use o padrão `expect`/`actual` para acessar APIs nativas (ex:
  `GoogleAuthHandlerViewModel`, `CreateSettingsLibrary`).
- **Navegação**: A navegação é type-safe, usando uma `sealed class Screen` serializável.

## 6. Governança de Documentação e Tarefas

A pasta `docs/` é a fonte central da verdade para a arquitetura e os requisitos técnicos do projeto. O objetivo é manter
o código, as tarefas e a documentação sempre sincronizados.

- **Manutenção de Contexto:** Para garantir o alinhamento, é fundamental que você mantenha o contexto do projeto em
  mente. Se, em qualquer momento, você sentir que não tem o contexto necessário sobre um requisito ou o funcionamento de
  uma feature, **consulte os casos de uso relevantes em `docs/use-cases/` para (re)estabelecer o entendimento** antes de
  prosseguir. A leitura inicial dos documentos é essencial para formar essa base de conhecimento, que deve ser
  recarregada sempre que o contexto se perder.
- **Fonte da Verdade:** A documentação de caso de uso (padrão UML textual) define o comportamento esperado de uma
  feature. As tarefas do YouTrack devem ser guiadas por ela.
- **Gestão de Mudanças:** Após uma modificação, avalie o impacto. Se a mudança afetar o comportamento descrito no caso
  de uso, a documentação **deve** ser atualizada para refletir o novo estado.
- **Interação com Tarefas em Andamento:** Não modifique diretamente uma tarefa que já está em uma sprint ativa. Em vez
  disso, adicione comentários na tarefa no YouTrack para registrar decisões, sugerir melhorias ou esclarecer pontos.

### Nível de Detalhe das Tarefas

- **Seja Técnico e Específico:** Ao detalhar uma tarefa técnica (especialmente uma subtarefa), não hesite em adicionar um nível maior de detalhe para guiar a implementação. O objetivo é que um desenvolvedor tenha um guia claro, mas sem restringir sua liberdade de como implementar.
- **O que incluir:**
  - **Pseudocódigo:** Para lógicas complexas ou algoritmos.
  - **Estrutura de Dados:** Descreva o formato de DTOs ou objetos esperados. Exemplo: `A rota POST /api/exemplo deve esperar um DTO com a seguinte estrutura: data class ExemploDto(val id: String, val valor: Int)`.
  - **Passo a Passo:** Detalhe os passos que um caso de uso ou método deve seguir, como as validações, chamadas a repositórios e o objeto de retorno.

### Como Encontrar Tarefas Relacionadas (Subtarefas)

- **Não Confie Apenas na Busca por Título:** Uma busca pelo título exato pode não encontrar tarefas, especialmente se elas forem subtarefas com títulos mais técnicos ou diferentes.
- **Use a Busca Avançada para Subtarefas:** A maneira mais eficaz de encontrar subtarefas é usar a ferramenta `advanced_search` com a query `Subtask of: <ID_DA_TAREFA_PAI>`. Isso listará todas as subtarefas diretas de uma tarefa principal.

### Ciclo de Vida de Novas Ideias e Features

Para garantir que novas ideias sejam capturadas e desenvolvidas de forma organizada, o seguinte fluxo de trabalho deve
ser seguido:

1. **Visão Estratégica (Roadmap):** Novas ideias e features de alto nível devem ser adicionadas primeiro ao arquivo
   `docs/README.md` como itens marcados com `(coming soon)`. Isso mantém o roadmap do projeto claro e versionado.

2. **Pesquisa Antes da Criação:** Antes de criar uma nova tarefa no YouTrack, sempre realize uma busca para garantir que
   uma tarefa semelhante já não exista no projeto correto. Isso evita duplicidade e mantém o backlog organizado.

3. **Backlog Tático (YouTrack):** Para cada item adicionado ao roadmap (que ainda não possui uma tarefa), uma tarefa
   correspondente deve ser criada no YouTrack. Inicialmente, essa tarefa pode ser um simples "placeholder" para garantir
   que a ideia entre no backlog e possa ser priorizada.

4. **Detalhamento (Caso de Uso):** Quando a tarefa for priorizada para desenvolvimento, um novo arquivo de caso de uso
   detalhado (ex: `docs/use-cases/UC-XX-nome-da-feature.md`) deve ser criado.

5. **Desenvolvimento (Tarefa Detalhada):** A tarefa no YouTrack deve ser atualizada com os critérios de aceitação
   detalhados, fazendo referência ao novo documento de caso de uso. Isso garante que o desenvolvimento seja guiado por
   requisitos claros e completos.

## 7. Interação com Ferramentas (Tool Interaction)

- **Parâmetros como JSON:** Ao usar ferramentas que interagem com APIs externas (como o YouTrack), se um parâmetro como `kwargs` for do tipo `string`, ele pode estar esperando um **objeto JSON válido** como valor.
  - **Exemplo:** Em vez de passar `issue_id="ID", description="texto"`, o correto é `{"issue_id": "ID", "description": "texto"}`.
  - **Caracteres Especiais:** Dentro da string de descrição no JSON, caracteres especiais como quebras de linha devem ser escapados (ex: `\n`), e aspas duplas devem ser escapadas (ex: `\"`). A formatação correta é crucial.

## Instrução para comunicação comigo:

- Sempre leia o arquivo no qual eu citei, busque ele e leia para entender o cenário, e se for necessário leia os
  relacionados para entender como tudo ta funcionando para não da resposta inútes.
- De preferencia a me explicar e mostrar o código e não para fazer ele.