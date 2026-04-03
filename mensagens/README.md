# 📱 Mensagens API - Backend

API de mensagens em tempo real para a plataforma **FatecRide**, com suporte para comunicação síncrona via WebSocket e assíncrona via REST.

---

## 🎯 Visão Geral

Este projeto implementa um serviço de mensagens full-stack que:
- ✅ Permite envio de mensagens entre usuários em tempo real via WebSocket
- ✅ Fornece histórico de mensagens com paginação
- ✅ Gerencia conversas e contagem de mensagens não lidas
- ✅ Integra com MySQL para dados de usuários
- ✅ Armazena mensagens em MongoDB
- ✅ Autentica requisições com JWT Token

---

## 📋 Requisitos

- **Node.js** 16+ 
- **MongoDB** local ou remoto
- **MySQL** local ou remoto
- **npm** ou **yarn**

---

## ⚙️ Instalação

### 1. Clone o repositório
```bash
git clone <seu-repositorio>
cd mensagens
```

### 2. Instale as dependências
```bash
npm install
```

### 3. Configure as variáveis de ambiente

Copie o arquivo `.env.example` para `.env`:
```bash
cp .env.example .env
```

Edite `.env` com suas credenciais reais:
```env
SECRET=sua_chave_jwt_super_secreta_aqui_min_32_caracteres
MONGODB_URI=mongodb://localhost/chat
DATABASEMYSQL=backendFatecCarona
USERMYSQL=root
PASSWORDMYSQL=sua_senha_mysql
PORT_REST=9000
NODE_ENV=production
```

### 4. Inicie o servidor
```bash
node server.js
```

O servidor estará disponível em: http://localhost:9000

---

## 📡 API Endpoints

### REST API

#### **POST** `/api/messages` - Enviar Mensagem
Autentica com JWT no header e envia uma nova mensagem.

**Header:**
```
Authorization: Bearer seu_jwt_token_aqui
```

**Body:**
```json
{
  "receiver": 2,
  "id_solicitacao": 1,
  "data": "2024-04-03T10:30:00Z",
  "message": "Olá! Tudo bem?"
}
```

**Response (201):**
```json
{
  "data": {
    "_id": "507f1f77bcf86cd799439011",
    "id_sender": 1,
    "id_receiver": 2,
    "id_solicitacao": 1,
    "data": "2024-04-03T10:30:00Z",
    "message": "Olá! Tudo bem?",
    "lida": false
  }
}
```

---

#### **GET** `/api/messages/conversations` - Listar Conversas
Retorna todas as conversas do usuário autenticado com última mensagem.

**Header:**
```
Authorization: Bearer seu_jwt_token_aqui
```

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "partnerId": 2,
      "lastMessage": {
        "_id": "507f1f77bcf86cd799439011",
        "id_sender": 1,
        "id_receiver": 2,
        "message": "Última mensagem",
        "data": "2024-04-03T10:30:00Z",
        "lida": false
      },
      "unreadCount": 3
    }
  ]
}
```

---

#### **GET** `/api/messages/with/:userId` - Histórico de Mensagens
Retorna mensagens entre dois usuários com paginação.

**Query Parameters:**
- `page` (default: 1) - Número da página
- `limit` (default: 50) - Mensagens por página

**Header:**
```
Authorization: Bearer seu_jwt_token_aqui
```

**URL Example:**
```
GET /api/messages/with/2?page=1&limit=50
```

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "_id": "507f1f77bcf86cd799439010",
      "id_sender": 1,
      "id_receiver": 2,
      "message": "Primeira mensagem",
      "data": "2024-04-03T09:00:00Z",
      "lida": true
    },
    {
      "_id": "507f1f77bcf86cd799439011",
      "id_sender": 2,
      "id_receiver": 1,
      "message": "Resposta",
      "data": "2024-04-03T09:15:00Z",
      "lida": true
    }
  ]
}
```

---

#### **PATCH** `/api/messages/:messageId/read` - Marcar como Lida
Marca uma mensagem como lida. Apenas o destinatário pode fazer isso.

**Header:**
```
Authorization: Bearer seu_jwt_token_aqui
```

**URL Example:**
```
PATCH /api/messages/507f1f77bcf86cd799439011/read
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "_id": "507f1f77bcf86cd799439011",
    "lida": true
  }
}
```

---

### WebSocket API

