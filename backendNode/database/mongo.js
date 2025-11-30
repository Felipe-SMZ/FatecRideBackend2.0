const mongoose = require("mongoose");


 const  connectMongo= ()=> {
    mongoose.connect('mongodb://localhost/backendfatecnode')
    .then(()=>{
        console.log("ok conectado")       
    }).catch((err)=>{
        console.warn("não foi possivel a conexao com mongo (continuando sem DB): " + err);
        // Não encerra o processo aqui para permitir que o servidor suba em ambientes sem Mongo.
        // As operações que dependem do banco poderão falhar, mas isso facilita testes locais.
    });
    // Logar eventos de erro posteriores na conexão
    mongoose.connection.on('error', (e) => {
        console.warn('Mongoose connection error:', e);
    });
}


module.exports = connectMongo



