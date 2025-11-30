require('dotenv').config();
const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');

// Service centralizado: toda lógica de negócio fica aqui
const anuncioService = require('../service/AnuncioService');
const TokenService = require('../service/TokenService');
const AnuncioModel = require('../models/AnuncioSchema'); // fallback para leituras diretas

/**
 * Normaliza nomes de campos enviados pelo cliente.
 * Aceita tanto campos com acento (ex: 'endereço_empresa') quanto sem (ex: 'endereco_empresa').
 * Isso evita erros de validação quando clientes enviam chaves inconsistentes.
 */
function normalizeAnuncioPayload(raw = {}) {
  return {
    nome_dono: raw.nome_dono ?? raw['nome_dono'] ?? raw['nome dono'],
    logo: raw.logo ?? raw['logo'],
    razao_social: raw.razao_social ?? raw['razao_social'] ?? raw['razao social'],
    nome_fantasia: raw.nome_fantasia ?? raw['nome_fantasia'] ?? raw['nome fantasia'],
    cnpj: raw.cnpj ?? raw['cnpj'],
    ramo_empresa: raw.ramo_empresa ?? raw['ramo_empresa'] ?? raw['ramo empresa'],
    descricao_anuncio: raw.descricao_anuncio ?? raw['descricao_anuncio'] ?? raw['descricao anuncio'],
    endereco_empresa: raw.endereco_empresa ?? raw['endereço_empresa'] ?? raw['endereco empresa'] ?? raw['endereço empresa'],
    endereco_dono: raw.endereco_dono ?? raw['endereço_dono'] ?? raw['endereco dono'] ?? raw['endereço dono'],
    contato: raw.contato ?? raw['contato'],
    email: raw.email ?? raw['email'],
    senha: raw.senha ?? raw['senha'],
    anuncio: raw.anuncio ?? raw['anuncio'],
    quantidade_alcance: (typeof raw.quantidade_alcance !== 'undefined') ? raw.quantidade_alcance : (typeof raw['quantidade_alcance'] !== 'undefined' ? raw['quantidade_alcance'] : raw.quantidadeAlcance),
  };
}

/**
 * Middleware simples para validar token presente no header Authorization.
 */
function checkToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.status(403).json({ message: 'acesso negado' });
  try {
    jwt.verify(token, process.env.SECRET);
    return next();
  } catch (err) {
    return res.status(401).json({ message: 'token inválido' });
  }
}

/**
 * POST /anuncio/login
 * Recebe { email, senha } e delega ao service. Retorna token em caso de sucesso.
 */
router.post('/login', async (req, res) => {
  try {
    const body = { email: req.body.email, senha: req.body.senha };
    const results = await anuncioService.login(body);
    if (results.sucesso) return res.status(results.status_code).json({ data: results.data });
    return res.status(results.status_code).json({ message: results.message });
  } catch (err) {
    console.error('POST /anuncio/login error:', err);
    return res.status(500).json({ message: err.message });
  }
});

/**
 * POST /anuncio
 * Cria um anunciante. Valida e delega ao service.
 */
router.post('/', async (req, res) => {
  try {
    const payload = normalizeAnuncioPayload(req.body);
    const results = await anuncioService.cadastrar(payload);
    if (results.sucesso) return res.status(results.status_code).json({ message: 'criado com sucesso', data: results.data });
    return res.status(results.status_code).json({ error: results.message });
  } catch (err) {
    console.error('POST /anuncio error:', err);
    return res.status(500).json({ message: err.message });
  }
});

/**
 * GET /anuncio/divulgar
 * Retorna um anúncio público (aleatório) para divulgação.
 */
router.get('/divulgar', async (req, res) => {
  try {
    const results = await anuncioService.divulgar();
    if (results.sucesso) return res.status(results.status_code).json(results.data);
    return res.status(results.status_code).json({ message: results.message });
  } catch (err) {
    console.error('GET /anuncio/divulgar error:', err);
    return res.status(500).json({ message: err.message });
  }
});

/**
 * GET /anuncio/me
 * Retorna os dados do anunciante autenticado (remove a senha da resposta).
 */
