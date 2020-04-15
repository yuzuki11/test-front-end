FROM openjdk:8-alpine
MAINTAINER cyprinus cyprinus@cyprinus.net.cn
WORKDIR /ROOT
CMD ["java", "-version"]
ADD matrix-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
