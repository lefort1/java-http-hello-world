FROM openjdk:8

ADD target/hello-world.jar .

EXPOSE 8000

CMD ["java", "-jar", "hello-world.jar"]
