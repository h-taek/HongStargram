FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY myserver.jar /app/myserver.jar
COPY lib/ /app/lib/
VOLUME ["/app/image"]
ENV TZ=Asia/Seoul
EXPOSE 8003 8004 8005
ENTRYPOINT ["java","-jar","/app/myserver.jar"]