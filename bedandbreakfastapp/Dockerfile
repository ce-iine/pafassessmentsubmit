FROM openjdk:21-bookworm AS builder

WORKDIR /src

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

RUN ./mvnw package -Dmvn.test.skip=true

FROM openjdk:21-bookworm 

WORKDIR /app

COPY --from=builder /src/target/assessment-0.0.1-SNAPSHOT.jar app.jar

# run the application 
# define railway environment variable
# MONGO
ENV SPRING_DATA_MONODB_DATABASE=bedandbreakfast
ENV SPRING_DATA_MONGODB_URI=
# SQL
ENV SPRING_DATASOURCE_URL=

#expose this port - reference environment variable
EXPOSE ${PORT} 

#run the program 
ENTRYPOINT SERVER_PORT=${PORT} java -jar ./app.jar