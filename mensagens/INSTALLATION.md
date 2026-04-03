# 🚀 Guia de Instalação Completo - Mensagens API

Este guia detalha como configurar e executar a API de Mensagens do FatecRide em diferentes ambientes.

---

## ⚡ Instalação Rápida (5 minutos)

### 1. Pré-requisitos
```bash
# Verificar versão Node.js (deve ser 16+)
node --version

# Verificar npm
npm --version
```

### 2. Clone e Instale
```bash
cd mensagens
npm install
```

### 3. Configure o Ambiente
```bash
cp .env.example .env
# Edite .env com seus dados
```

### 4. Inicie o Servidor
```bash
node server.js
```

✅ **Pronto!** Servidor rodando em `http://localhost:9000`

---

## 📋 Instalação Detalhada

### Passo 1: Instalar Dependências do Sistema

#### Windows
```powershell
# Instalar Node.js (https://nodejs.org)
# Instalar MongoDB (https://www.mongodb.com/try/download/community)
# Instalar MySQL (https://dev.mysql.com/downloads/mysql/)
```

#### macOS (com Homebrew)
```bash
brew install node mongodb-community mysql
```

#### Linux (Ubuntu/Debian)
```bash
# Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# MongoDB
curl https://www.mongodb.org/static/pgp/server-6.0.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/6.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-6.0.list
sudo apt-get update
sudo apt-get install -y mongodb-org

# MySQL
sudo apt-get install -y mysql-server
```

---

### Passo 2: Verificar Instalação

```bash
node --version       # v18.x.x ou superior
npm --version        # 9.x.x ou superior
mongod --version     # 6.0 ou superior
mysql --version      # 8.0 ou superior
```

---

### Passo 3: Iniciar Serviços

#### MongoDB

**Windows:**
```powershell
mongod --dbpath "C:\data\db"
```

**macOS/Linux:**
```bash
brew services start mongodb-community
# ou
sudo systemctl start mongod
```

#### MySQL

**Windows:**
```powershell
# MySQL já inicia como serviço
# Verificar: services.msc
```

**macOS/Linux:**
```bash
brew services start mysql
# ou
sudo systemctl start mysql
```

---

### Passo 4: Preparar Banco de Dados

#### MongoDB
```bash
# Conectar ao MongoDB
mongosh
# ou
mongo

# Listar bancos
show databases

# Usar/criar banco
use chat

# Verificar coleções
show collections

# Sair
exit
```

#### MySQL
```bash
# Conectar ao MySQL
mysql -u root -p

# Digitar senha (padrão: vazio, só pressione Enter)

# Criar banco se não existir
CREATE DATABASE IF NOT EXISTS backendFatecCarona;

# Usar banco
USE backendFatecCarona;

# Ver tabelas
SHOW TABLES;

# Sair
EXIT;
```

---

### Passo 5: Clonar Repositório

```bash
# Clone
git clone https://github.com/seu-usuario/mensagens.git
cd mensagens

# Ou se já tiver baixado
cd /caminho/para/mensagens
```

---

### Passo 6: Instalar Dependências Node.js

```bash
npm install
npm list                    # Verificar instalação
```

**Output esperado:**
```
mensagens@1.0.0
├── body-parser@1.20.2
├── cors@2.8.5
├── dotenv@17.2.3
├── express@4.18.2
├── helmet@7.1.0
├── jsonwebtoken@9.0.2
├── mongoose@8.19.2
├── mysql2@3.15.3
├── sequelize@6.37.7
└── ws@8.18.3
```

---

### Passo 7: Configurar Variáveis de Ambiente

#### Criar arquivo `.env`
```bash
cp .env.example .env
```

#### Editar `.env`

Abra o arquivo `.env` e configure:

```env
# 🔐 JWT Secret (MUDE ISSO!)
# Gere com: node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
SECRET=sua_chave_super_secreta_aqui_min_32_caracteres

# 🗄️ MongoDB
MONGODB_URI=mongodb://localhost/chat

# 🗄️ MySQL
DATABASEMYSQL=backendFatecCarona
USERMYSQL=root
PASSWORDMYSQL=sua_senha_mysql

# 🚀 Servidor
PORT_REST=9000
NODE_ENV=development
```

**Gerar JWT Secret Seguro:**
```bash
# No terminal
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"

# Copie o resultado no .env
# Exemplo de resultado: 
# a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f
```

---

### Passo 8: Criar Usuários de Teste (Opcional)

#### Em MySQL
```sql
-- Conectar
mysql -u root -p

-- Usar banco
USE backendFatecCarona;

-- Ver estrutura de usuários
DESCRIBE users;

-- Inserir usuários de teste
INSERT INTO users (nome, email, telefone, tipo_usuario) 
VALUES 
  ('João Driver', 'joao@test.com', '11999999999', 1),
  ('Maria Passenger', 'maria@test.com', '11988888888', 2);

-- Verificar
SELECT * FROM users;
```

#### Em MongoDB
```bash
mongosh
use chat

db.mensagens.insertOne({
  id_sender: 1,
  id_receiver: 2,
  id_solicitacao: 1,
  data: new Date(),
  data_atualizacao: null,
  message: "Teste de mensagem",
  lida: false
})

db.mensagens.find().pretty()
```

---

### Passo 9: Testar Conexões

