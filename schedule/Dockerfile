FROM openjdk:18-alpine3.14

RUN mkdir /app

COPY launcher/target/* /app

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "launcher-1.0.0.jar"]
