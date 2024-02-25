FROM eclipse-temurin:17

WORKDIR /app

COPY . /app

RUN rm -rf build && rm -rf .gradle && ./gradlew clean;

RUN chmod +x start.sh && ./gradlew

EXPOSE 8080

CMD ["sh", "start.sh"]