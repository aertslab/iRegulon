package view.resultspanel.trackview.detailpanel;

import domainmodel.PredictRegulatorsParameters;
import domainmodel.Track;
import domainmodel.TranscriptionFactor;

public class TFandTrackSelected {

    private Track track;
    private TranscriptionFactor tf;
    private PredictRegulatorsParameters predictRegulatorsParameters;

    public TFandTrackSelected(PredictRegulatorsParameters predictRegulatorsParameters) {
        this.track = null;
        this.tf = null;
        this.predictRegulatorsParameters = predictRegulatorsParameters;
    }


    public Track getTrack() {
        return this.track;
    }

    public void setTrack(Track track) {
        this.track = track;
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
