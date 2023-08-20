import java.util.*;

public class BSCRegistry implements Registry {
    private List<BSC> bscList;
    private Refreshable panel;

    public BSC getBSC() {
        boolean spawnNewBSC = true;
        for(int i = 0; i < bscList.size(); i++) {
            BSC bsc = bscList.get(i);
            if(!bsc.moreThan5MessagesPending()) {
                spawnNewBSC = false;
                break;
            }
        }

        if(spawnNewBSC) {
            BSC bsc = bscList.get(0);
            BSC newBSC = new BSC(bsc.getBTSRegistry(), bsc.getIdGenerator());
            newBSC.setBSCRegistry(bsc.getBSCRegistry());
            register(newBSC);
            panel.addNewUnitPanel(newBSC);
            return newBSC;
        }

        int min = Integer.MAX_VALUE;
        int idx = 0;
        for(int i = 0; i < bscList.size(); i++) {
            BSC bsc = bscList.get(i);
            if(bsc.getLoad() < min) {
                min = bsc.getLoad();
                idx = i;
            }
        }
        return bscList.get(idx);
    }

    public BSCRegistry() {
        bscList = new ArrayList<>();
    }

    public void register(BSC bsc) {
        bscList.add(bsc);
    }

    @Override
    public void setPanel(Refreshable panel) {
        this.panel = panel;
    }
    public BSC getBSC(int i) {
        return bscList.get(i);
    }

    public int getSize() {
        return bscList.size();
    }
}