#
# Build stage
#
FROM maven:4.0.0-jdk-17.0.5 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:jdk-17.0.5
COPY --from=build /home/app/target/PeopleManagement-DevJr-ApiRest-0.0.1-SNAPSHOT.jar /usr/local/lib/demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]