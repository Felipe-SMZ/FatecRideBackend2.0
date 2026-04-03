# 🏗️ Arquitetura do Projeto - Mensagens API

Documentação detalhada da arquitetura, padrões e fluxos da API de Mensagens.

---

## 📐 Arquitetura de Alto Nível

```
┌─────────────────────────────────────────────────────────────────┐
│                      FRONTEND (Browser)                         │
│                                                                 │
│  ┌─────────────────────┐         ┌──────────────────┐         │
│  │   REST Client       │         │  WebSocket Client│         │
│  │  (axios/fetch)      │         │                  │         │
│  └──────────┬──────────┘         └────────┬─────────┘         │
└─────────────┼──────────────────────────────┼──────────────────┘
              │                              │
              │ HTTP GET/POST/PATCH          │ WebSocket
              │                              │
┌─────────────┼──────────────────────────────┼──────────────────┐
│             ▼                              ▼                   │
│  ┌──────────────────────────────────────────────┐              │
│  │        Express Server (Port 9000)            │              │
│  │                                              │              │
│  │  ┌──────────────────────────────────────┐   │              │
│  │  │  CORS Middleware & Body Parser       │   │              │
│  │  └──────────────────────────────────────┘   │              │
│  │                                              │              │
│  │  ┌─────────────────────────────────────┐    │              │
│  │  │  Router: /api/messages               │    │              │
│  │  │  - POST /                            │    │              │
│  │  │  - GET /conversations               │    │              │
│  │  │  - GET /with/:userId               │    │              │
│  │  │  - PATCH /:messageId/read           │    │              │
│  │  └─────────────────────────────────────┘    │              │
│  │                    │                         │              │
│  │  ┌────────────────▼──────────────────────┐  │              │
│  │  │  JWT Middleware (checkToken)          │  │              │
│  │  │  - Decodifica & valida token         │  │              │
│  │  │  - Anexa usuarioID ao request        │  │              │
│  │  └────────────────┬──────────────────────┘  │              │
│  │                   │                         │              │
│  │  ┌────────────────▼──────────────────────┐  │              │
│  │  │  Controller Layer                     │  │              │
│  │  │  (MensagensController.js)             │  │              │
│  │  │  - Valida request/response           │  │              │
│  │  │  - Chama services                    │  │              │
│  │  └────────────────┬──────────────────────┘  │              │
│  │                   │                         │              │
│  │  ┌────────────────▼──────────────────────┐  │              │
│  │  │  Service Layer                       │  │              │
│  │  │  (MensagensService.js)               │  │              │
│  │  │  - Lógica de negócio                 │  │              │
│  │  │  - Validações                        │  │              │
│  │  │  - Orquestração de dados             │  │              │
│  │  └────────────────┬──────────────────────┘  │              │
│  │                   │                         │              │
│  │  ┌────────────────┴──────────────────────┐  │              │
│  │  │  Data Layer                          │  │              │
│  │  │  - UserService.js (busca de users)  │  │              │
│  │  │  - TokenService.js (JWT)            │  │              │
│  │  │  - Mongoose Models                  │  │              │
│  │  │  - Sequelize Models                 │  │              │
│  │  └────────────────┬──────────────────────┘  │              │
│  │                   │                         │              │
│  │  ┌────────────────▼──────────────────────┐  │              │
│  │  │  WebSocket Server (ws)               │  │              │
│  │  │  - Gerencia conexões em tempo real   │  │              │
│  │  │  - Broadcast de mensagens            │  │              │
│  │  └────────────────┬──────────────────────┘  │              │
│  │                   │                         │              │
│  └───────────────────┼────────────────────────┘              │
└──────────────────────┼─────────────────────────────────────────┘
                       │
         ┌─────────────┼──────────────┐
         │             │              │
         ▼             ▼              ▼
    ┌─────────┐  ┌──────────┐  ┌───────────┐
    │ MongoDB │  │  MySQL   │  │ Filesystem│
    │  (Chat) │  │ (Users)  │  │  Logger   │
    └─────────┘  └──────────┘  └───────────┘
```

---

## 📁 Estrutura de Pastas Detalhada

