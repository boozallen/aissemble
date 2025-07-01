FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# AuthzForce RESTful PDP JAR
RUN curl -L -o app.jar https://repo1.maven.org/maven2/org/ow2/authzforce/authzforce-ce-restful-pdp-cxf-spring-boot-server/7.1.0/authzforce-ce-restful-pdp-cxf-spring-boot-server-7.1.0.jar

COPY --chmod=755 ./src/main/resources/server ./

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]