package persistence;


import domainmodel.AbstractMotif;
import domainmodel.TranscriptionFactor;
import view.IRegulonResourceBundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public final class BedConversionUtilities extends IRegulonResourceBundle {
    public static final BedConversionUtilities INSTANCE = new BedConversionUtilities();

    private static final String PARAMETER_NAME = "featureIDandTarget=";

    private BedConversionUtilities() {
    }

    public String createUCSCResourceLink(final AbstractMotif motif) {
        final StringBuilder builder = new StringBuilder();
        builder.append(getBundle().getString("URL_UCSC"));
        builder.append(getBundle().getString("URL_motifBedGenerator"));
        builder.append("?");
        builder.append(generateParameters(motif));
        return builder.toString();
    }

	public String getRegionsBed(final AbstractMotif motif) throws BedException {
	    final StringBuilder regions = new StringBuilder();
		try {
			final URL url = new URL(getBundle().getString("URL_motifBedGenerator") + "?" + generateParameters(motif));
			final URLConnection connection = url.openConnection();
		    connection.setDoInput(true);
		    connection.setDoOutput(true);

		    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    String line; int lineNr = 0;
		    while ((line = reader.readLine()) != null) {
                if (lineNr < 2) continue; //Delete first 2 lines ...
                regions.append(line);
                regions.append('\n');
		    	lineNr++;
		    }
		    reader.close();

			return regions.toString();
		} catch (IOException e) {
            throw new BedException(regions.toString(), e);
		}
	}

	private String generateParameters(final AbstractMotif motif) {
		final StringBuilder factors = new StringBuilder();
		for (TranscriptionFactor tf : motif.getTranscriptionFactors()) {
			if (factors.length() != 0) factors.append(",");
			factors.append(tf.getName());
		}
		factors.setLength(factors.length() - 2);

        final StringBuilder parameters = new StringBuilder();
        parameters.append(PARAMETER_NAME);
        parameters.append(motif.getDatabaseID());
        parameters.append(':');
        if (motif.getCandidateTargetGenes().size() >= 1) {
            parameters.append(motif.getCandidateTargetGenes().get(0).getGeneName());
		}

        parameters.append(':');
        parameters.append(factors);
		return parameters.toString();
	}
}
