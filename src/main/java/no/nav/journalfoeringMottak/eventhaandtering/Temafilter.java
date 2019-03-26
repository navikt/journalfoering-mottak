package no.nav.journalfoeringMottak.eventhaandtering;

import no.nav.journalfoeringMottak.journalpost.InngaaendeJournalpost;

import java.util.Arrays;
import java.util.List;

class Temafilter {
    private static final List<String> GENERELLE_TEMAER = Arrays.asList("AGR", "BIL");
    private static final List<String> MANUELL_JOURNALFOERING_TEMAER = Arrays.asList("", "");
    private static final List<String> INFOTRYGDTEMAER = Arrays.asList("KON", "");
    private static final List<String> ARENATEMAER = Arrays.asList("", "");

    private final Kafkaprodusent kafkaprodusent = new Kafkaprodusent();

    void sorterForTema(final InngaaendeJournalpost inngaaendeJournalpost) {
        if (inngaaendeJournalpost.skalBehandles(GENERELLE_TEMAER)) {
            kafkaprodusent.produserGenerellHendelseKafka(inngaaendeJournalpost);
        }
        if (inngaaendeJournalpost.skalBehandles(INFOTRYGDTEMAER)) {
            kafkaprodusent.produserInfotrygdhendelseKafka(inngaaendeJournalpost);
        }
        if (inngaaendeJournalpost.skalBehandles(ARENATEMAER)) {
            kafkaprodusent.produserArenahendelseKafka(inngaaendeJournalpost);
        }
    }
}
