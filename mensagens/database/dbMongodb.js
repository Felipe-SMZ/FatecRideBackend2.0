const mongoose = require("mongoose");



mongoose.connect("mongodb://localhost/chat")
    .then(()=>{
        console.log("Ok, tudo certo com mongo")
    }).catch((error)=>{

        console.log("Problemas de conexão com o mongo", error)
    })




