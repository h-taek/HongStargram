FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY myserver.jar /app/myserver.jar
COPY lib/ /app/lib/
VOLUME ["/app/.user_data"]
ENV TZ=Asia/Seoul
EXPOSE 8000 8001
ENTRYPOINT ["java","-jar","/app/myserver.jar"]