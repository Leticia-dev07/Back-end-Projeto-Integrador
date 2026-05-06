# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o pom e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código e gera o JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Execução
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o JAR gerado no build
COPY --from=build /app/target/*.jar app.jar

# Porta padrão
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]