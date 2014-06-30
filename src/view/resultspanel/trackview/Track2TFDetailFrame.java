package view.resultspanel.trackview;

import cytoscape.Cytoscape;
import domainmodel.AbstractTrack;
import domainmodel.TranscriptionFactor;
import infrastructure.CytoscapeEnvironment;
import org.cytoscape.application.swing.AbstractCyAction;
import view.resultspanel.trackview.detailpanel.TFandTrackSelected;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public final class Track2TFDetailFrame extends JDialog {
    private final TFandTrackSelected information;

    private static final String[] PREFIXES = new String[]{"A", "B", "C", "D"};

    public Track2TFDetailFrame(final TFandTrackSelected information) {
        super(Cytoscape.getDesktop(), true);

        this.information = information;

        setTitle(deriveTitle());
        setContentPane(new JPanel(new BorderLayout()) {
            {
                add(createContentPanel(), BorderLayout.CENTER);
                add(new JPanel(new FlowLayout()) {
                    {
                        add(new JButton(new CloseAction()));
                    }
                }, BorderLayout.SOUTH);
            }
        });
        pack();
    }

    public TFandTrackSelected getInformation() {
        return information;
    }

    private String deriveTitle() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Detail on relation between ");
        builder.append(information.getTrack().getName());
        builder.append(" and ");
        builder.append(information.getTranscriptionFactor().getName());
        return builder.toString();
    }

    private JComponent createContentPanel() {
        final JPanel content = new JPanel(new GridBagLayout());
        final GridBagConstraints cc = new GridBagConstraints();
        cc.anchor = GridBagConstraints.CENTER;
        cc.fill = GridBagConstraints.BOTH;
        cc.gridheight = 1;
        cc.gridwidth = 1;
        cc.weightx = 1.0;
        cc.weighty = 1.0;
        cc.gridx = 0;
        cc.gridy = 0;

        String prevComponent = "Enriched track";
        content.add(createEnrichedTrackPanel(cc.gridy, getInformation().getTrack()), cc);
        cc.gridy++;

        content.add(createTranscriptionFactorPanel(cc.gridy, prevComponent, getInformation().getTrack(), getInformation().getTranscriptionFactor()), cc);

        return new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private JPanel createEnrichedTrackPanel(final int idx, final AbstractTrack track) {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints cc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEtchedBorder());

        addTitle(idx, "Enriched track", cc, panel);
        addLabel("Name", track.getName(), cc, panel);
        addLabel("Description", track.getDescription(), cc, panel);
        addLabel("NEScore", track.getNEScore(), cc, panel);
        //addLogo(track.getName(), cc, panel);

        return panel;
    }

    private JPanel createTranscriptionFactorPanel(final int idx, final String prevComponent, final AbstractTrack track, final TranscriptionFactor factor) {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints cc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEtchedBorder());

        if (prevComponent.equals("Enriched track") || prevComponent.equals("Similar track"))
            addTitle(idx, "Annotated for transcription factor", cc, panel);
        else addTitle(idx, "Orthologous to transcription factor", cc, panel);
        addLabel("Name", factor.getName(), cc, panel);
        addLabel("Species and nomenclature", factor.getSpeciesNomeclature().toString(), cc, panel);

        return panel;
    }

    private void addTitle(final int idx, final String title, final GridBagConstraints cc, final JComponent container) {
        cc.anchor = GridBagConstraints.CENTER;
        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1.0;
        cc.weighty = 0.0;
        cc.gridwidth = 2;
        cc.gridheight = 1;
        cc.gridx = 0;

        final JLabel titleLB = new JLabel(PREFIXES[idx] + ". " + title);
        titleLB.setFont(new Font("Serif", 0, 30));
        container.add(titleLB, cc);

        cc.gridy += 2;
    }

    private void addLabel(final String label, final Object value, final GridBagConstraints cc, final JComponent container) {
        cc.anchor = GridBagConstraints.LINE_START;
        cc.fill = GridBagConstraints.NONE;
        cc.weightx = 0.0;
        cc.weighty = 0.0;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.gridx = 0;

        final JLabel labelLB = new JLabel(label + ":");
        container.add(labelLB, cc);

        cc.anchor = GridBagConstraints.CENTER;
        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1.0;
        cc.weighty = 0.0;
        cc.gridwidth = 1;
        cc.gridheight = 1;
        cc.gridx = 1;

        final JTextField fieldTF = new JTextField(value == null ? "" : value.toString());
        fieldTF.setEditable(false);
        container.add(fieldTF, cc);

        cc.gridy++;
    }

    private class CloseAction extends AbstractCyAction {
        private CloseAction() {
            super("Close");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
}
