const {sequelize , Sequelize} = require("../../database/dbMysql")
const User = require("./User")


const  Gender = sequelize.define("genero",{
	id_genero:{
      type: Sequelize.BIGINT,
      primaryKey:true,
      autoIncrement:true
   },
	 genero:{
        type:Sequelize.STRING
     }
},{
  tableName: "genero", // mesmo nome que o banco já tem
  timestamps: false      // se não quiser criar createdAt/updatedAt
})



module.exports =  Gender