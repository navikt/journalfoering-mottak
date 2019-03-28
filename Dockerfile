FROM maven:3.5.4-jdk-11-slim as builder

ADD . .

RUN mvn clean package shade:shade --settings maven-settings.xml

FROM navikt/java:11
COPY --from=builder /target/original-journalfoeringMottak.jar app.jar