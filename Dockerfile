FROM openjdk:17-jdk

ARG JAR_FILE=build/libs/Reinput-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} Reinput.jar

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "Reinput.jar"]
