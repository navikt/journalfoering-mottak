package no.nav.joarkMottak.eventhaandtering;

import no.nav.joarkMottak.journalpost.InngaaendeJournalpost;
import no.nav.joarkMottak.miljo.Environment;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

class Kafkaprodusent {
    private static final Logger LOG = LoggerFactory.getLogger(Kafkaprodusent.class);
    private static final String PRODUSENT_ID = "KrutJoarkProdusent";
    private static final String TOPIC_FEIL_DATAGRUNNLAG = "privat-krut-feilDatagrunnlag-alpha";
    private static final String TOPIC_GENERELL = "privat-krut-generellHendelse-alpha";
    private static final String TOPIC_INFOTRYGD = "privat-krut-infotrygdhendelse-alpha";
    private static final String TOPIC_ARENA = "privat-krut-arenahendelse-alpha";
    private final KafkaProducer<String, InngaaendeJournalpost> producer;

    Kafkaprodusent() {
        final Environment env = new Environment();
        final Properties properties = new Properties();
        //https://cwiki.apache.org/confluence/display/KAFKA/KIP-185%3A+Make+exactly+once+in+order+delivery+per+partition+the+default+producer+setting
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getBootstrapServersUrl());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, PRODUSENT_ID);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GenerellKafkaSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "2");
        properties.put(CommonClientConfigs.RETRIES_CONFIG, Integer.MAX_VALUE);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "100");
        properties.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required " + env.kafkaUsernameAndPassword() + ";"
        );
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        if (env.getTrustStoreLocation().isEmpty()) {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        } else {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, new File(env.getTrustStoreLocation()).getAbsolutePath());
            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, env.getTrustStorePassword());
        }
        producer = new KafkaProducer<>(properties);
    }

    void produserGenerellHendelseKafka(final InngaaendeJournalpost inngaaendeJournalpost) {
        sendKafkamelding(inngaaendeJournalpost, TOPIC_GENERELL);
    }

    void produserInfotrygdhendelseKafka(final InngaaendeJournalpost inngaaendeJournalpost) {
        sendKafkamelding(inngaaendeJournalpost, TOPIC_INFOTRYGD);
    }

    void produserArenahendelseKafka(final InngaaendeJournalpost inngaaendeJournalpost) {
        sendKafkamelding(inngaaendeJournalpost, TOPIC_ARENA);
    }

    private void sendKafkamelding(final InngaaendeJournalpost inngaaendeJournalpost, final String topic) {
        try {
            RecordMetadata recordMetadata = producer.send(
                    new ProducerRecord<>(topic, inngaaendeJournalpost)).get();
            System.out.println("Lagde -> " + topic + " med offset: " + recordMetadata.offset());
        } catch(final InterruptedException | ExecutionException e) {
            System.out.println("Feil under produksjon av melding med topic: " + topic);
        }
    }
}
