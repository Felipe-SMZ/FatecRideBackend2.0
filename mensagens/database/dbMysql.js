require("dotenv").config();

const Sequelize = require("sequelize");

const sequelize = new Sequelize(
  process.env.DATABASEMYSQL,
  process.env.USERMYSQL,
  process.env.PASSWORDMYSQL,
  {
    host: "localhost",
    dialect: "mysql",
  }
);

sequelize.authenticate()
  .then(() => {
    console.log("tudo certo");
  })
  .catch((error) => {
    console.log("erro de conexão" + error);
  });

module.exports = {
  sequelize,
  Sequelize,
};
