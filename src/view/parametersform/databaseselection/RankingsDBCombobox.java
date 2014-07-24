package view.parametersform.databaseselection;

import domainmodel.RankingsDatabase;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RankingsDBCombobox extends JComboBox {

    private List<RankingsDatabase> rankingsDatabases;

    public RankingsDBCombobox() {
        super();
        this.rankingsDatabases = new ArrayList<RankingsDatabase>();
    }

    public void updateDatabases(List<RankingsDatabase> newRankingsDatabases) {
        final RankingsDatabase curSelection = (RankingsDatabase) getSelectedItem();
        this.rankingsDatabases = newRankingsDatabases;
        this.removeAllItems();
        for (RankingsDatabase rankingsDatabase : newRankingsDatabases) {
            this.addItem(rankingsDatabase);
        }

        if (rankingsDatabases.size() == 1) {
            setSelectedItem(rankingsDatabases.get(0));
        } else if (rankingsDatabases.contains(curSelection)) {
            setSelectedItem(curSelection);
        } else {
            setSelectedItem(rankingsDatabases.get(0));
        }
    }

    public boolean canBeSelected(RankingsDatabase aRankingsDatabase) {
        return this.rankingsDatabases.contains(aRankingsDatabase);
    }


}
