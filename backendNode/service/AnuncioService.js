require('dotenv').config();
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const AnuncioModel = require('../models/AnuncioSchema');
const TokenService = require('./TokenService');

/**
 * Serviço que encapsula a lógica de negócio relacionada a anunciantes.
 * Retorna objetos padronizados: { sucesso: boolean, status_code, data?, message? }
 */
const SALT_ROUNDS = 10;

async function login({ email, senha }) {
  try {
    const anunciante = await AnuncioModel.findOne({ email }).lean();
    if (!anunciante) return { sucesso: false, status_code: 401, message: 'Credenciais inválidas' };
    const match = await bcrypt.compare(senha, anunciante.senha || '');
    if (!match) return { sucesso: false, status_code: 401, message: 'Credenciais inválidas' };

    const token = TokenService.gerarToken({ id: anunciante._id, email: anunciante.email });
    return { sucesso: true, status_code: 200, data: { token } };
  } catch (err) {
    console.error('AnuncioService.login error:', err);
    return { sucesso: false, status_code: 500, message: err.message };
  }
}

async function cadastrar(payload) {
  try {
    if (!payload.email || !payload.senha) return { sucesso: false, status_code: 400, message: 'email e senha são obrigatórios' };
    const exists = await AnuncioModel.findOne({ email: payload.email });
    if (exists) return { sucesso: false, status_code: 409, message: 'email já cadastrado' };
    const hashed = await bcrypt.hash(payload.senha, SALT_ROUNDS);
    const doc = new AnuncioModel({ ...payload, senha: hashed });
    const saved = await doc.save();
    const obj = saved.toObject();
    delete obj.senha;
    return { sucesso: true, status_code: 201, data: obj };
  } catch (err) {
    console.error('AnuncioService.cadastrar error:', err);
    return { sucesso: false, status_code: 500, message: err.message };
  }
}

async function divulgar() {
  try {
    // Retorna um anúncio aleatório disponível para divulgar
    const count = await AnuncioModel.countDocuments();
    if (!count) return { sucesso: false, status_code: 404, message: 'Nenhum anúncio encontrado' };
    const rand = Math.floor(Math.random() * count);
    const doc = await AnuncioModel.findOne().skip(rand).lean();
    if (!doc) return { sucesso: false, status_code: 404, message: 'Nenhum anúncio encontrado' };
    if (doc.senha) delete doc.senha;
    return { sucesso: true, status_code: 200, data: doc };
  } catch (err) {
    console.error('AnuncioService.divulgar error:', err);
    return { sucesso: false, status_code: 500, message: err.message };
  }
}

async function getAnunciante(token) {
  try {
    const payload = TokenService.decodificarToken(token);
    if (!payload || !payload.id) return { sucesso: false, status_code: 401, message: 'token inválido' };
    const doc = await AnuncioModel.findById(payload.id).lean();
    if (!doc) return { sucesso: false, status_code: 404, message: 'Anunciante não encontrado' };
    if (doc.senha) delete doc.senha;
    return { sucesso: true, status_code: 200, data: doc };
  } catch (err) {
    console.error('AnuncioService.getAnunciante error:', err);
    return { sucesso: false, status_code: 500, message: err.message };
  }
}

async function atualizar_anuncio(payload, token) {
  try {
    // payload deve conter _id ou usamos o token para encontrar o dono
    let id = payload._id;
    if (!id) {
      const p = TokenService.decodificarToken(token);
      id = p?.id;
    }
    if (!id) return { sucesso: false, status_code: 400, message: 'id do anunciante ausente' };

    // Se estiver atualizando senha, hash
    const toUpdate = { ...payload };
    if (toUpdate.senha) {
      toUpdate.senha = await bcrypt.hash(toUpdate.senha, SALT_ROUNDS);
    }

    const updated = await AnuncioModel.findByIdAndUpdate(id, toUpdate, { new: true, runValidators: true }).lean();
    if (!updated) return { sucesso: false, status_code: 404, message: 'Anunciante não encontrado' };
    if (updated.senha) delete updated.senha;
    return { sucesso: true, status_code: 200, data: updated };
  } catch (err) {
    console.error('AnuncioService.atualizar_anuncio error:', err);
    // Se for erro de validação do mongoose, manter mensagem clara
    if (err.name === 'ValidationError') return { sucesso: false, status_code: 400, message: err.message };
    return { sucesso: false, status_code: 500, message: err.message };
  }
}

async function atualizar_parcial(updates, token) {
  try {
    // Faz a mesma lógica do atualizar_anuncio porém considera somente campos enviados
    // Determina id a partir do token
    const payload = TokenService.decodificarToken(token);
    const id = payload?.id;
    if (!id) return { sucesso: false, status_code: 401, message: 'token inválido' };

    const toUpdate = { ...updates };
    if (toUpdate.senha) toUpdate.senha = await bcrypt.hash(toUpdate.senha, SALT_ROUNDS);

    const updated = await AnuncioModel.findByIdAndUpdate(id, { $set: toUpdate }, { new: true, runValidators: true }).lean();
    if (!updated) return { sucesso: false, status_code: 404, message: 'Anunciante não encontrado' };
    if (updated.senha) delete updated.senha;
    return { sucesso: true, status_code: 200, data: updated };
  } catch (err) {
    console.error('AnuncioService.atualizar_parcial error:', err);
    if (err.name === 'ValidationError') return { sucesso: false, status_code: 400, message: err.message };
    return { sucesso: false, status_code: 500, message: err.message };
  }
}

async function deletar(token) {
  try {
    const payload = TokenService.decodificarToken(token);
    const id = payload?.id;
    if (!id) return { sucesso: false, status_code: 401, message: 'token inválido' };
    const removed = await AnuncioModel.findByIdAndDelete(id).lean();
    if (!removed) return { sucesso: false, status_code: 404, message: 'Anunciante não encontrado' };
    return { sucesso: true, status_code: 200, data: { message: 'removido' } };
  } catch (err) {
    console.error('AnuncioService.deletar error:', err);
    return { sucesso: false, status_code: 500, message: err.message };
  }
}

module.exports = {
  login,
  cadastrar,
  divulgar,
  getAnunciante,
  atualizar_anuncio,
  atualizar_parcial,
  deletar,
};
