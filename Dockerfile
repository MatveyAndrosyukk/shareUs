FROM wouterpolet/maven-jdk-15

COPY ./ ./

RUN mvn clean package

CMD ["java", "-jar", "target/shareUs-0.0.1-SNAPSHOT.jar"]



