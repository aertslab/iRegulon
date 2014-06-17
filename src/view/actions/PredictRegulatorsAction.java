package view.actions;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import domainmodel.AbstractMotifAndTrack;
import domainmodel.PredictRegulatorsParameters;
import domainmodel.Results;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.ServerCommunicationException;
import view.ResourceAction;
import view.parametersform.IRegulonType;
import view.parametersform.PredictedRegulatorsParameters;
import view.resultspanel.ResultsCytoPanelComponent;

import javax.swing.*;
import java.awt.*;
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
        final PredictRegulatorsParameters predictRegulatorsParameters = this.parameters.deriveParameters();
        if (!predictRegulatorsParameters.getIRegulonType().equals(IRegulonType.PREDICTED_REGULATORS)) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "This option is not yet implemented.");
            return;
        }

        final List<AbstractMotifAndTrack> motifsAndTracks;
        try {
            motifsAndTracks = service.findPredictedRegulators(predictRegulatorsParameters);
        } catch (ServerCommunicationException e) {
            String errorMessage = deriveMessage(e);

            String lostGenesError = "The following genes were lost:<br>";
            int lostGenesErrorLength = lostGenesError.length();
            int lostGenesErrorFoundIndex = errorMessage.indexOf(lostGenesError);
            if (lostGenesErrorFoundIndex >= 0) {
                // When the error message contains the string "The following genes were lost:<br>", separate the error
                // message from the list of lost genes.
                String errorMessageLostGenes = errorMessage.substring(0, lostGenesErrorFoundIndex + lostGenesErrorLength - 1) + "</html>";

                // Get lost genes from the error message, so they can be displayed in a textarea.
                String lostGenes = errorMessage.substring(lostGenesErrorFoundIndex + lostGenesErrorLength);
                // Replace breaks by newlines.
                lostGenes = lostGenes.replaceAll("<br>", " ");
                // Remove "</html>" from the end of the lost genes string.
                lostGenes = lostGenes.substring(0, lostGenes.length() - 7);

                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());

                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = 0;

                JLabel errorMessageLabel = new JLabel(errorMessageLostGenes);
                panel.add(errorMessageLabel, c);

                // Create a textarea for displaying the lost genes.
                JTextArea textArea = new JTextArea(10, 20);
                textArea.setText(lostGenes);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setCaretPosition(0);
                textArea.setEditable(false);


                c.gridwidth = 3;
                c.gridx = 0;
                c.gridy = 1;

                // Put textarea in a scrollpane.
                JScrollPane scrollPane = new JScrollPane(textArea);

                panel.add(scrollPane, c);

                JOptionPane.showMessageDialog(Cytoscape.getDesktop(), panel, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(), deriveMessage(e), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        if (! motifsAndTracks.isEmpty()) {
            final ResultsCytoPanelComponent outputView = new ResultsCytoPanelComponent(predictRegulatorsParameters.getName(), new Results(motifsAndTracks, predictRegulatorsParameters));
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
