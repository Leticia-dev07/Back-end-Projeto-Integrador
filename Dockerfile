# Etapa 1: Build (Compilação)
FROM maven:3.9.9-amazoncorretto-25-debian AS build
WORKDIR /app

# Cache das dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Compilação
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Execução (Ajustada para imagem que existe)
FROM amazoncorretto:25-al2023-headless
WORKDIR /app

# Copia o jar gerado
COPY --from=build /app/target/*.jar app.jar

# Porta do Render
EXPOSE 8080

# Comando para rodar
ENTRYPOINT ["java", "-jar", "app.jar"]