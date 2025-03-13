# Java AWS Lambda + Layer: Teste de autenticação em BD PostgreSQL

Este repositório tem como objetivo fornecer um conjunto de códigos e pequenas instruções para auxiliar na criação de funções Lambda na Amazon AWS. Junto a função Lambda, que neste exemplo tem como objetivo realizar um teste de autenticação em BD PostgreSQL, este repositório fornece também instruções para a criação de um pequeno Layer que será útil para as dependências deste código. E por último, mas não menos importante, este repositório cobre a criação de um AWS SAM template, que é útil para a [Infrastructure as code](https://docs.aws.amazon.com/whitepapers/latest/introduction-devops-aws/infrastructure-as-code.html).

Em uma função Lambda para o devido funcionamento do código (independente de qual a linguagem de programação escolhida) é necessário que o código suba juntamente as dependências (bibliotecas, etc.), seja diretamente na função Lambda ou através das Layers.

Neste projeto em especifico temos a necessidade da biblioteca "postgresql-42.5.0.jar".

## Atenção
Este projeto contém duas branchs:
- **master**: Refere-se a um exemplo simples de AWS Lambda + Layer, com o código na linguagem de programação Java;
- **feature/login-api**: Refere-se a um exemplo mais completo, com a ideia ainda de um AWS Lambda, porém sem o uso de Layer. Aqui fora utilizado o [AWS SAM](https://aws.amazon.com/pt/serverless/sam/) e melhorado a lógica de autenticação do usuário.

Resumo de tecnologias e funcionalidades neste repositório:

**Branch: master**
- AWS Lambda
- Uso de varáveis de ambiente do AWS Lambda
- Layer
- Conexão com o Banco de Dados PostgreSQL na Amazon
- Código Java para teste de autenticação simples em BD PostgreSQL (também na AWS)
- Necessária a criação manual da Lambda no AWS Console

<br></br>

**Branch: feature/login-api**
- AWS Lambda
- Uso de varáveis de ambiente do AWS Lambda
- AWS SAM
- AWS Toolkit para IntelliJ
- Conexão com o Banco de Dados PostgreSQL na Amazon
- Código Java para teste de autenticação mais completo em BD PostgreSQL (também na AWS)
- Criação da Lambda via terminal utilizando o AWS SAM


# Uso
## Branch: Master
**OBS**: Usaremos Layer.

**1** - Gere o arquivo JAR:
```
./mvnw clean package
```

Se atente aos logs para verificar o local do arquivo JAR.

**2** - Crie a Layer utilizando o arquivo [java-layer.zip](/src/main/resources/java-layer.zip). Se tiver mais interesse em entender como funciona as Layers na AWS clique [aqui](https://docs.aws.amazon.com/lambda/latest/dg/packaging-layers.html);

**3** - Crie sua função Lambda com o arquivo JAR gerado, após a criação, edite a função Lambda adicionando a Layer recém criada;

**4** - Crie as variáveis de ambiente dentro da sua função Lambda para configuração do Banco de Dados desejado


Caso você queira seguir com outra abordagem, que é o caso de NÃO utilizar Layer e sim subir apenas a função Lambda, descomente o trecho de código abaixo no arquivo "pom.xml":
```
<build>
    <plugins>
      <plugin>
        <!-- Build the libs with the jar -->
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.2.1</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>shade</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```
O trecho de código acima empacota as libs junto ao código no arquivo JAR. 

## Branch: feature/login-api
**1** - Faça o build da aplicação (do arquivo template.yaml)
```
sam build
```

**2** - Faça o deploy da aplicação (do arquivo template.yaml). Este passo irá criar a Lambda (e demais itens que estiverem no arquivo de template) automaticamente
```
sam deploy --guided
```

**3** - As variáveis de ambiente já foram criadas dentro da sua função Lambda para configuração do Banco de Dados desejado, pois estavam no template do AWS SAM, porém, altere manualmente a variavel ```DB_PASSWORD``` que foi propositalmente versionada com a senha errada para evitar o vazamento de informações sensíveis. A dica é que essa alteração pode ser feita via AWS CLI ou via CI/CD do GitHub.

**4** - Limpezade todos os recursos subidos pelo AWS SAM:
```
sam delete
```
