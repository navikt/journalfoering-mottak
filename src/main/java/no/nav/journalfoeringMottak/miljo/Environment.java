package no.nav.journalfoeringMottak.miljo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {
    private final static Logger LOG = LoggerFactory.getLogger(Environment.class);
    private static final String USERNAME = "INFOTRYGD_JOARK_MOTTAK_USERNAME";
    private static final String PASSWORD = "INFOTRYGD_JOARK_MOTTAK_PASSWORD";
    private static final String KAFKA_BOOTSTRAP_SERVERS = "KAFKA_BOOTSTRAP_SERVERS";
    private static final String KAFKA_SCHEMA_REGISTRY_URL = "KAFKA_SCHEMA_REGISTRY_URL";
    private static final String FASIT_ENVIRONMENT_NAME = "FASIT_ENVIRONMENT_NAME";
    private static final String NAV_TRUSTSTORE_PATH = "NAV_TRUSTSTORE_PATH";
    private static final String NAV_TRUSTSTORE_PASSWORD = "NAV_TRUSTSTORE_PASSWORD";

    public Environment() {
        confirmSystemVariables();
    }

    private void confirmSystemVariables() {
        try {
            getEnvVar(USERNAME);
            getEnvVar(PASSWORD);
            getEnvVar(KAFKA_BOOTSTRAP_SERVERS);
            getEnvVar(KAFKA_SCHEMA_REGISTRY_URL);
            getEnvVar(FASIT_ENVIRONMENT_NAME);
            getEnvVar(NAV_TRUSTSTORE_PATH);
            getEnvVar(NAV_TRUSTSTORE_PASSWORD);
        } catch (final IllegalArgumentException e) {
            LOG.error("Error occurred while fetching environment variables: " + e.getMessage());
            System.exit(0);
        }
    }

    private static String getEnvVar(final String varName) throws IllegalArgumentException {
        final String envVar = System.getenv(varName);
        if (envVar == null) {
            throw new IllegalArgumentException("Missing environment variable for " + varName + " and default value is null");
        }
        return envVar;
    }

    public String kafkaUsernameAndPassword() {
        return Credentials.kafkaSaslAuthentication(getEnvVar(USERNAME), getEnvVar(PASSWORD));
    }

    public String getBootstrapServersUrl() {
        return getEnvVar(KAFKA_BOOTSTRAP_SERVERS);
    }

    public String getSchemaRegistryUrl() {
        return getEnvVar(KAFKA_SCHEMA_REGISTRY_URL);
    }


    public String getTrustStoreLocation() {
        return getEnvVar(NAV_TRUSTSTORE_PATH);
    }

    public String getTrustStorePassword() {
        return getEnvVar(NAV_TRUSTSTORE_PASSWORD);
    }

    public static String subscriptionTopic(final String topic) {
        if (getEnvVar(FASIT_ENVIRONMENT_NAME).isEmpty()) {
            return topic;
        }
        return topic + "-" + getEnvVar(FASIT_ENVIRONMENT_NAME);
    }

    //Understands valid formats of username and password strings and objects
    private static class Credentials {

        static String kafkaSaslAuthentication(final String username, final String password) {
            return String.format("username=%s password=%s", username, password);
        }
    }
}
