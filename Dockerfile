FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /server-app


ARG JARFILE=build/libs/*SNAPSHOT.jar

COPY ${JARFILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080