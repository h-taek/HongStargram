FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy project structure
COPY Back /app/Back
COPY lib /app/lib

# Compile the server code
RUN javac -encoding UTF-8 -cp "lib/*" -d out Back/API/*.java Back/DB/*.java

# Create volume for images
VOLUME ["/app/Back/image"]

ENV TZ=Asia/Seoul
EXPOSE 8003 8004 8005

ENTRYPOINT ["java", "-cp", "out:lib/*", "Back.API.Server"]