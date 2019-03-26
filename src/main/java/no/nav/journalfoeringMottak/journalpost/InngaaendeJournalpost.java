package no.nav.journalfoeringMottak.journalpost;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InngaaendeJournalpost {
    private String hendelsesId;
    private int versjon;
    private String hendelsesType;
    private long journalpostId;
    private String journalpostStatus;
    private String temaGammelt;
    private String temaNytt;
    private String mottaksKanal;
    private String kanalReferanseId;
    private String behandlingstema;

    private InngaaendeJournalpost(final Builder builder) {
        this.hendelsesId = builder.hendelsesId;
        this.versjon = builder.versjon;
        this.hendelsesType = builder.hendelsesType;
        this.journalpostId = builder.journalpostId;
        this.journalpostStatus = builder.journalpostStatus;
        this.temaGammelt = builder.temaGammelt;
        this.temaNytt = builder.temaNytt;
        this.mottaksKanal = builder.mottaksKanal;
        this.kanalReferanseId = builder.kanalReferanseId;
        this.behandlingstema = builder.behandlingstema;
    }

    public String getHendelsesId() {
        return hendelsesId;
    }

    public int getVersjon() {
        return versjon;
    }

    private String getHendelsesType() {
        return hendelsesType;
    }

    public String getJournalpostId() { return String.valueOf(journalpostId); }

    public String getJournalpostStatus() {
        return journalpostStatus;
    }

    private String getTemaGammelt() {
        return temaGammelt;
    }

    public String getTemaNytt() {
        return temaNytt;
    }

    private String getMottaksKanal() {
        return mottaksKanal;
    }

    public String getKanalReferanseId() {
        return kanalReferanseId;
    }

    public String getBehandlingstema() {
        return behandlingstema;
    }

    public static final class Builder {

        private String hendelsesId;
        private int versjon;
        private String hendelsesType;
        private long journalpostId;
        private String journalpostStatus;
        private String temaGammelt;
        private String temaNytt;
        private String mottaksKanal;
        private String kanalReferanseId;
        private String behandlingstema;

        Builder() {}

        public static Builder enJournalpost() {
            return new Builder();
        }

        public Builder medHendesesId(final String hendelsesId) {
            this.hendelsesId = hendelsesId;
            return this;
        }

        public Builder medVersjon(final int versjon) {
            this.versjon = versjon;
            return this;
        }

        public Builder medHendelsestype(final String hendelsesType) {
            this.hendelsesType = hendelsesType;
            return this;
        }

        public Builder medJournalpostId(final long journalpostId) {
            this.journalpostId = journalpostId;
            return this;
        }

        public Builder medJournalpostStatus(final String journalpostStatus) {
            this.journalpostStatus = journalpostStatus;
            return this;
        }

        public Builder medTemaGammelt(final String temaGammelt) {
            this.temaGammelt = temaGammelt;
            return this;
        }

        public Builder medTemaNytt(final String temaNytt) {
            this.temaNytt = temaNytt;
            return this;
        }

        public Builder medMottakskanal(final String mottaksKanal) {
            this.mottaksKanal = mottaksKanal;
            return this;
        }

        public Builder medKanalreferanseId(final String kanalReferanseId) {
            this.kanalReferanseId = kanalReferanseId;
            return this;
        }

        public Builder medBehandlingstema(final String behandlingstema) {
            this.behandlingstema = behandlingstema;
            return this;
        }

        public InngaaendeJournalpost build() {
            return new InngaaendeJournalpost(this);
        }
    }

    private boolean harDetteTemaet(final List<String> temaer) { return temaer.stream().anyMatch(tema -> temaNytt.equals(tema)); }

    private boolean oppgavetypeMidlertidig() {
        return journalpostStatus.equals("M") || journalpostStatus.equals("MO");
    }

    public boolean skalBehandles(final List<String> temaer) { return harDetteTemaet(temaer) && oppgavetypeMidlertidig(); }

    public String[] ingaaendeJournalpostMetrics() {
        return new String[] {hendelsesType, temaGammelt, temaNytt, mottaksKanal};
    }
}
