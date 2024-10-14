FROM java:8
LABEL authors="kwadwo"

VOLUME /tmp

ADD dockerapp-0.0.1-SNAPSHOT.jar app.jar

RUN bash -c 'touch /app.jar'

#ENTRYPOINT ["java", "-D java.security.egd=file:/dev/ ./urandom", "-jar", "/app.jar"]
ENTRYPOINT ["java", "-D", "java.security.egd=file:/dev/", "./urandom", "-jar", "/app.jar"]
