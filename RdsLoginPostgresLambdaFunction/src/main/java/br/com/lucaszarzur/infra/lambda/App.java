package br.com.lucaszarzur.infra.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.logging.Logger;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    // Faça a configuração das variaveis de ambiente diretamente no Lambda Function na AWS
    private static final String DB_HOST = System.getenv("DB_HOST");   // RDS Endpoint (e.g. mydbinstance.123456789012.us-west-1.rds.amazonaws.com)
    private static final String DB_NAME = System.getenv("DB_NAME");   // Database name
    private static final String DB_USER = System.getenv("DB_USER");   // Database user
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD"); // Database password
    private static final String DB_PORT = "5432";  // Default PostgreSQL port

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        System.out.println("[LOGIN] Request: " + input.getBody());

        Connection conn = null;
        int status = 500;
        String message = "";

        try {
            // Verifica se o clienteId foi passado
            JsonNode user = convertToJson(input.getBody());

            String url = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);

            logger.info("Conexão ao banco de dados estabelecida com sucesso!");

            String query = "SELECT DISTINCT id, nome FROM cliente WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, user.findValue("clienteId").asInt());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {

                    StringBuilder sb = new StringBuilder();
                    int id = resultSet.getInt("id");   // Obtendo o id pela coluna nome
                    String nome = resultSet.getString("nome"); // Obtendo o nome pela coluna nome
                    sb.append("id: ").append(id).append(", nome: ").append(nome).append("\n");

                    System.out.println("id: " + id + ", nome: " + nome);  // Imprimindo id e nome

                    status = 200;
                    message = String.format("{ \"message\": \"Cliente encontrado\"}");
                } else {
                    status = 404;
                    message = String.format("{ \"message\": \"Cliente não encontrado\"}");
                }
            }
        } catch (Exception e) {
            status = 500;
            message = input.getBody() == null ?
                    String.format("{ \"message\": \"clienteId não fornecido\"}") :
                    String.format("{ \"message\": \"Error while connecting to the database\", \"details: \": \"%s\" }", e.getMessage());
        } finally {
            // Fechar os recursos no bloco finally para garantir que sejam fechados
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                logger.severe("Erro ao fechar os recursos: " + e.getMessage());
                status = 500;
                message = String.format("{ \"message\": \"Erro interno\"}");
            }
        }

        return createResponse(status, message);
    }

    private JsonNode convertToJson(String objectToConvert) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(objectToConvert);
    }

    private APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        return response
                .withStatusCode(statusCode)
                .withBody(body);
    }
}