```bash
# Teste MongoDB
node -e "
const mongoose = require('mongoose');
mongoose.connect(process.env.MONGODB_URI)
  .then(() => console.log('✅ MongoDB OK'))
  .catch(e => console.log('❌ MongoDB ERRO:', e.message))
"

# Teste MySQL
node -e "
const { sequelize } = require('./database/dbMysql');
sequelize.authenticate()
  .then(() => console.log('✅ MySQL OK'))
  .catch(e => console.log('❌ MySQL ERRO:', e.message))
"
```

---

### Passo 10: Iniciar Servidor

```bash
# Desenvolvimento (com logs detalhados)
node server.js

# SAÍDA ESPERADA:
# ✅ Conexão MongoDB estabelecida com sucesso
# tudo certo
# Server (REST + WS) listening on port 9000
```

✅ **Pronto!** O servidor está rodando.

---

## 🧪 Testar a API

### 1. Obter Token JWT

```bash
# Em outro terminal, crie um token
node -e "
const jwt = require('jsonwebtoken');
require('dotenv').config();

const token = jwt.sign(
  { sub: 1 },  // user ID = 1
  process.env.SECRET,
  { expiresIn: '1h' }
);

console.log('TOKEN:', token);
"
```

### 2. Testar POST (Enviar Mensagem)

```bash
# Substitua 'SEU_TOKEN' pelo token do passo anterior
curl -X POST http://localhost:9000/api/messages \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "receiver": 2,
    "id_solicitacao": 1,
    "data": "2024-04-03T10:30:00Z",
    "message": "Olá!"
  }'
```

### 3. Testar GET (Conversas)

```bash
curl -X GET http://localhost:9000/api/messages/conversations \
  -H "Authorization: Bearer SEU_TOKEN"
```

### 4. Testar WebSocket

```bash
# Instalar wscat globalmente
npm install -g wscat

# Conectar
wscat -c ws://localhost:9000 -H "sec-websocket-protocol: SEU_TOKEN"

# Enviar mensagem (dentro do wscat)
{"receiver": 2, "id_solicitacao": 1, "data": "2024-04-03T10:30:00Z", "message": "Teste WS"}
```

---

## 🐳 Instalação com Docker (Opcional)

### Criar Dockerfile

```dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --only=production

COPY . .

EXPOSE 9000

CMD ["node", "server.js"]
```

### Criar docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "9000:9000"
    environment:
      - MONGODB_URI=mongodb://mongo:27017/chat
      - DATABASEMYSQL=backendFatecCarona
      - USERMYSQL=root
      - PASSWORDMYSQL=mysqlroot
      - SECRET=seu_secret_aqui
      - NODE_ENV=production
    depends_on:
      - mongo
      - mysql

  mongo:
    image: mongo:6
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

  mysql:
    image: mysql:8
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=mysqlroot
      - MYSQL_DATABASE=backendFatecCarona
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mongo_data:
  mysql_data:
```

### Executar com Docker

```bash
docker-compose up
```

---

## 🌥️ Deploy em Produção

### Opção 1: Heroku

```bash
# Instalar Heroku CLI
npm install -g heroku

# Login
heroku login

# Criar app
heroku create seu-app-name

# Configurar variáveis
heroku config:set SECRET=sua_chave_secreta
heroku config:set MONGODB_URI=seu_mongodb_uri
heroku config:set NODE_ENV=production

# Deploy
git push heroku main
```

### Opção 2: AWS EC2

```bash
# SSH na instância
ssh -i seu-key.pem ec2-user@seu-ip

# Instalar Node/MongoDB/MySQL
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo bash -
sudo apt-get install nodejs mongodb-org mysql-server

# Clonar e instalar
git clone seu-repo
cd mensagens
npm install

# Usar PM2 para manter vivo
npm install -g pm2
pm2 start server.js --name "mensagens-api"
pm2 startup
pm2 save
```

### Opção 3: DigitalOcean App Platform

1. Conecte seu repositório GitHub
2. Configure variáveis de ambiente
3. Deploy automático

---

## 🛠️ Troubleshooting de Instalação

### Erro: "Cannot find module 'express'"

```bash
# Solução: reinstalar dependências
rm -rf node_modules package-lock.json
npm install
```

### Erro: "ECONNREFUSED 127.0.0.1:27017" (MongoDB)

```bash
# Solução 1: Iniciar MongoDB
mongod

# Solução 2: Usar MongoDB remoto
MONGODB_URI=mongodb+srv://usuario:senha@cluster.mongodb.net/chat
```

### Erro: "Access denied for user 'root'@'localhost'"

```bash
# Solução: Resetar senha MySQL
mysql -u root
ALTER USER 'root'@'localhost' IDENTIFIED BY 'nova_senha';
FLUSH PRIVILEGES;
EXIT;

# Depois atualizar .env
PASSWORDMYSQL=nova_senha
```

### Erro: "Port 9000 already in use"

```bash
# Solução 1: Mudar porta
PORT_REST=9001

# Solução 2: Matar processo na porta
# Windows
netstat -ano | findstr :9000
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:9000 | xargs kill -9
```

---

## ✅ Checklist de Instalação

- [ ] Node.js 16+ instalado
- [ ] MongoDB rodando
- [ ] MySQL rodando
- [ ] Repositório clonado
- [ ] `npm install` executado
- [ ] `.env` configurado com valores reais
- [ ] `node server.js` funcionando sem erros
- [ ] Teste GET `/api/messages/conversations` retorna 200
- [ ] WebSocket conecta com token válido

---

## 📞 Suporte

Erro não listado? Abra uma issue no GitHub com:
- Sistema operacional
- Versão Node.js
- Erro completo (copy-paste do terminal)
- Comando que rodou antes do erro

