# Etapa 1: Build (Compilação)
# Usamos o Maven 3.9.9 que é o mais estável para lidar com Java 25
FROM maven:3.9.9-amazoncorretto-25-debian AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para baixar as dependências e ganhar tempo no próximo deploy
COPY pom.xml .
RUN mvn dependency:go-offline

# Agora copia o código fonte e faz o build
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime (O que vai rodar de fato no Render)
FROM openjdk:25-slim-bookworm
WORKDIR /app

# Copia o arquivo .jar que o Maven gerou (o nome geralmente é pi-0.0.1-SNAPSHOT.jar)
COPY --from=build /app/target/*.jar app.jar

# Porta que o Spring Boot usa
EXPOSE 8080

# Comando para iniciar a aplicação no Render
ENTRYPOINT ["java", "-jar", "app.jar"]