# Etapa 1: Build da aplicação
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copia o pom.xml e baixa dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte e faz o build
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime (imagem final leve)
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o .jar compilado
COPY --from=builder /app/target/*.jar app.jar

# Exponha a porta
EXPOSE 3306

# Define o comando de execução
ENTRYPOINT ["java", "-jar", "app.jar"]
