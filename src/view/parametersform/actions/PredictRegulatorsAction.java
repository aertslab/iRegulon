package view.parametersform.actions;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import domainmodel.AbstractMotifAndTrack;
import domainmodel.InputParameters;
import domainmodel.Results;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.ServerCommunicationException;
import view.ResourceAction;
import view.parametersform.IRegulonType;
import view.parametersform.PredictedRegulatorsParameters;
import view.resultspanel.ResultsView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;


public class PredictRegulatorsAction extends ResourceAction {
    private static final String NAME = "action_submit_analysis";

    private final ComputationalService service = new ComputationalServiceHTTP();
	private final PredictedRegulatorsParameters parameters;
	
	public PredictRegulatorsAction(final PredictedRegulatorsParameters parameters) {
		super(NAME);
		this.parameters = parameters;
	}

    public PredictedRegulatorsParameters getParameters() {
        return parameters;
    }

    @Override
	public void actionPerformed(ActionEvent event) {
        final InputParameters input = this.parameters.deriveParameters();
        if (!input.getIRegulonType().equals(IRegulonType.PREDICTED_REGULATORS)) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "This option is not yet implemented.");
            return;
        }

        final List<AbstractMotifAndTrack> motifsAndTracks;
        try {
            motifsAndTracks = service.findPredictedRegulators(input);
        } catch (ServerCommunicationException e) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), deriveMessage(e), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!motifsAndTracks.isEmpty()) {
            final ResultsView outputView = new ResultsView(input.getName(), new Results(motifsAndTracks, input));
            final CytoPanel panel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
            panel.setState(CytoPanelState.DOCK);
            outputView.addToPanel(panel);
        } else {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "Not a single motif or track is enriched for your input gene signature.");
        }
    }

    private String deriveMessage(ServerCommunicationException e) {
        if (e.getMessage().contains("The following genes\nwere lost:")) {
            final StringBuilder builder = new StringBuilder();
            builder.append("<html>");

            final String[] components = e.getMessage().split(":");
            builder.append(components[0].replace('\n', ' '));
            builder.append(":<br>");

            final List<String> IDs = extractIDs(components[1]);
            if (IDs.size() < 10) {
                builder.append(IDs.get(0));
                for (String ID : IDs.subList(1, IDs.size())) {
                    builder.append(" ");
                    builder.append(ID);
                }
            } else {
                builder.append(IDs.get(0));
                for (String ID : IDs.subList(1, 5)) {
                    builder.append(" ");
                    builder.append(ID);
                }
                builder.append(" ...");
                final int size = IDs.size();
                for (String ID : IDs.subList(size-5, size)) {
                    builder.append(" ");
                    builder.append(ID);
                }
            }

            builder.append("</html>");
            return builder.toString();
        } else {
            return e.getMessage();
        }
    }

    private List<String> extractIDs(final String text) {
        final String[] IDs = text.split(";");
        final List<String> result = new ArrayList<String>();
        for (String ID : IDs) {
            result.add(ID.replace("\n", "").trim());
        }
        return result;
    }
}
