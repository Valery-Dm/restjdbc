#FROM frolvlad/alpine-oraclejdk8:slim
#VOLUME /tmp
#ADD restjdbc-1.1.0.jar app.jar
#RUN sh -c 'touch /app.jar'
#ENV JAVA_OPTS=""
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]

FROM java:8
VOLUME /tmp
ADD restjdbc-1.1.0.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]