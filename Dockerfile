#FROM gcr.io/google-appengine/openjdk:11
#FROM aws/codebuild/java:openjdk-9
FROM openjdk:8-jre-alpine
EXPOSE 8080
COPY ./irunninglog-main/target/irunninglog.jar irunninglog.jar
ENTRYPOINT ["java","-jar","irunninglog.jar"]