#### Conexão
```javascript
const ws = new WebSocket('ws://localhost:9000', ['seu_jwt_token_aqui']);
```

#### Enviar Mensagem
```javascript
ws.send(JSON.stringify({
  receiver: 2,
  id_solicitacao: 1,
  data: "2024-04-03T10:30:00Z",
  message: "Olá via WebSocket!"
}));
```

#### Receber Mensagem
```javascript
ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  if (data.tipo === 'mensagem_recebida') {
    console.log('Mensagem recebida:', data.mensagem);
  }
};
```

---

## 🗂️ Estrutura do Projeto

```
mensagens/
├── server.js                          # Servidor principal (Express + WebSocket)
├── package.json                       # Dependências do projeto
├── .env                              # Variáveis de ambiente (não commitar)
├── .env.example                      # Exemplo de .env
├── .gitignore                        # Arquivos ignorados no Git
│
├── controller/
│   ├── MensagensController.js        # Rotas REST de mensagens
│   └── UserControler.js              # Rotas de usuários (não implementado)
│
├── service/
│   ├── MensagensService.js           # Lógica de negócio de mensagens
│   ├── TokenService.js               # Geração e validação de JWT
│   └── UserService.js                # Buscas de usuários no MySQL
│
├── models/
│   ├── mongodbmodels/
│   │   └── MensagensSchema.js        # Schema MongoDB para mensagens
│   └── mysqlmodels/
│       ├── User.js                   # Model MySQL de usuários
│       ├── Course.js                 # Model MySQL de cursos
│       ├── Gender.js                 # Model MySQL de gêneros
│       └── UserType.js               # Model MySQL de tipos de usuário
│
├── database/
│   ├── dbMongodb.js                  # Conexão MongoDB
│   └── dbMysql.js                    # Conexão MySQL (Sequelize)
│
└── pages/
    ├── motorista.html                # Interface driver (não usada)
    └── passageiro.html               # Interface passenger (não usada)
```

---

## 🔐 Segurança - Análise e Correções

### ✅ Problemas Identificados e Corrigidos

| Problema | Severidade | Status | Ação |
|----------|-----------|--------|------|
| **Importação falsa** `sequelize/lib/utils` não usada | 🔴 Alto | ✅ CORRIGIDO | Removida do `server.js` |
| **body-parser v2.2.0** versão suspeita/falsa | 🔴 Crítico | ✅ CORRIGIDO | Downgrade para `^1.20.2` |
| **express v5.1.0** beta/experimental | 🟡 Médio | ✅ CORRIGIDO | Downgrade para `^4.18.2` |
| **MongoDB hardcoded** sem variável de ambiente | 🟡 Médio | ✅ CORRIGIDO | Usar `MONGODB_URI` do `.env` |
| **JWT Secret fraco** "my-secret-key-from-video" | 🔴 Alto | ✅ CORRIGIDO | Placeholder com instruções |
| **Credenciais MySQL padrão** (root/root) | 🔴 Alto | ⚠️ MANUAL | Configure em `.env` |
| **Sem Helmet** para headers HTTP security | 🟡 Médio | ⚠️ RECOMENDADO | Instale helmet |
| **Sem validação de input** nas rotas | 🔴 Crítico | ⚠️ RECOMENDADO | Use `joi` ou `zod` |
| **Sem rate limiting** contra abuso | 🟡 Médio | ⚠️ RECOMENDADO | Use `express-rate-limit` |

---

## 🚀 Melhorias Recomendadas

### 1. **Adicione Helmet para Headers de Segurança**
```bash
npm install helmet
```

```javascript
// No server.js (depois de criar app)
const helmet = require('helmet');
app.use(helmet());
```

### 2. **Adicione Validação de Input**
```bash
npm install joi
```

```javascript
// Em MensagensController.js
const Joi = require('joi');

const messageSchema = Joi.object({
  receiver: Joi.number().required(),
  id_solicitacao: Joi.number().required(),
  data: Joi.date().required(),
  message: Joi.string().min(1).max(5000).required(),
});

router.post("/", checkToken, async (req, res) => {
  const { error, value } = messageSchema.validate(req.body);
  if (error) return res.status(400).json({ error: error.details });
  // ... resto do código
});
```

### 3. **Adicione Rate Limiting**
```bash
npm install express-rate-limit
```

```javascript
// No server.js
const rateLimit = require('express-rate-limit');

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutos
  max: 100 // 100 requisições por IP
});

app.use(limiter);
```

