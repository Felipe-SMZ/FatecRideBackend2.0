// ./models/AnuncioSchema.js (corrigido)
const mongoose = require('mongoose');

const AnuncioSchema = new mongoose.Schema({
    nome_dono: { type: String, required: true },
    logo: { type: String, required: true },
    razao_social: { type: String, required: true },
    nome_fantasia: { type: String, required: true },
    cnpj: { type: String, required: true },
    ramo_empresa: { type: String, required: true },
    descricao_anuncio: { type: String, required: true },
    endereco_empresa: { type: String, required: true },
    endereco_dono: { type: String, required: true },
    contato: { type: String, required: true },
    email: { type: String, required: true },
    senha: { type: String, required: true },
    anuncio: { type: String, required: true },
    quantidade_alcance: { type: Number, required: true },
    quantidade_alcancados: { type: Number, required: true, default: 0 },
}, {
    timestamps: true,
});

const Anuncio = mongoose.model('anuncio', AnuncioSchema);

module.exports = Anuncio;