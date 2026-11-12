# API Sorelle — rotas

Base: `http://localhost:5126`. Os dados trafegam em JSON, com nomes de campo em camelCase.
Rotas protegidas exigem o cabeçalho `Authorization: Bearer <token>`.
Erros de validação devolvem `{ "erro": "mensagem" }`.

## Autenticação

### POST /api/auth/registrar (público)
Corpo: `{ "nome": "", "email": "", "senha": "" }`
- **200**: `{ "token": "", "usuarioId": 0, "nome": "", "email": "", "perfil": "Cliente" }`
- **400**: nome com menos de 2 caracteres, e-mail inválido ou senha com menos de 6 caracteres
- **409**: e-mail já cadastrado

### POST /api/auth/login (público)
Corpo: `{ "email": "", "senha": "" }`
- **200**: mesmo formato do registro
- **401**: `E-mail ou senha incorretos.`

### GET /api/auth/eu (logado)
- **200**: `{ "autenticado": true, "nome": "" }`
- **401**: sem token, ou token inválido ou expirado

O token é um JWT com validade de 120 minutos (configurável em `Jwt:ExpiraEmMinutos`) e carrega `sub` (id), `email`, `nome` e `role` (perfil).

## Produtos

### GET /api/produtos (público)
Lista os produtos ativos.
- **200**: `[ { "id": 1, "nome": "", "descricao": "", "preco": 0.0, "arvores": 0, "tag": "" } ]`

## Compras

### POST /api/compras (logado)
Registra pedido, itens e certificado em uma única transação.
Corpo: `{ "nomeCertificado": "", "itens": [ { "produtoId": 1, "quantidade": 2 } ] }`
- **200**: `{ "pedidoId": 0, "codigoDiploma": "SOR-AAAAMMDD-00001", "totalArvores": 0, "valorTotal": 0.0 }`
- **400**: nome do certificado curto, sacola vazia, quantidade fora de 1 a 99, ou produto inexistente/inativo (a compra inteira é recusada)
- **401**: sem token ou token inválido

O valor e as árvores são calculados no servidor. O usuário vem do token.

## Impacto

### GET /api/impacto (logado)
- **200**: `{ "totalArvores": 0, "co2RetidoKg": 0, "nivel": "Semente", "certificados": [ { "nomeCertificado": "", "codigoDiploma": "", "totalArvoresPlantadas": 0, "dataEmissao": "2026-01-01T00:00:00" } ] }`

CO₂ retido = árvores × 15 (critério do grupo). Níveis: Semente, Semeador (10+), Protetor da Floresta (50+) e Guardião da Mata (100+).

## Administração (perfil Admin)

Sem token: **401**. Com token de cliente: **403**.

| Rota | Função | Resposta |
|---|---|---|
| `GET /api/admin/resumo` | Totais gerais | `{ totalUsuarios, totalPedidos, valorVendido, totalArvores }` |
| `GET /api/admin/produtos` | Todos os produtos, inclusive inativos | Lista com o campo `ativo` |
| `POST /api/admin/produtos` | Cria produto | `{ "id": 0 }` |
| `PUT /api/admin/produtos/{id}` | Atualiza produto | 204, ou 404 |
| `DELETE /api/admin/produtos/{id}` | **Inativa** o produto (não apaga) | 204, ou 404 |
| `GET /api/admin/pedidos` | Pedidos com nome do cliente e código do certificado | Lista |
| `GET /api/admin/usuarios` | Usuários e perfis (sem a senha) | Lista |

Corpo de criação e edição: `{ "nome": "", "descricao": "", "preco": 0.0, "arvores": 0, "tag": "", "ativo": true }`
Validações (400): nome com menos de 2 caracteres, preço negativo ou árvores negativas.

## CORS

Origens liberadas: `http://localhost:5173`, `http://localhost:3000` e `https://sorellems.netlify.app`. Outras origens precisam ser adicionadas em `Program.cs`.
