package view.resultspanel.motifview.detailpanel;

import domainmodel.PredictRegulatorsParameters;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

public class TFandMotifSelected {

    private Motif motif;
    private TranscriptionFactor tf;
    private PredictRegulatorsParameters predictRegulatorsParameters;

    public TFandMotifSelected(PredictRegulatorsParameters predictRegulatorsParameters) {
        this.motif = null;
        this.tf = null;
        this.predictRegulatorsParameters = predictRegulatorsParameters;
    }


    public Motif getMotif() {
        return this.motif;
    }

    public void setMotif(Motif motif) {
        this.motif = motif;
    }

    public TranscriptionFactor getTranscriptionFactor() {
        return this.tf;
    }

    public void setTranscriptionFactor(TranscriptionFactor tf) {
        this.tf = tf;
    }

    public PredictRegulatorsParameters getPredictRegulatorsParameters() {
        return this.predictRegulatorsParameters;
    }

}