router.get('/me', checkToken, async (req, res) => {
  try {
    const token = req.headers['authorization'].split(' ')[1];

    // Decodifica o payload do token — preferir TokenService quando disponível
    let payload = null;
    try {
      payload = TokenService && TokenService.decodificarToken ? TokenService.decodificarToken(token) : jwt.verify(token, process.env.SECRET);
    } catch (err) {
      console.error('Erro ao decodificar token:', err);
      return res.status(401).json({ message: 'token inválido' });
    }

    // Se o service expõe um getter, use-o; caso contrário, faça fallback para o model
    try {
      if (typeof anuncioService.getAnunciante === 'function') {
        const r = await anuncioService.getAnunciante(token);
        if (r.sucesso === false) return res.status(r.status_code || 400).json({ message: r.message });
        const data = r.data ?? r;
        if (data && data.senha) delete data.senha;
        return res.status(200).json({ data });
      }
    } catch (svcErr) {
      console.warn('anuncioService.getAnunciante falhou, fallback para model:', svcErr);
    }

    // Fallback direto ao model
    const user = await AnuncioModel.findById(payload.id).lean();
    if (!user) return res.status(404).json({ message: 'Usuário não encontrado' });
    if (user.senha) delete user.senha;
    return res.status(200).json({ data: user });
  } catch (err) {
    console.error('GET /anuncio/me error:', err);
    if (err.name === 'JsonWebTokenError' || err.name === 'TokenExpiredError') return res.status(401).json({ message: 'token inválido' });
    return res.status(500).json({ message: err.message });
  }
});

/**
 * PATCH /anuncio
 * Atualização parcial: aceita somente os campos enviados.
 * Se o service tiver um método específico para parcial, delegamos; senão fazemos merge e chamamos o método completo.
 */
router.patch('/', checkToken, async (req, res) => {
  try {
    const token = req.headers['authorization'].split(' ')[1];
    const updatesRaw = normalizeAnuncioPayload(req.body);

    // Se o service implementou atualização parcial, use-a
    if (typeof anuncioService.atualizar_parcial === 'function') {
      const svc = await anuncioService.atualizar_parcial(updatesRaw, token);
      if (svc.sucesso === false) return res.status(svc.status_code || 400).json({ message: svc.message });
      return res.status(svc.status_code || 200).json({ data: svc.data ?? svc });
    }

    // Caso contrário, recupere o documento atual e mescle
    let current = null;
    try {
      if (typeof anuncioService.getAnunciante === 'function') {
        const r = await anuncioService.getAnunciante(token);
        current = r?.data ?? r;
      }
    } catch (e) {
      console.warn('getAnunciante service falhou, fallback para model', e);
    }
    if (!current) {
      const payload = TokenService && TokenService.decodificarToken ? TokenService.decodificarToken(token) : jwt.verify(token, process.env.SECRET);
      current = await AnuncioModel.findById(payload.id).lean();
    }
    if (!current) return res.status(404).json({ message: 'Usuário não encontrado' });

    const merged = { ...current, ...updatesRaw };
    const results = await anuncioService.atualizar_anuncio(merged, token);
    if (results.sucesso) return res.status(results.status_code).json(results.data ?? { message: 'atualizado' });
    return res.status(results.status_code || 400).json({ error: results.message });
  } catch (err) {
    console.error('PATCH /anuncio error:', err);
    if (err.name === 'JsonWebTokenError' || err.name === 'TokenExpiredError') return res.status(401).json({ message: 'token inválido' });
    return res.status(500).json({ message: err.message });
  }
});

/**
 * PUT /anuncio
 * Mantém compatibilidade com a implementação atual (espera payload completo).
 * Faz merge com documento atual para preencher campos faltantes antes de chamar o service.
 */
router.put('/', checkToken, async (req, res) => {
  try {
    const token = req.headers['authorization'].split(' ')[1];
    const payloadNormalized = normalizeAnuncioPayload(req.body);

    // Busca dados atuais para preencher campos faltantes (service preferred)
    let current = null;
    try {
      if (typeof anuncioService.getAnunciante === 'function') {
        const r = await anuncioService.getAnunciante(token);
        current = r?.data ?? r;
      }
    } catch (e) {
      console.warn('getAnunciante falhou, fallback para model', e);
    }
    if (!current) {
      const decoded = TokenService && TokenService.decodificarToken ? TokenService.decodificarToken(token) : jwt.verify(token, process.env.SECRET);
      current = await AnuncioModel.findById(decoded.id).lean();
    }

    const fullPayload = { ...(current || {}), ...payloadNormalized };
    const results = await anuncioService.atualizar_anuncio(fullPayload, token);
    if (results.sucesso) return res.status(results.status_code).json(results.data);
    return res.status(results.status_code || 400).json({ error: results.message });
  } catch (err) {
    console.error('PUT /anuncio error:', err);
    if (err.name === 'JsonWebTokenError' || err.name === 'TokenExpiredError') return res.status(401).json({ message: 'token inválido' });
    return res.status(500).json({ message: err.message });
  }
});

/**
 * DELETE /anuncio
 * Deleta a conta do anunciante autenticado.
 */
router.delete('/', checkToken, async (req, res) => {
  try {
    const token = req.headers['authorization'].split(' ')[1];
    const results = await anuncioService.deletar(token);
    if (results.sucesso) return res.status(results.status_code).json(results.data);
    return res.status(results.status_code || 400).json({ error: results.message });
  } catch (err) {
    console.error('DELETE /anuncio error:', err);
    return res.status(500).json({ message: err.message });
  }
});

module.exports = router;
