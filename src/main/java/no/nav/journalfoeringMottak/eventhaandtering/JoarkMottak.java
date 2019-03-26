package no.nav.journalfoeringMottak.eventhaandtering;

import com.google.gson.Gson;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroDeserializer;
import no.nav.journalfoeringMottak.journalpost.InngaaendeJournalpost;
import no.nav.journalfoeringMottak.miljo.Environment;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.io.File;

public class JoarkMottak {

    private final static Logger LOG = LoggerFactory.getLogger(JoarkMottak.class);
    private final static String GROUP_ID = "journalfoeringMottak";
    private final static String TOPIC = "aapen-dok-journalfoering-v1";
    private final KafkaConsumer<String, GenericRecord> consumer;
    private final Temafilter temafilter = new Temafilter();
    private final Gson gson = new Gson();

    public JoarkMottak() {
        final Environment env = new Environment();
        consumer = new KafkaConsumer<>(setConsumerProperties(env));
    }

    private Properties setConsumerProperties(final Environment env) {
        final Properties props = new Properties();
        props.put(CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG, 1000);
        props.put(CommonClientConfigs.RECONNECT_BACKOFF_MS_CONFIG, 5000);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getBootstrapServersUrl());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GenericAvroDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, env.getSchemaRegistryUrl());
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required " + env.kafkaUsernameAndPassword() + ";"
        );
        if (env.getTrustStoreLocation().isEmpty()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        } else {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, new File(env.getTrustStoreLocation()).getAbsolutePath());
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, env.getTrustStorePassword());
        }
        return props;
    }

    public void run() {
        try {
            consumer.subscribe(Collections.singletonList(Environment.subscriptionTopic(TOPIC)));
            while (true) {
                lesKafkaHendelser(consumer.poll(Duration.ofSeconds(5)));
                consumer.commitSync();
            }
        } finally {
            LOG.info("Stopper InfotrygdKafkaConsumer");
            consumer.close();
        }
    }

    private void lesKafkaHendelser(final ConsumerRecords<String, GenericRecord> hendeleser) {
        for (final ConsumerRecord<String, GenericRecord> record : hendeleser) {
            final InngaaendeJournalpost inngaaendeJournalpost = gson.fromJson(record.value().toString(), InngaaendeJournalpost.class);
            temafilter.sorterForTema(inngaaendeJournalpost);
        }
    }
}

