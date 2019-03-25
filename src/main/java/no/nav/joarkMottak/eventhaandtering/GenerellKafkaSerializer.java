package no.nav.joarkMottak.eventhaandtering;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.joarkMottak.journalpost.InngaaendeJournalpost;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;


public class GenerellKafkaSerializer implements Serializer<InngaaendeJournalpost> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {}

    @Override
    public byte[] serialize(String s, InngaaendeJournalpost record) {
        try {
            return new ObjectMapper().writeValueAsBytes(record);
        } catch (Exception e) {
            throw new IllegalStateException("Failed while serializing message", e);
        }
    }

    @Override
    public void close() {}
}
