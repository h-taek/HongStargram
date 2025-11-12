FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY myserver.jar /app/myserver.jar
COPY lib/ /app/lib/
VOLUME ["/app/.user_data"]
VOLUME ["/app/.user_data/chat"]
VOLUME ["/app/.user_data/image"]
ENV TZ=Asia/Seoul
EXPOSE 8000 8001 8002
ENTRYPOINT ["java","-jar","/app/myserver.jar"]