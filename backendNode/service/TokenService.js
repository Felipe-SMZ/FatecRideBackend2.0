const jwt = require("jsonwebtoken");
require('dotenv').config()

const  gerarToken = (payload)=>{  
    console.log("JWT_SECRET:", process.env.SECRET);

    const token = jwt.sign(payload,process.env.SECRET,{expiresIn:"1h"})
    return token
}


const decodificarToken = (token) =>{

    const dados = jwt.decode(token);
    return dados;
}





module.exports = {
    gerarToken,
    decodificarToken
}