FROM java:8

WORKDIR /workdir
COPY . /workdir
RUN ./gradlew clean build 

EXPOSE 8080
CMD ["./gradlew", "bootRun"]
