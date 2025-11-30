const mongoose = require("mongoose")



// Esquema de mensagens simples. IDs são armazenados como Number para
// corresponder ao `BIGINT` do MySQL usado nos usuários.
const MensagensSchema = mongoose.Schema({
    id_sender: {
        type: Number,
        required: true,
    },
    id_receiver: {
        type: Number,
        required: true,
    },
    id_solicitacao: {
        type: Number,
        required: true,
    },
    data: {
        type: Date,
        required: true,
    },
    data_atualizacao: {
        type: Date,
        default: null,
    },
    message: {
        type: String,
        required: true,
    },
    // Indica se a mensagem já foi lida pelo destinatário
    lida: {
        type: Boolean,
        default: false,
    },
});

module.exports = mongoose.model("mensagens", MensagensSchema);