### 4. **Adicione Logging e Monitoring**
```bash
npm install morgan winston
```

### 5. **Aumente Expiração do JWT**
```javascript
// No TokenService.js
const token = jwt.sign(payload, process.env.SECRET, {
  expiresIn: "7d" // Aumentado para 7 dias
})
```

### 6. **Adicione HTTPS em Produção**
Configure um proxy reverso (nginx) ou use `express.js` com certificados SSL.

### 7. **Validações de Email/Telefone**
Adicione validações mais robustas para os modelos de usuário.

---

## 🧪 Testes

Nenhum teste automatizado foi configurado ainda. 

**Recomendado:**
```bash
npm install --save-dev jest supertest
```

### Exemplo de teste simples:
```javascript
// tests/messages.test.js
const request = require('supertest');
const app = require('../server');

describe('POST /api/messages', () => {
  it('Deve retornar 403 sem token', async () => {
    const response = await request(app).post('/api/messages');
    expect(response.status).toBe(403);
  });
});
```

---

## 🐛 Troubleshooting

### Erro: "Token ausente" no WebSocket
- Verifique se está passando o token no header `sec-websocket-protocol`
- Formato correto: `sec-websocket-protocol: seu_jwt_token`

### Erro: "ECONNREFUSED 127.0.0.1:27017" (MongoDB)
- MongoDB não está rodando. Inicie: `mongod`
- Ou altere `MONGODB_URI` em `.env` para apontar para o servidor remoto

### Erro: "Usuario não encontrado"
- Usuário não existe no MySQL
- Verifique a tabela `users` e o campo `id_usuario`

### Erro: "Sequelize é indefinido"
- A importação do `dbMysql.js` pode estar falhando
- Verifique configuração MySQL em `.env`

---

## 📚 Dependências do Projeto

| Pacote | Versão | Propósito |
|--------|--------|----------|
| **express** | ^4.18.2 | Framework web |
| **ws** | ^8.18.3 | WebSocket server |
| **mongoose** | ^8.19.2 | ODM MongoDB |
| **sequelize** | ^6.37.7 | ORM MySQL |
| **mysql2** | ^3.15.3 | Driver MySQL |
| **jsonwebtoken** | ^9.0.2 | Autenticação JWT |
| **cors** | ^2.8.5 | CORS middleware |
| **dotenv** | ^17.2.3 | Variáveis de ambiente |
| **body-parser** | ^1.20.2 | Parse JSON/form data |
| **helmet** | ^7.1.0 | Headers HTTP security |

---

## 📝 Variáveis de Ambiente

Crie um arquivo `.env` na raiz com:

```env
# 🔐 JWT - Geração: Use: crypto.randomBytes(32).toString('hex')
SECRET=sua_chave_jwt_super_secreta_aqui_min_32_caracteres

# 🗄️ Banco de Dados MongoDB
MONGODB_URI=mongodb://localhost/chat
# Ou remoto: mongodb+srv://usuario:senha@cluster.mongodb.net/chat?retryWrites=true

# 🗄️ Banco de Dados MySQL
DATABASEMYSQL=backendFatecCarona
USERMYSQL=root
PASSWORDMYSQL=sua_senha_mysql_segura

# 🚀 Servidor
PORT_REST=9000
NODE_ENV=production  # ou 'development'
```

---

## 🤝 Contribuindo

1. Faça um fork
2. Crie uma branch (`git checkout -b feature/sua-feature`)
3. Commit suas mudanças (`git commit -m 'Add feature'`)
4. Push para a branch (`git push origin feature/sua-feature`)
5. Abra um Pull Request

---

## 📞 Suporte

Para dúvidas ou problemas:
- 📧 Email: seu-email@fatec.com.br
- 🐛 Issues: Abra uma issue no GitHub
- 💬 Discord: Acesse nosso servidor

---

## 📄 Licença

ISC © 2024 FatecRide

---

## 🗺️ Roadmap

- [ ] Implementar testes unitários com Jest
- [ ] Adicionar autenticação OAuth2
- [ ] Implementar criptografia de ponta a ponta
- [ ] Dashboard de administração
- [ ] Suporte a anexos/imagens
- [ ] Integração com Firebase Notifications
- [ ] Deploy em Docker
- [ ] CI/CD com GitHub Actions

---

**Última atualização:** 3 de abril de 2026  
**Versão:** 1.0.0
