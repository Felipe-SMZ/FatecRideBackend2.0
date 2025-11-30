const express = require("express");
const router = express.Router();
const { sendMessage, getUserConversations, getConversationMessages, markMessageRead } = require("../service/MensagensService");
const jwt = require("jsonwebtoken")


function checkToken(req, res, next) {
  const authHEaderv = req.headers["authorization"];
  console.log(req.headers["authorization"])
  const token = authHEaderv && authHEaderv.split(" ")[1];

  if (!token) {
    return res.status(403).json({ message: "acesso negado" });
  }

  try {
    const decoded = jwt.verify(token, process.env.SECRET);
    // anexa id do usuário no request para as rotas seguintes
    req.userId = decoded.sub;
    next();
  } catch (error) {
    console.log(error)
    res.status(400).json({ message: "token invalido" });
  }
}

router.post("/", checkToken, async (req, res) => {
  const message = {
    receiver: req.body.receiver,
    data: req.body.data,
    message: req.body.message,
  };

  console.log(message)
  const results = await sendMessage(req.headers["authorization"].split(" ")[1], message);
  if (results.sucesso) {
    res.status(results.status_code).json({ data: results.data });
  } else {
    res.status(results.status_code).json({ error: results.message });
  }
});

// Lista conversas do usuário autenticado
router.get("/conversations", checkToken, async (req, res) => {
  const conversations = await getUserConversations(req.userId);
  res.status(200).json({ success: true, data: conversations });
});

// Histórico de mensagens entre o usuário autenticado e outro usuário
router.get("/with/:userId", checkToken, async (req, res) => {
  const page = parseInt(req.query.page) || 1;
  const limit = parseInt(req.query.limit) || 50;
  const partnerId = Number(req.params.userId);
  const messages = await getConversationMessages(req.userId, partnerId, page, limit);
  res.status(200).json({ success: true, data: messages });
});

// Marcar uma mensagem como lida
router.patch("/:messageId/read", checkToken, async (req, res) => {
  const result = await markMessageRead(req.params.messageId, req.userId);
  if (result.success) {
    res.status(result.status_code).json({ success: true, data: result.data });
  } else {
    res.status(result.status_code).json({ success: false, message: result.message });
  }
});

module.exports = router;
