FROM adoptopenjdk:11-jre-hotspot
ADD target/decapay.jar decapay.jar
ENTRYPOINT ["java", "-jar", "decapay.jar"]
