FROM openjdk:8-jre-alpine

COPY ./target/marketplace_1.2.0.jar ./marketplace_1.2.0.jar
ENTRYPOINT ["java", "-jar", "/marketplace_1.2.0.jar"]

EXPOSE 8220

CMD java $JAVA_HTTP_PROXY $JAVA_HTTPS_PROXY $JAVA_NON_PROXY_HOSTS -DSPRING_BOOT_WAIT_FOR_SERVICES=symbiote-aam:8080 -jar $(ls *run.jar)