```
mensagens/
│
├── 📄 server.js                           # Entrada principal
│   └─ Configura Express, CORS, WebSocket
│   
├── 📄 package.json                        # Metadados do projeto
├── 📄 .env                                # Variáveis secretas (Git ignore)
├── 📄 .env.example                        # Exemplo de .env
│
├── 📁 controller/                         # Camada de Roteamento
│   ├── MensagensController.js    ✅ ATIVO
│   │   └─ POST /
│   │   └─ GET /conversations
│   │   └─ GET /with/:userId
│   │   └─ PATCH /:messageId/read
│   │   └─ checkToken() middleware
│   │
│   └── UserControler.js          ❌ NÃO IMPLEMENTADO
│
├── 📁 service/                            # Camada de Lógica
│   ├── MensagensService.js       ✅ ATIVO
│   │   ├─ sendMessage()
│   │   ├─ getUserConversations()
│   │   ├─ getConversationMessages()
│   │   └─ markMessageRead()
│   │
│   ├── TokenService.js           ✅ ATIVO
│   │   ├─ gerarToken()
│   │   └─ decodificarToken()
│   │
│   └── UserService.js            ✅ ATIVO
│       ├─ findAllUser()
│       └─ findById()
│
├── 📁 models/                             # Camada de Dados
│   ├── mongodbmodels/
│   │   └── MensagensSchema.js    ✅ ATIVO
│   │       └─ Schema com campos:
│   │          - id_sender
│   │          - id_receiver
│   │          - id_solicitacao
│   │          - data
│   │          - data_atualizacao
│   │          - message
│   │          - lida
│   │
│   └── mysqlmodels/
│       ├── User.js               ⚠️ REFERENCIADO
│       ├── Course.js             ⚠️ NÃO USADO
│       ├── Gender.js             ⚠️ NÃO USADO
│       └── UserType.js           ⚠️ NÃO USADO
│
├── 📁 database/                           # Conexões DB
│   ├── dbMongodb.js              ✅ ATIVO
│   │   └─ mongoose.connect()
│   │
│   └── dbMysql.js                ✅ ATIVO
│       └─ Sequelize ORM
│
├── 📁 pages/                              # Frontend (HTML - Deprecated)
│   ├── motorista.html                    
│   └── passageiro.html
│
├── 📄 README.md                           # Documentação principal
├── 📄 SECURITY.md                         # Análise de segurança
├── 📄 INSTALLATION.md                     # Guia de instalação
├── 📄 ARCHITECTURE.md                     # Este arquivo
└── 📄 MENSAGENS_API_FRONTEND.md          # Documentação frontend
```

---

## 🔄 Fluxos de Dados

### Fluxo 1: Enviar Mensagem via REST

```
1. Cliente envia:
   POST /api/messages
   Authorization: Bearer TOKEN
   { receiver: 2, message: "Oi" }

2. MensagensController.js
   └─ checkToken() valida JWT
   └─ sendMessage(token, message)

3. MensagensService.js
   ├─ decodificarToken(token) ➜ userId
   ├─ findById(userId) ➜ Usuário enviador
   ├─ findById(message.receiver) ➜ Usuário receptor
   ├─ new MensagensSchema(messageData)
   ├─ messageSave.save() ➜ MongoDB
   └─ return { sucesso: true, data: savedMessage }

4. Controller:
   └─ res.status(201).json({ data: saved })

5. Cliente recebe:
   { data: { _id, id_sender, id_receiver, message, ... } }
```

### Fluxo 2: Receber Mensagem via WebSocket

```
1. Cliente conecta:
   new WebSocket('ws://localhost:9000', [TOKEN])

2. server.js
   ├─ req.headers['sec-websocket-protocol'] ➜ TOKEN
   ├─ checkToken(token)
   ├─ findById(decoded.sub) ➜ Usuário
   ├─ users.set(userId, ws) ➜ Armazena conexão
   └─ ws.on('message', ...)

3. Cliente envia:
   ws.send(JSON.stringify({
     receiver: 2,
     message: "Teste WS"
   }))

4. Server processa:
   ├─ JSON.parse(message)
   ├─ new MensagensSchema(messageData)
   ├─ messageSave.save() ➜ MongoDB
   ├─ reciverConect = users.get(receiver)
   ├─ if (reciverConect):
   │  └─ reciverConect.send(JSON.stringify({
   │     tipo: 'mensagem_recebida',
   │     mensagem: messageData
   │  }))
   └─ ws.send({ tipo: 'mensagem_confirmada' })

5. Receptor recebe (em tempo real):
   { tipo: 'mensagem_recebida', mensagem: {...} }

6. Emissor recebe confirmação:
   { tipo: 'mensagem_confirmada' }
```

