const {sequelize , Sequelize} = require("../../database/dbMysql")
const User = require("./User")


const  UserType = sequelize.define("tipo_usuario",{
	id_tipo_usuario:{
      type: Sequelize.BIGINT,
      primaryKey:true,
      autoIncrement:true
   },
	 tipo:{
        type:Sequelize.STRING
     }
},{
  tableName: "tipo_usuario", // mesmo nome que o banco já tem
  timestamps: false      // se não quiser criar createdAt/updatedAt
})



module.exports =  UserType