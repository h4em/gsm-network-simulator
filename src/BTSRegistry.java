import java.util.*;
public class BTSRegistry implements Registry {
    private List<BTS> btsList;
    private Refreshable panel;

    public BTS getBTS() {
        boolean spawnNewBTS = true;
        for(int i = 0; i < btsList.size(); i++) {
            BTS bts = btsList.get(i);
            if(!bts.moreThan5MessagesPending()) {
                spawnNewBTS = false;
                break;
            }
        }

        if(spawnNewBTS) {
            BTS bts = btsList.get(0);
            BTS newBTS = new BTS(bts.getVRDRegistry(), bts.getBSCRegistry(), bts.getIdGenerator());
            register(newBTS);
            panel.addNewUnitPanel(newBTS);
            return newBTS;
        }

        int min = Integer.MAX_VALUE;
        int idx = 0;
        for(int i = 0; i < btsList.size(); i++) {
            BTS bts = btsList.get(i);
            if(bts.getLoad() < min) {
                min = bts.getLoad();
                idx = i;
            }
        }
        return btsList.get(idx);
    }

    public List<BTS> getBtsList() {
        return btsList;
    }
    public BTSRegistry() {
        btsList = new ArrayList<>();
    }
    public void register(BTS bts) {
        btsList.add(bts);
    }

    public BTS getBTS(int i) {
        return btsList.get(i);
    }

    public int getSize() {
        return btsList.size();
    }

    @Override
    public void setPanel(Refreshable panel) {
        this.panel = panel;
    }
}