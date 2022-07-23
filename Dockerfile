FROM eclipse-temurin:17-alpine
ADD target/decapay.jar decapay.jar
ENTRYPOINT ["java", "-jar", "decapay.jar"]
