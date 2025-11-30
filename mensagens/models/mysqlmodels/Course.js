const {sequelize , Sequelize} = require("../../database/dbMysql")
const User = require("./User")


const  Course = sequelize.define("curso",{
	id_curso:{
      type: Sequelize.BIGINT,
      primaryKey:true,
      autoIncrement:true
   },
	 nome:{
        type:Sequelize.STRING
     }
},{
  tableName: "curso", // mesmo nome que o banco já tem
  timestamps: false      // se não quiser criar createdAt/updatedAt
})



module.exports =  Course