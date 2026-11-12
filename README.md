# Sorelle Essence — PIM IV

Solução integrada da Sorelle Essence: aplicativo Android, API REST em ASP.NET Core e banco SQL Server.

```
App Android (Java)  ──┐
                      ├──►  API REST (C# / ASP.NET Core)  ──►  SQL Server (SorelleESG_DB)
Site (React)  ────────┘
```

## Estrutura do repositório

| Pasta | Conteúdo |
|---|---|
| `SorelleApp/` | Aplicativo Android nativo (Java) |
| `SorelleApi/` | API REST (C#, ASP.NET Core, Dapper) |
| `database/` | Script de criação do banco (`sorelle_esg_db.sql`) |
| `docs/` | Documentação (rotas da API em `docs/API.md`) |

## Requisitos

- SQL Server (ou SQL Server Express) e o SQL Server Management Studio (SSMS)
- .NET SDK na versão indicada em `SorelleApi/SorelleApi.csproj` (`TargetFramework`)
- Android Studio, com um emulador ou um aparelho Android (minSdk 24)
- Git

## 1. Banco de dados

1. Abra o SSMS, conecte no seu servidor e abra `database/sorelle_esg_db.sql`.
2. Execute (F5). O script cria o banco `SorelleESG_DB`, as tabelas, o trigger e as procedures.
3. Se o banco já existir, o script não altera nada (mensagem "O banco SorelleESG_DB ja existe").

## 2. API

1. Entre em `SorelleApi/` e crie o arquivo `appsettings.Development.json` (ele **não** vai pro Git):

```json
{
  "ConnectionStrings": {
    "SorelleDb": "Server=SEU_SERVIDOR\\SQLEXPRESS;Database=SorelleESG_DB;Trusted_Connection=True;TrustServerCertificate=True;"
  },
  "Jwt": {
    "Key": "uma-chave-secreta-com-no-minimo-32-caracteres"
  }
}
```

O nome do servidor é o que aparece no campo "Nome do Servidor" ao conectar no SSMS. Na connection string, a barra invertida é escrita em dobro (`\\`).

2. Rode:

```
cd SorelleApi
dotnet run
```

3. A API sobe em `http://localhost:5126`. Teste abrindo `http://localhost:5126/api/produtos`: devem aparecer 4 produtos.

### Criar um administrador

1. Cadastre uma conta normalmente (`POST /api/auth/registrar`, ou pelo app).
2. No SSMS: `UPDATE Usuarios SET Perfil = 'Admin' WHERE Email = 'email-da-conta';`
3. Faça **login de novo**: o token antigo não carrega o perfil novo.

## 3. Aplicativo Android

1. No Android Studio, abra a pasta `SorelleApp/` e espere a sincronização do Gradle (precisa de internet na primeira vez).
2. O endereço da API fica em `data/api/ApiClient.java` (`BASE_URL`).
3. Com a API rodando e o emulador aberto, crie o túnel até o PC:

```
adb reverse tcp:5126 tcp:5126
```

(o `adb` fica em `%LOCALAPPDATA%\Android\Sdk\platform-tools`). O comando precisa ser repetido toda vez que o emulador for reiniciado.

4. Rode o app (▶). Alternativa sem túnel: usar `http://10.0.2.2:5126/` no `BASE_URL` e iniciar a API com `dotnet run --urls "http://0.0.0.0:5126"`, liberando a porta no firewall do Windows.

## Problemas comuns

| Sintoma | Causa provável |
|---|---|
| App mostra "Sem conexão com o servidor" | API desligada, ou túnel (`adb reverse`) não criado |
| API não inicia: erro de chave | `Jwt:Key` ausente ou com menos de 32 caracteres |
| API: `Invalid column name 'Perfil'` | Banco criado com um script antigo |
| Build do Android reclama de `jlink` | O Gradle está usando um JRE sem `jlink`: use um JDK completo |
| Emulador não abre | Virtualização desligada na BIOS |

## Observações

- O site deve usar **esta** API (porta 5126). O `script_banco.sql` e a API do projeto do site são de uma versão anterior e **não são compatíveis** com este banco.
- Em desenvolvimento a comunicação é HTTP. Em produção seria necessário HTTPS.
- Não suba para o Git: `appsettings.Development.json`, chaves e senhas.
