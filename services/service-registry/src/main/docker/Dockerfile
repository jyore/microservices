FROM jyore/java:openjdk-8-jre

ADD service-registry-1.0.0-SNAPSHOT.jar /deployments/app.jar

VOLUME /data

WORKDIR /deployments

EXPOSE 8761

ENTRYPOINT ["java","-jar","/deployments/app.jar"]