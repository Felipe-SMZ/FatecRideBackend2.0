const express = require("express");
const bodyParser = require("body-parser");
const connectMongo = require("./database/mongo");

const cors = require("cors")
const  swaggerUI = require("swagger-ui-express");
const swaggerDocs = require("./swagger.json")
const app =  express()

app.use(cors({
    origin:"*",
    methods:["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"],
    allowedHeaders:["Content-Type", "Authorization", "Origin", "Accept"]
}))


app.use(bodyParser.urlencoded({extended:true}))
app.use(bodyParser.json())

app.use("/docs",swaggerUI.serve, swaggerUI.setup(swaggerDocs))
connectMongo()

const anuncioRouter = require("./controller/AnuncioController");




// mongoose.connect('mongodb://localhost/backendfatecnode')
//     .then(()=>{
//         console.log("ok conectado")       
//     }).catch((err)=>{
//         console.log("não foi possivel a conexao com mongo" + err);
//         process.exit(1);
//     });


app.use("/anuncio", anuncioRouter)


const port = 8081
app.listen(port, ()=>{
    console.log("servidor rodando na porta " + port)
})

