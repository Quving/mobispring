FROM gradle:latest

WORKDIR /workdir
COPY . /workdir

EXPOSE 8080
CMD ["./gradlew", "bootRun"]
