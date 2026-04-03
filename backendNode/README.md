# FatecRide Backend 2.0

API backend para plataforma de caronas FATEC, desenvolvida com Node.js e Express. Sistema de cadastro e gerenciamento de anúncios de empresas/estabelecimentos com autenticação JWT.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias](#tecnologias)
- [Análise de Segurança](#-análise-de-segurança)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Uso da API](#uso-da-api)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Endpoints](#endpoints)
- [Recomendações](#recomendações)

---

## Sobre o Projeto

Este é um backend Node.js que gerencia anunciantes (empresas) em uma plataforma de carona. O projeto implementa:

- ✅ Autenticação via JWT
- ✅ Cadastro e login de anunciantes
- ✅ Gestão de dados de anúncios
- ✅ CORS configurado
- ✅ Swagger/OpenAPI para documentação
- ✅ Hash de senhas com bcrypt
- ✅ Persistência em MongoDB

---

## Tecnologias

```json
{
  "runtime": "Node.js",
  "framework": "Express.js",
  "database": "MongoDB + Mongoose",
  "auth": "JWT (jsonwebtoken)",
  "security": "bcrypt",
  "documentation": "Swagger/OpenAPI",
  "cors": "cors package"
}
```

### Dependências Instaladas

| Pacote | Versão | Proposito |
|--------|--------|----------|
| `express` | ^5.1.0 | Framework web |
| `mongoose` | ^8.19.1 | ODM MongoDB |
| `jsonwebtoken` | ^9.0.2 | Autenticação JWT |
| `bcrypt` | ^6.0.0 | Hash de senhas |
| `cors` | ^2.8.5 | CORS handling |
| `body-parser` | ^2.2.0 | Parsing de requisições |
| `dotenv` | ^17.2.3 | Variáveis de ambiente |
| `swagger-ui-express` | ^5.0.1 | UI Swagger |

---

## 🚨 ANÁLISE DE SEGURANÇA

### ✅ Pontos Fortes

1. **Hash de Senhas**: Implementação correta com bcrypt (SALT_ROUNDS = 10)
2. **JWT para Autenticação**: Token com expiração de 1 hora
3. **Verificação de Token**: Middleware `checkToken()` em endpoints protegidos
4. **Remoção de Senhas**: Dados sensíveis removidos antes de retornar ao cliente
5. **Estrutura em Camadas**: Separação entre Controller, Service e Model
6. **Validação Básica**: Verificação de email duplicado no cadastro

---

### ⚠️ PROBLEMAS CRÍTICOS DE SEGURANÇA

#### 🔴 **CRÍTICO #1: Dependência Falsa - body-parser 2.2.0**
**Problema**: A versão `2.2.0` de `body-parser` **NÃO EXISTE** na npm.
- Última versão estável é `1.20.x`
- Versão `2.2.0` pode ser um pacote malicioso/comprometido
- **Risco**: Execução de código malicioso, roubo de dados, backdoor

**Ação Imediata**:
```bash
npm list body-parser
npm uninstall body-parser
npm install body-parser@latest
# Depois verificar package-lock.json
```

#### 🔴 **CRÍTICO #2: express 5.1.0 (Versão Beta)**
**Problema**: Express 5.x está em desenvolvimento, não é recomendado para produção
- Versão produção estável: `4.18.x` ou superior
- Pode conter bugs e mudanças incompatíveis

**Solução**:
```bash
npm uninstall express
npm install express@4.18.2 --save
```

#### 🟠 **ALTA #1: SECRET Hardcoded e Fraco**
**Arquivo**: `.env`
```
SECRET=my-secret-key-from-video  ❌ INSEGURO
```

**Problemas**:
- Chave muito simples (presente em vídeo/público)
- Exposta no repositório
- Sem rotação

**Solução**:
```bash
# Gerar nova chave segura (minimo 32 caracteres)
openssl rand -base64 32
# ou em PowerShell:
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

Resultado:
```
SECRET=qNqN7S9p2K8mL4j6H0w5x3Y8Z2a9b1c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9
```

#### 🟠 **ALTA #2: CORS Muito Aberto**
**Arquivo**: `app.js`
```javascript
app.use(cors({
    origin:"*",  // ❌ Permite qualquer origem
    methods:["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"],
    allowedHeaders:["Content-Type", "Authorization", "Origin", "Accept"]
}))
```

**Problema**: Qualquer site pode fazer requisições para sua API

**Solução** - Configurar whitelist:
```javascript
const allowedOrigins = [
  'http://localhost:3000',
  'http://localhost:3001',
  'https://seu-frontend.com',
  'https://app.seu-dominio.com'
];

app.use(cors({
  origin: (origin, callback) => {
    if (!origin || allowedOrigins.includes(origin)) {
      callback(null, true);
    } else {
      callback(new Error('Origem não permitida'));
    }
  },
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
```

#### 🟠 **ALTA #3: JWT Secret Logado no Console**
**Arquivo**: `service/TokenService.js`
```javascript
const gerarToken = (payload)=>{  
    console.log("JWT_SECRET:", process.env.SECRET);  // ❌ Exposição!
    const token = jwt.sign(payload,process.env.SECRET,{expiresIn:"1h"})
    return token
}
```

**Problema**: Secret pode vazarlogue em log de produção

**Solução**:
```javascript
const gerarToken = (payload)=>{  
    // Remover console.log
    const token = jwt.sign(payload, process.env.SECRET, {expiresIn:"1h"})
    return token
}
```

#### 🟠 **ALTA #4: Sem Helmet (Headers de Segurança)**
**Problema**: API expõe informações sensíveis em headers (X-Powered-By, etc)

**Solução** - Instalar e configurar helmet:
```bash
npm install helmet
```

Adicionar em `app.js`:
```javascript
const helmet = require('helmet');
app.use(helmet());
```

#### 🟡 **MÉDIA #1: Sem Rate Limiting**
**Problema**: Vulnerável a brute force attacks no login

**Solução** - Instalar express-rate-limit:
```bash
npm install express-rate-limit
```

Configurar em Controller:
```javascript
const rateLimit = require('express-rate-limit');

const loginLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutos
  max: 5, // máx 5 tentativas
  message: 'Muitas tentativas de login, tente novamente mais tarde'
});

router.post('/login', loginLimiter, async (req, res) => { ... });
```

#### 🟡 **MÉDIA #2: Falta Validação de Entrada**
**Arquivo**: `controller/AnuncioController.js`, `service/AnuncioService.js`

**Problemas**:
- Sem validação de email (format, RFC)
- Sem validação de CNPJ
- Sem validação de telefone
- Sem sanitização de input

**Solução** - Instalar validator:
```bash
npm install joi
# ou
npm install validator
```

Exemplo com `joi`:
```javascript
const Joi = require('joi');

const anuncioSchema = Joi.object({
  nome_dono: Joi.string().required().min(3).max(100),
  email: Joi.string().email().required(),
  cnpj: Joi.string().pattern(/^\d{2}\.\d{3}\.\d{3}\/0001-\d{2}$/).required(),
  contato: Joi.string().pattern(/^\(\d{2}\)\s\d{4,5}-\d{4}$/).required(),
  senha: Joi.string().required().min(8).pattern(/[A-Z]/).pattern(/[0-9]/).pattern(/[!@#$%^&*]/),
  quantidade_alcance: Joi.number().integer().min(1).max(1000)
});
```

#### 🟡 **MÉDIA #3: Sem Proteção contra NoSQL Injection**
**Problema**: Entrada do usuário passada diretamente para queries

**Solução**: Mongoose já protege com schemas, mas adicionar validação explícita:
```javascript
// Usar sempre selectivo de campos
AnuncioModel.findOne({ email: payload.email }).lean();
// Não fazer queries dinâmicas com input direto
```

#### 🟡 **MÉDIA #4: Sem Logging e Monitoramento**
**Problema**: Sem registros de erros e ações suspeitas

**Solução** - Instalar winston ou bunyan:
```bash
npm install winston
```

---

## Instalação

### Pré-requisitos

- Node.js v16+ 
- npm ou yarn
- MongoDB rodando localmente (ou URI de conexão remota)

### Passos

1. **Clone o repositório** e navegue até o diretório:
```bash
cd backendNode
```

2. **Instale as dependências**:
```bash
npm install
```

3. **Verifique as vulnerabilidades**:
```bash
npm audit
```

---

## Configuração

### 1. Variáveis de Ambiente

Crie arquivo `.env` na raiz (já existe, mas revise):

```env
# SEGURANÇA - USE UMA CHAVE FORTE!
SECRET=qNqN7S9p2K8mL4j6H0w5x3Y8Z2a9b1c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9

# BANCO DE DADOS
MONGODB_URI=mongodb://localhost/backendfatecnode
# Para MongoDB Atlas:
# MONGODB_URI=mongodb+srv://user:password@cluster.mongodb.net/backendfatecnode

# AMBIENTE
NODE_ENV=development
PORT=8081
```

### 2. Banco de Dados

Se usando MongoDB local:
```bash
# Inicie MongoDB (Windows/PowerShell)
mongod

# Ou com Docker
docker run -d --name mongodb -p 27017:27017 mongo
```

Se usando MongoDB Atlas:
1. Crie cluster em [mongodb.com](https://www.mongodb.com)
2. Crie usuário com acesso
3. Copie connection string para `.env`

---

## Uso da API

### Iniciar Servidor

```bash
npm start
# ou
node app.js
```

Saída esperada:
```
servidor rodando na porta 8081
ok conectado
```

### Acessar Documentação

Swagger disponível em:
```
http://localhost:8081/docs
```

---

## Estrutura do Projeto

```
backendNode/
├── app.js                    # Arquivo principal, configuração Express
├── package.json             # Dependências do projeto
├── .env                     # Variáveis de ambiente (GITIGNORE!)
├── swagger.json            # Documentação OpenAPI
│
├── controller/
│   └── AnuncioController.js # Rotas e middlewares
│
├── service/
│   ├── AnuncioService.js    # Lógica de negócio
│   └── TokenService.js      # Geração e validação JWT
│
├── models/
│   └── AnuncioSchema.js     # Schema MongoDB
│
└── database/
    └── mongo.js            # Conexão MongoDB
```

### Fluxo de Dados

```
HTTP Request → Controller → Service → Model → Database
    ↓
Response com Token/Data
```

---

## Endpoints

### 🔒 Autenticação (Público)

#### `POST /anuncio/login`
Faz login de um anunciante.

**Request:**
```json
{
  "email": "contato@souza-tech.com",
  "senha": "SenhaSegura123!"
}
```

**Response (200):**
```json
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

#### `POST /anuncio`
Cadastra novo anunciante (público).

**Request:**
```json
{
  "nome_dono": "João Silva",
  "logo": "logo.png",
  "razao_social": "Silva Comércio LTDA",
  "nome_fantasia": "Silva Tech",
  "cnpj": "12.345.678/0001-99",
  "ramo_empresa": "Tecnologia",
  "descricao_anuncio": "Serviços de TI",
  "endereco_empresa": "Av. Principal, 100",
  "endereco_dono": "Rua Secundária, 50",
  "contato": "(11) 98765-4321",
  "email": "joao@silva.com",
  "senha": "SenhaForte@123",
  "anuncio": "Promoção de serviços",
  "quantidade_alcance": 50
}
```

**Response (201):**
```json
{
  "message": "criado com sucesso",
  "data": { /* dados sem senha */ }
}
```

---

### 🔐 Protegido por JWT

#### `GET /anuncio/me`
Retorna dados do anunciante logado.

**Header:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200):**
```json
{
  "data": {
    "_id": "507f...",
    "nome_dono": "João Silva",
    "email": "joao@silva.com",
    // ... outros dados
  }
}
```

---

#### `PATCH /anuncio`
Atualiza parcialmente dados do anunciante (autenticado).

**Header:**
```
Authorization: Bearer <token>
```

**Request:**
```json
{
  "nome_fantasia": "Silva Tech Premium",
  "quantidade_alcance": 100
}
```

**Response (200):** Dados atualizados (sem senha)

---

#### `PUT /anuncio`
Atualiza completamente dados do anunciante (autenticado).

**Mesma estrutura de POST /anuncio**

---

#### `GET /anuncio/divulgar`
Retorna anúncio aleatório (público).

**Response (200):**
```json
{
  "_id": "507f...",
  "nome_fantasia": "Silva Tech",
  "ramo_empresa": "Tecnologia",
  "descricao_anuncio": "Serviços de TI",
  // ... sem email e senha
}
```

---

## Recomendações

### 🔧 Ações Imediatas (Semana 1)

- [ ] **Corrigir body-parser** → `npm install body-parser@latest`
- [ ] **Downgrade Express** → `npm install express@4.18.2`
- [ ] **Mudar SECRET** → Gerar chave forte (32+ caracteres)
- [ ] **Remover console.log do TokenService**
- [ ] **Adicionar Helmet** → `npm install helmet` + config

### 🛡️ Melhorias Médias (Semana 2-3)

- [ ] **Implementar Rate Limiting** (express-rate-limit)
- [ ] **Adicionar Validação com Joi** para todos inputs
- [ ] **Configurar CORS whitelist** (domínios conhecidos)
- [ ] **Adicionar Logging** (winston mais tarde)

### 🚀 Melhorias Futuras (Mês 1)

- [ ] Testes unitários (Jest)
- [ ] Testes de integração
- [ ] Monitoramento com APM
- [ ] CI/CD pipeline
- [ ] Documentação de API mais detalhada
- [ ] Versionamento de API (`/v1/anuncio`)

### 📚 Segurança Contínua

- [ ] Executar `npm audit` regularmente
- [ ] Manter dependências atualizadas
- [ ] Revisar logs de acesso
- [ ] Implementar rotação de senha para SECRET
- [ ] Usar HTTPS em produção (npm package `helmet` ajuda)

---

## Checklist de Deploy

Antes de levar para produção:

```
✅ SEGURANÇA
- [ ] Node.js versão LTS
- [ ] Todas as dependências auditadas
- [ ] SECRET com 32+ caracteres aleatórios
- [ ] CORS configurado para domínios específicos
- [ ] Helmet instalado e ativado
- [ ] Rate limiting implementado
- [ ] Validação de entrada em todos endpoints
- [ ] HTTPS ativado (reverse proxy)

✅ BANCO DE DADOS
- [ ] MongoDB com autenticação
- [ ] Backup automático
- [ ] Índices criados em campos frecuentes
- [ ] String de conexão em variável de ambiente

✅ OPERACIONAL
- [ ] Logging centralizado
- [ ] Monitoramento de erros
- [ ] Health check endpoint (`GET /health`)
- [ ] Graceful shutdown implementado
- [ ] Variáveis de ambiente por ambiente
- [ ] .env no .gitignore
```

---

## Troubleshooting

### Erro: `ECONNREFUSED 127.0.0.1:27017`
MongoDB não está rodando.
```bash
# Windows
mongod
# ou com Docker
docker start mongodb
```

### Erro: `body-parser` versão inválida
```bash
npm uninstall body-parser
npm install body-parser@latest
npm audit fix
```

### Token expirado (401)
Token JWT expira em 1 hora. Faça novo login:
```bash
POST /anuncio/login
```

### CORS Error
Adicionar origem à whitelist em `app.js`, seção CORS.

---

## Referências

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Node.js Security Checklist](https://nodejs.org/en/docs/guides/nodejs-security/)
- [Helmet.js Documentation](https://helmetjs.github.io/)
- [MongoDB Security](https://docs.mongodb.com/manual/security/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

## Licença

ISC

---

**Última atualização:** Abril 2026
**Status de Segurança:** ⚠️ Crítico - Requer ações imediatas
