package br.com.lucaszarzur;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

public class RdsPostgresLambda implements RequestHandler<Object, String> {

    private static final Logger logger = Logger.getLogger(RdsPostgresLambda.class.getName());

    // Faça a configuração das variaveis de ambiente diretamente no Lambda Function na AWS
    private static final String DB_HOST = System.getenv("DB_HOST");   // RDS Endpoint (e.g. mydbinstance.123456789012.us-west-1.rds.amazonaws.com)
    private static final String DB_NAME = System.getenv("DB_NAME");   // Database name
    private static final String DB_USER = System.getenv("DB_USER");   // Database user
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD"); // Database password
    private static final String DB_PORT = "5432";  // Default PostgreSQL port

    @Override
    public String handleRequest(Object input, Context context) {
        String result = "Connection failed";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // URL de conexão para a instância do PostgreSQL
            String url = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

            // Carregar driver JDBC do PostgreSQL
            Class.forName("org.postgresql.Driver");

            // Estabelecer a conexão com o banco de dados
            conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);

            // Log de conexão bem-sucedida
            logger.info("Conexão ao banco de dados estabelecida com sucesso!");

            // Criar um objeto Statement
            stmt = conn.createStatement();

            // Executar a consulta SQL
            String query = "SELECT id, nome FROM cliente LIMIT 10";  // Substitua pelo nome da sua tabela
            rs = stmt.executeQuery(query);

            // Processar o conjunto de resultados
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                int id = rs.getInt("id");   // Obtendo o id pela coluna nome
                String nome = rs.getString("nome"); // Obtendo o nome pela coluna nome
                sb.append("id: ").append(id).append(", nome: ").append(nome).append("\n");
                System.out.println("id: " + id + ", nome: " + nome);  // Imprimindo id e nome
            }

            // Definir o resultado da execução da função Lambda
            result = sb.toString();
        } catch (Exception e) {
            logger.severe("Error while connecting to the database: " + e.getMessage());
            result = "Error: " + e.getMessage();
        } finally {
            // Fechar os recursos no bloco finally para garantir que sejam fechados
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                logger.severe("Erro ao fechar os recursos: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return result;
    }
}