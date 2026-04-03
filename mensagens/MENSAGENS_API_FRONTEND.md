# 📱 Guia de Integração — API de Mensagens (Frontend)

Complete guide for frontend integration with the FatecRide messaging API. This document covers both REST and WebSocket approaches for real-time messaging.

---

## 🔐 Autenticação

All requests require a valid JWT token. There are two ways to send it:

### REST API
```
Authorization: Bearer <JWT_TOKEN>
```

### WebSocket
```javascript
new WebSocket('ws://localhost:9000', [JWT_TOKEN])
```

**Token Format:**
- Obtained from the login endpoint (external service)
- Format: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
- Contains: `{ sub: userId, iss: "...", exp: ... }`
- Expires in 1 hour by default

---

## 🌐 Base URLs

| Ambiente | REST | WebSocket |
|----------|------|-----------|
| Local | `http://localhost:9000` | `ws://localhost:9000` |
| Produção | `https://api.fatecride.com.br` | `wss://api.fatecride.com.br` |

---

## 📡 REST API Endpoints

### 1️⃣ Enviar Mensagem
```http
POST /api/messages
Content-Type: application/json
Authorization: Bearer <TOKEN>

{
  "receiver": 2,
  "id_solicitacao": 1,
  "data": "2024-04-03T10:30:00Z",
  "message": "Olá! Como vai?"
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
    "message": "Olá! Como vai?",
    "lida": false
  }
}
```

---

### 2️⃣ Listar Conversas
```http
GET /api/messages/conversations
Authorization: Bearer <TOKEN>
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

### 3️⃣ Histórico de Mensagens
```http
GET /api/messages/with/2?page=1&limit=50
Authorization: Bearer <TOKEN>
```

**Query Parameters:**
- `page` (default: 1)
- `limit` (default: 50)

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
    }
  ]
}
```

---

### 4️⃣ Marcar como Lida
```http
PATCH /api/messages/507f1f77bcf86cd799439011/read
Authorization: Bearer <TOKEN>
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

## 🔌 WebSocket (Real-time)

### Conectar
```javascript
const token = localStorage.getItem('token'); // ou sessionStorage
const ws = new WebSocket('ws://localhost:9000', [token]);

ws.onopen = () => {
  console.log('WebSocket conectado!');
};

