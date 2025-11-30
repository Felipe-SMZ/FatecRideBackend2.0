
const User = require("../models/mysqlmodels/User")


const findAllUser = async ()=>{


    try {
        const users =await User.findAll();
        return users
    } catch (error){
        console.log(error)
        return [];
    }
   
}



const findById = async (id)=>{

   
    try {
        
        const users =await User.findOne({where:  {id_usuario:id}});
        return users
    } catch (error){
        console.log(error)
        return [];
    }
   
}


// (async () =>{
//     const usuarios = await findAllUser();
//     console.log(usuarios.map(u => u.toJSON()));
// })();


module.exports = {
    findAllUser,
    findById
}