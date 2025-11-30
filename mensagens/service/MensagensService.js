const MensagensSchema = require("../models/mongodbmodels/MensagensSchema"); // importa o model
const { decodificarToken } = require("./TokenService");
const { findById } = require("./UserService");


// Envia/Salva uma mensagem. Valida token e usuários antes de salvar.
const sendMessage = async (token, message) => {
    const dados = decodificarToken(token);

    if (!dados) return { sucesso: false, status_code: 401, message: "Token inválido" };

    const existUser = await findById(dados.sub);
    const existReciver = await findById(message.receiver);

    if (!existUser) return { sucesso: false, status_code: 400, message: "Nenhum usuario encontrado" };
    if (!existReciver) return { sucesso: false, status_code: 400, message: "Nenhum usuario receptor encontrado" };

    try {
        const messageData = {
            id_sender: dados.sub,
            id_receiver: message.receiver,
            id_solicitacao: message.id_solicitacao,
            data: message.data,
            data_atualizacao: null,
            message: message.message,
        };

        const messageSave = new MensagensSchema(messageData);
        const saved = await messageSave.save();
        return { sucesso: true, status_code: 201, data: saved };
    } catch (error) {
        return { sucesso: false, status_code: 500, message: "não foi possivel salvar mensagem " + error };
    }
};


// Retorna lista de conversas do usuário com última mensagem e contagem de não lidas
const getUserConversations = async (userId) => {
    try {
        const messages = await MensagensSchema.find({
            $or: [{ id_sender: userId }, { id_receiver: userId }],
        })
            .sort({ data: -1 })
            .lean();

        const convMap = new Map();

        for (const msg of messages) {
            const partnerId = msg.id_sender === userId ? msg.id_receiver : msg.id_sender;
            if (!convMap.has(partnerId)) {
                const unread = await MensagensSchema.countDocuments({
                    id_sender: partnerId,
                    id_receiver: userId,
                    lida: false,
                });

                convMap.set(partnerId, {
                    partnerId,
                    lastMessage: msg,
                    unreadCount: unread,
                });
            }
        }

        return Array.from(convMap.values());
    } catch (error) {
        return [];
    }
};


// Retorna mensagens entre dois usuários (paginação simples)
const getConversationMessages = async (userId, partnerId, page = 1, limit = 50) => {
    try {
        const skip = (page - 1) * limit;
        const messages = await MensagensSchema.find({
            $or: [
                { id_sender: userId, id_receiver: partnerId },
                { id_sender: partnerId, id_receiver: userId },
            ],
        })
            .sort({ data: 1 })
            .skip(skip)
            .limit(limit)
            .lean();

        return messages;
    } catch (error) {
        return [];
    }
};


// Marca uma mensagem como lida (apenas receptor pode marcar)
const markMessageRead = async (messageId, userId) => {
    try {
        const msg = await MensagensSchema.findById(messageId);
        if (!msg) return { success: false, status_code: 404, message: "Mensagem não encontrada" };
        if (msg.id_receiver !== userId) return { success: false, status_code: 403, message: "Apenas receptor pode marcar lida" };

        msg.lida = true;
        msg.data_atualizacao = new Date();
        const saved = await msg.save();
        return { success: true, status_code: 200, data: saved };
    } catch (error) {
        return { success: false, status_code: 500, message: String(error) };
    }
};


module.exports = {
    sendMessage,
    getUserConversations,
    getConversationMessages,
    markMessageRead,
};

