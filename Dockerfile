FROM java:8

ENV SERVER_PORT 9100

WORKDIR /workdir
COPY . /workdir
RUN ./gradlew clean build

CMD ["./gradlew", "bootRun"]
