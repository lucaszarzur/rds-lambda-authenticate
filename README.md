# Java AWS Lambda + Layer: Teste de autenticação em BD PostgreSQL

Este repositório tem como objetivo fornecer um conjunto de códigos e pequenas instruções para auxiliar na criação de funções Lambda na Amazon AWS. Junto a função Lambda, que neste exemplo tem como objetivo realizar um teste de autenticação em BD PostgreSQL, este repositório fornece também instruções para a criação de um pequeno Layer que será útil para as dependências deste código.

Em uma função Lambda para o devido funcionamento do código (independente de qual a linguagem de programação escolhida) é necessário que o código suba juntamente as dependências (bibliotecas, etc.), seja diretamente na função Lambda ou através das Layers.

Neste projeto em especifico temos a necessidade da biblioteca "postgresql-42.5.0.jar".

Aqui optaremos pelas Layers, e é por isso que temos o arquivo "[java-layer.zip](/src/main/resources/java-layer.zip)" (que contém o arquivo "postgresql-42.5.0.jar") anexado neste repositório.

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


## Uso
**OBS**: Usaremos Layer.

**1** - Caso desejado, no arquivo [script.sql](/src/main/resources/script.sql) há a criação da tabela e inserção de alguns registros nessa tabela em especifica que o código deste repositório realiza os testes;

**2** - Gere o arquivo JAR:

```
./mvnw clean package
```

Se atente aos logs para verificar o local do arquivo JAR.

**3** - Crie a Layer utilizando o arquivo [java-layer.zip](/src/main/resources/java-layer.zip). Se tiver mais interesse em entender como funciona as Layers na AWS clique [aqui](https://docs.aws.amazon.com/lambda/latest/dg/packaging-layers.html);

**4** - Crie sua função Lambda com o arquivo JAR gerado, após a criação, edite a função Lambda adicionando a Layer recém criada;

**5** - Crie as variáveis de ambiente dentro da sua função Lambda para configuração do Banco de Dados desejado