### Fluxo 3: Listar Conversas

```
1. Cliente:
   GET /api/messages/conversations
   Authorization: Bearer TOKEN

2. MensagensController
   └─ getUserConversations(req.userId)

3. MensagensService
   ├─ find({ $or: [{ id_sender: userId }, { id_receiver: userId }] })
   ├─ Agrupar por partner
   ├─ Para cada partner:
   │  ├─ Pegar última mensagem
   │  └─ Contar mensagens não lidas
   └─ return array de conversas

4. Response:
   [{
     partnerId: 2,
     lastMessage: {_id, sender, receiver, message, data, lida},
     unreadCount: 3
   }]
```

---

## 🔐 Fluxo de Autenticação JWT

```
1️⃣ GERAÇÃO (não implementado neste projeto, serviço externo)
   ┌─────────────────────────────────────────┐
   │ Serviço de Login (fora desta API)       │
   ├─────────────────────────────────────────┤
   │ 1. Usuário entra user/password          │
   │ 2. Validar no MySQL                     │
   │ 3. Gerar JWT:                           │
   │    jwt.sign({sub: userId}, SECRET)      │
   │ 4. Retornar token ao cliente            │
   └─────────────────────────────────────────┘

2️⃣ ENVIO (Cliente)
   ┌─────────────────────────────────────────┐
   │ Authorization: Bearer <JWT_TOKEN>       │
   └─────────────────────────────────────────┘

3️⃣ VALIDAÇÃO (server.js)
   ┌─────────────────────────────────────────┐
   │ checkToken(token)                       │
   │ ├─ jwt.verify(token, SECRET)            │
   │ ├─ Se válido: decoded = {sub: userId}   │
   │ ├─ Se inválido: throw Error             │
   │ └─ return decoded                       │
   └─────────────────────────────────────────┘

4️⃣ USO
   ┌─────────────────────────────────────────┐
   │ req.userId = decoded.sub                │
   │ Usar em queries: where { id_usuario }   │
   └─────────────────────────────────────────┘
```

---

## 🗄️ Modelos de Dados

### MongoDB: Mensagem

```javascript
{
  "_id": ObjectId,                    // Gerado automaticamente
  "id_sender": 1,                     // ID do User do MySQL
  "id_receiver": 2,                   // ID do User do MySQL
  "id_solicitacao": 1,                // ID da carona/solicitação
  "data": ISODate("2024-04-03..."),  // Data envio
  "data_atualizacao": null,           // Quando editou
  "message": "Olá, tudo bem?",       // Conteúdo
  "lida": false                       // Flag leitura
}
```

**Índices recomendados:**
```javascript
// Buscar por sender
db.mensagens.createIndex({ id_sender: 1 })

// Buscar por receiver
db.mensagens.createIndex({ id_receiver: 1 })

// Buscar conversas
db.mensagens.createIndex({ id_sender: 1, id_receiver: 1, data: -1 })

// Buscar não lidas
db.mensagens.createIndex({ id_receiver: 1, lida: 1 })
```

### MySQL: User (Sequelize)

```javascript
{
  id_usuario: BigInt,          // PK
  nome: String,
  email: String,
  telefone: String,
  tipo_usuario: Int,           // 1=Driver, 2=Passenger
  data_criacao: DateTime,
  // ... outros campos
}
```

---

## 📊 Fluxo de uma Conversa Completa

```
USUARIO 1 (Driver)              USUARIO 2 (Passenger)
       │                                 │
       │  1. Conecta via WebSocket       │
       ├─────────────────────────────────►│
       │     (token enviado)              │
       │                                 │
       │  2. Envia mensagem               │
       ├─ "Estou chegando" ────────────►│
       │                                 │
       │  3. Mensagem salva no MongoDB    │
       │                                 │
       │  4. Notificação enviada (WS)    │ 
       │ ◄────────────────────────────────┤
       │    (em tempo real)               │
       │                                 │
       │                                 │ 5. Marca como lida
       │                                 │    PATCH /:id/read
       │◄─ Confirmação lida ─────────────┤
       │                                 │
       │  6. Depois, ver historico        │
       │    GET /with/user2              │
       │    ├─ Último 50 mensagens       │
       │    └─ Listar todas as conversas │
       │       GET /conversations        │
       │                                 │
       ▼                                 ▼
```

