const mongoose = require("mongoose");
require('dotenv').config();

const mongoUri = process.env.MONGODB_URI || "mongodb://localhost/chat";

mongoose.connect(mongoUri)
    .then(()=>{
        console.log("✅ Conexão MongoDB estabelecida com sucesso")
    }).catch((error)=>{
        console.error("❌ Erro de conexão com MongoDB:", error)
    })




