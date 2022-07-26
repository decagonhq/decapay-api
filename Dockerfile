FROM eclipse-temurin:17-alpine
ADD target/decapay.jar decapay.jar
EXPOSE 5001
ENTRYPOINT ["java", "-jar", "decapay.jar"]
