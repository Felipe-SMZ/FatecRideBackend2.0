const { sequelize, Sequelize } = require("../../database/dbMysql");
const UserType = require("./UserType");
const Course = require("./Course");
const Gender = require("./Gender");
const User = sequelize.define(
  "usuarios",
  {
    id_usuario: {
      type: Sequelize.BIGINT,
      primaryKey: true,
      autoIncrement: true,
    },
    nome: {
      type: Sequelize.STRING,
    },
    sobrenome: {
      type: Sequelize.STRING,
    },
    email: {
      type: Sequelize.STRING,
    },
    senha: {
      type: Sequelize.STRING,
    },
    telefone: {
      type: Sequelize.STRING,
    },
    foto: {
      type: Sequelize.STRING,
    },
  },
  {
    tableName: "usuarios", // mesmo nome que o banco já tem
    timestamps: false, // se não quiser criar createdAt/updatedAt
  }
);

Course.hasMany(User, { foreignKey: "id_curso" });
User.belongsTo(Course, { foreignKey: "id_curso" });

UserType.hasMany(User, { foreignKey: "id_tipo_usuario" });
User.belongsTo(UserType, { foreignKey: "id_tipo_usuario" });

Gender.hasMany(User, { foreignKey: "id_genero" });
User.belongsTo(Gender, { foreignKey: "id_genero" });

module.exports = User;
