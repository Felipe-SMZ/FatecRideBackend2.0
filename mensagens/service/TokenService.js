const jwt = require("jsonwebtoken");
require('dotenv').config()

const  gerarToken = (payload)=>{  
    console.log("JWT_SECRET:", process.env.SECRET);

    const token = jwt.sign(payload,process.env.SECRET,{expiresIn:"1h"})
    return token
}


// Verifica a assinatura do token e retorna o payload decodificado.
// Retorna null se o token for inválido/expirado.
const decodificarToken = (token) => {
    try {
        const dados = jwt.verify(token, process.env.SECRET);
        return dados;
    } catch (error) {
        return null;
    }
}


module.exports = {
    gerarToken,
    decodificarToken
}