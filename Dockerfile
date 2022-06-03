FROM adoptopenjdk/openjdk15
VOLUME /main-app
ADD target/sweater-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]