---

## 🔌 Stack Tecnológico

| Camada | Tecnologia | Versão | Propósito |
|--------|-----------|--------|----------|
| **Runtime** | Node.js | 16+ | Execução JavaScript |
| **Framework Web** | Express | 4.18.2 | HTTP Server |
| **WebSocket** | ws | 8.18.3 | Real-time |
| **Autenticação** | JWT | 9.0.2 | Token-based auth |
| **MongoDB ODM** | Mongoose | 8.19.2 | Schema validation |
| **MySQL ORM** | Sequelize | 6.37.7 | SQL ORM |
| **Driver MySQL** | mysql2 | 3.15.3 | Connection |
| **CORS** | cors | 2.8.5 | Cross-origin |
| **Body Parser** | body-parser | 1.20.2 | JSON parsing |
| **Env Config** | dotenv | 17.2.3 | Environment vars |
| **HTTP Headers** | Helmet | 7.1.0 | Security headers |

---

## 🏃 Request/Response Examples

### Timing de uma Requisição

```
1. Cliente envia request
   ├─ TCP Handshake: ~5ms
   ├─ TLS (se HTTPS): ~50ms
   └─ HTTP Headers: ~10ms

2. Express processa
   ├─ CORS Middleware: ~1ms
   ├─ Body Parser: ~5ms
   └─ JWT Verificação: ~10ms

3. Controller/Service
   ├─ Validação de dados: ~5ms
   ├─ Query MongoDB: ~50ms
   ├─ Query MySQL (se houver): ~20ms
   └─ Processamento: ~10ms

4. Response
   ├─ Serializar JSON: ~5ms
   ├─ Enviar dados: ~20ms
   └─ Total: ~190ms (em média)
```

---

## ⚡ Performance & Escalabilidade

### Gargalos Atuais

1. **MongoDB sem índices**: Queries podem ser lentas
2. **Sem cache**: Mesmas conversas são refetchadas sempre
3. **Sem pagination em conversas**: Pode trazer 1000+ registros

### Melhorias Recomendadas

```javascript
// 1. Adicionar Redis para cache
const redis = require('redis');
const client = redis.createClient();

// Cache conversas por 5min
const conversations = await cache.get(`conv:${userId}`);

// 2. Paginação automática
async getUserConversations(userId, page = 1, limit = 20) {
  const skip = (page - 1) * limit;
  return await Mensagens.find({...}).skip(skip).limit(limit);
}

// 3. Índices MongoDB
db.mensagens.createIndex({ id_sender: 1, id_receiver: 1, data: -1 });
```

---

## 🔗 Integrações Externas

### Necessárias
- ✅ MySQL (referenciar usuários)
- ✅ MongoDB (armazenar mensagens)

### Opcionais
- [ ] Redis (cache de conversas)
- [ ] Firebase Cloud Messaging (notificações push)
- [ ] Elasticsearch (busca full-text)
- [ ] S3/GCS (armazenar arquivos)

---

## 📈 Versões e Changelog

| Versão | Data | Mudanças |
|--------|------|----------|
| 1.0.0 | 2024-04-03 | Release inicial |
| 0.3.0 | 2024-04-02 | Adicionado WebSocket |
| 0.2.0 | 2024-04-01 | REST API completa |
| 0.1.0 | 2024-03-30 | Estrutura base |

---

## 👥 Fluxo de Usuários

```
┌──────────────────────────────────────────────┐
│ Frontend (React/Vue/HTML)                    │
│                                              │
│ 1. Login (Serviço externo)                   │
│    └─ Obter JWT Token                        │
│                                              │
│ 2. Conectar WebSocket com token              │
│    └─ Manter conexão viva                    │
│                                              │
│ 3. Carregar conversas                        │
│    └─ GET /conversations (HTTP)              │
│                                              │
│ 4. Selecionar conversa                       │
│    └─ GET /with/:userId (HTTP com paginação)│
│                                              │
│ 5. Estar pronto para recv/send               │
│    ├─ Enviar: ws.send() (WebSocket)          │
│    └─ Receber: ws.onmessage (WebSocket)      │
│                                              │
└──────────────────────────────────────────────┘
```

