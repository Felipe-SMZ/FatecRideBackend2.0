# API de Mensagens — Frontend

Este documento descreve como o frontend deve usar as APIs REST e o WebSocket do serviço de mensagens.
Todas as chamadas REST exigem o header `Authorization: Bearer <TOKEN>`.

Base REST: `http://<HOST>:9000/api/messages`
WebSocket: `ws://<HOST>:9000` (o token é passado como subprotocol/segundo parâmetro)

Formato de autenticação
- Header REST: `Authorization: Bearer <JWT>`
- WebSocket: instanciar `new WebSocket(url, token)` onde `token` é o JWT (somente o token, sem "Bearer ")

Endpoints REST

1) Enviar mensagem (fallback)
- POST `/api/messages`
- Body JSON: `{ "receiver": 2, "message": "Olá", "id_solicitacao": 1, "data": "2025-11-18T12:00:00Z" }`
- Headers: `Authorization: Bearer <TOKEN>`
- Resposta: `201` com o documento Mongo salvo (contém `_id`, `id_sender`, `id_receiver`, `data`, `message`, `lida`).

2) Listar conversas do usuário
- GET `/api/messages/conversations`
- Query: nenhuma
- Retorno: `200` com array de itens `{ partnerId, lastMessage, unreadCount }` ordenado por `lastMessage.data` desc.

3) Histórico entre você e outro usuário
- GET `/api/messages/with/:userId?page=1&limit=50`
- Retorno: `200` com lista de mensagens ordenadas por data asc (paginado).

4) Marcar mensagem como lida
- PATCH `/api/messages/:messageId/read`
- Retorno: `200` com o documento atualizado.

Formato de mensagens (modelo)
- `_id`: string (ObjectId)
- `id_sender`: number
- `id_receiver`: number
- `id_solicitacao`: number
- `data`: ISO string
- `message`: texto
- `lida`: boolean

Exemplos de uso (frontend)

- Enviar via fetch (REST):
```javascript
fetch('http://localhost:3000/api/messages', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
  },
  body: JSON.stringify({ receiver: 2, message: 'Olá', id_solicitacao: 1, data: new Date().toISOString() })
}).then(r => r.json()).then(console.log);
```

- Conectar WebSocket e enviar mensagem (cliente nativo):
```javascript
const ws = new WebSocket('ws://localhost:9000', token);

ws.onopen = () => {
  // enviar mensagem em tempo real
  ws.send(JSON.stringify({ receiver: 2, message: 'Olá via WS', id_solicitacao: 1, data: new Date().toISOString() }));
};

ws.onmessage = (evt) => {
  const data = JSON.parse(evt.data);
  console.log('evento WS:', data);
};
```

Notas e recomendações
- Prefira WebSocket para chat em tempo real; REST funciona como fallback e para carregamento inicial/histórico.
- Sempre pagine o histórico (`page` + `limit`) para evitar carregar muitas mensagens.
- Assegure que o token JWT seja renovado antes de expirar (o backend rejeitará tokens expirados).

Se precisar, eu gero uma página HTML de exemplo para testar o frontend com essas rotas e o WebSocket.
