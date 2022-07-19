FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} decapay.jar
ENTRYPOINT ["java", "-jar", "decapay.jar"]