ws.onerror = (error) => {
  console.error('Erro WebSocket:', error);
};
```

### Enviar Mensagem
```javascript
ws.send(JSON.stringify({
  receiver: 2,
  id_solicitacao: 1,
  data: new Date().toISOString(),
  message: "Olá via WebSocket!"
}));
```

### Receber Mensagem
```javascript
ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  
  if (data.tipo === 'mensagem_recebida') {
    // Mensagem recebida de outro usuário
    console.log('Mensagem recebida:', data.mensagem);
    updateChatUI(data.mensagem);
  } 
  else if (data.tipo === 'mensagem_confirmada') {
    // Confirmação de que sua mensagem foi entregue
    console.log('Mensagem entregue:', data.mensagem);
  }
};
```

### Desconectar
```javascript
ws.close();
```

---

## 💻 Exemplos por Framework

### Vanilla JavaScript (Fetch)

#### Enviar Mensagem
```javascript
async function sendMessage(token, receiver, message, solicitacaoId) {
  try {
    const response = await fetch('http://localhost:9000/api/messages', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        receiver,
        message,
        id_solicitacao: solicitacaoId,
        data: new Date().toISOString()
      })
    });

    if (!response.ok) throw new Error('Erro ao enviar mensagem');
    const result = await response.json();
    console.log('Mensagem enviada:', result.data);
    return result.data;
  } catch (error) {
    console.error('Erro:', error);
  }
}
```

#### Listar Conversas
```javascript
async function getConversations(token) {
  try {
    const response = await fetch('http://localhost:9000/api/messages/conversations', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    const result = await response.json();
    console.log('Conversas:', result.data);
    return result.data;
  } catch (error) {
    console.error('Erro:', error);
  }
}
```

---

### Axios
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:9000/api/messages',
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

// Enviar
api.post('/', {
  receiver: 2,
  message: 'Olá',
  id_solicitacao: 1,
  data: new Date().toISOString()
});

// Conversas
api.get('/conversations');

// Histórico
api.get('/with/2?page=1&limit=50');

// Marcar lida
api.patch('/507f1f77bcf86cd799439011/read');
```

---

### React Hook (Custom)
```javascript
import { useEffect, useRef, useState } from 'react';

function useMessagesAPI(token) {
  const [conversations, setConversations] = useState([]);
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const wsRef = useRef(null);

  // Carregar conversas
  useEffect(() => {
    if (!token) return;
    
    setLoading(true);
    fetch('http://localhost:9000/api/messages/conversations', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
      .then(r => r.json())
      .then(data => setConversations(data.data))
      .finally(() => setLoading(false));
  }, [token]);

  // Conectar WebSocket
  useEffect(() => {
    if (!token) return;

    wsRef.current = new WebSocket('ws://localhost:9000', [token]);

    wsRef.current.onopen = () => console.log('WS conectado');
    
    wsRef.current.onmessage = (event) => {
      const data = JSON.parse(event.data);
      if (data.tipo === 'mensagem_recebida') {
        setMessages(prev => [...prev, data.mensagem]);
      }
    };

    wsRef.current.onerror = (error) => console.error('WS erro:', error);

    return () => {
      if (wsRef.current) wsRef.current.close();
    };
  }, [token]);

  const sendMessage = (receiver, message, solicitacaoId) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify({
        receiver,
        message,
        id_solicitacao: solicitacaoId,
        data: new Date().toISOString()
      }));
    }
  };

  return { conversations, messages, loading, sendMessage };
}

// Uso
export function ChatComponent() {
  const token = localStorage.getItem('token');
  const { conversations, messages, sendMessage } = useMessagesAPI(token);

  return (
    <div>
      {conversations.map(conv => (
        <div key={conv.partnerId}>
          <p>{conv.lastMessage.message}</p>
          <span>{conv.unreadCount} não lidas</span>
        </div>
      ))}
    </div>
  );
}
```

---

### Vue 3 Composable
```javascript
import { ref, onMounted, onBeforeUnmount } from 'vue';

export function useMessagesAPI(token) {
  const conversations = ref([]);
  const messages = ref([]);
  const loading = ref(false);
  const ws = ref(null);

  onMounted(async () => {
    if (!token.value) return;

    // Carregar conversas
    loading.value = true;
    const response = await fetch('http://localhost:9000/api/messages/conversations', {
      headers: { 'Authorization': `Bearer ${token.value}` }
    });
    conversations.value = (await response.json()).data;
    loading.value = false;

    // WebSocket
    ws.value = new WebSocket('ws://localhost:9000', [token.value]);
    ws.value.onmessage = (event) => {
      const data = JSON.parse(event.data);
      if (data.tipo === 'mensagem_recebida') {
        messages.value.push(data.mensagem);
      }
    };
  });

  onBeforeUnmount(() => {
    if (ws.value) ws.value.close();
  });

  const sendMessage = (receiver, message, solicitacaoId) => {
    ws.value?.send(JSON.stringify({
      receiver,
      message,
      id_solicitacao: solicitacaoId,
      data: new Date().toISOString()
    }));
  };

  return {
    conversations,
    messages,
    loading,
    sendMessage
  };
}
```

---

## 🛠️ Tratamento de Erros

### REST API
```javascript
async function sendMessageWithErrorHandling(token, receiver, message, solicitacaoId) {
  try {
    const response = await fetch('http://localhost:9000/api/messages', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        receiver, message, id_solicitacao: solicitacaoId,
        data: new Date().toISOString()
      })
    });

    if (response.status === 403) {
      throw new Error('Token inválido ou expirado. Faça login novamente.');
    }
    if (response.status === 400) {
      throw new Error('Usuário receptor não encontrado.');
    }
    if (response.status === 500) {
      throw new Error('Erro no servidor. Tente novamente.');
    }

    return await response.json();
  } catch (error) {
    console.error('Erro:', error.message);
    // Notificar usuário
    showError(error.message);
  }
}
```

### WebSocket
```javascript
const ws = new WebSocket('ws://localhost:9000', [token]);

ws.onerror = (event) => {
  console.error('Erro WebSocket:', event);
  // Reconectar automaticamente
  setTimeout(() => reconnectWebSocket(), 3000);
};

ws.onclose = () => {
  console.warn('WebSocket desconectado');
  // Tentar reconectar
  setTimeout(() => reconnectWebSocket(), 5000);
};
```

---

## 📋 Model (Formato de Mensagem)

```typescript
interface Message {
  _id: string;              // ObjectId do MongoDB
  id_sender: number;        // ID do usuário que enviou
  id_receiver: number;      // ID do usuário que recebe
  id_solicitacao: number;   // ID da carona/solicitação
  data: string;             // ISO 8601 datetime
  data_atualizacao: null;   // Null se não editada
  message: string;          // Conteúdo da mensagem
  lida: boolean;            // Se foi lida
}
```

---

## ✅ Melhores Práticas

### 1. **Sempre Usar Paginação**
```javascript
// ❌ Ruim - carrega tudo
const messages = await api.get(`/with/${userId}`);

// ✅ Bom - pagina de 50 em 50
const messages = await api.get(`/with/${userId}?page=1&limit=50`);
const nextPage = await api.get(`/with/${userId}?page=2&limit=50`);
```

### 2. **Renovar Token Antes de Expirar**
```javascript
setInterval(() => {
  if (shouldRenewToken()) {
    refreshToken();
  }
}, 5 * 60 * 1000); // A cada 5 minutos
```

### 3. **Prefira WebSocket para Real-time**
```javascript
// ❌ Ruim - polling a cada segundo
setInterval(() => fetchMessages(), 1000);

// ✅ Bom - WebSocket para notificações instantâneas
ws.onmessage = (event) => updateChat(JSON.parse(event.data));
```

### 4. **Cache de Conversas**
```javascript
const cache = new Map();

async function getConversationsWithCache(token) {
  if (cache.has('conversations')) {
    return cache.get('conversations');
  }
  
  const convs = await fetchConversations(token);
  cache.set('conversations', convs);
  return convs;
}
```

### 5. **Tratamento de Desconexão**
```javascript
let reconnectAttempts = 0;
const MAX_RECONNECTS = 5;

function connectWebSocket(token) {
  ws = new WebSocket('ws://localhost:9000', [token]);
  
  ws.onopen = () => {
    reconnectAttempts = 0; // Reset counter
  };
  
  ws.onclose = () => {
    if (reconnectAttempts < MAX_RECONNECTS) {
      reconnectAttempts++;
      setTimeout(() => connectWebSocket(token), Math.pow(2, reconnectAttempts) * 1000);
    } else {
      alert('Falha na conexão. Recarregue a página.');
    }
  };
}
```

---

## 🐛 Troubleshooting

### "Token inválido ou expirado"
- Não renovou o token a tempo
- Solução: Implemente refresh token automático

### "CORS error"
- Frontend em porta diferente do backend
- Solução: Backend já tem CORS habilitado para `localhost:3000` e `localhost:3001`

### WebSocket desconecta constantemente
- Proxy/firewall bloqueando WebSocket
- Solução: Use fallback para REST polling

### Mensagem fica presa em "enviando"
- WebSocket desconectado
- Solução: Detectar desconexão e reconectar automaticamente

---

## 📚 Endpoints Rápidos

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/messages` | Enviar mensagem |
| GET | `/api/messages/conversations` | Listar conversas |
| GET | `/api/messages/with/:userId` | Histórico com usuário |
| PATCH | `/api/messages/:id/read` | Marcar como lida |

---

**Última atualização:** 3 de abril de 2026  
**Versão:** 2.0.0
