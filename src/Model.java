import java.util.*;

public class Model implements Intermediate {
    private List<VBD> vbdList;

    private List<BSCRegistry> bscRegistriesList;
    private BTSRegistry leftBTSLayerRegistry;
    private BSCRegistry bscRegistry;
    private BTSRegistry rightBTSLayerRegistry;
    private VRDRegistry vrdRegistry;
    private IDGenerator idGenerator;

    public Model() {
        this.vbdList = new ArrayList<>();

        this.idGenerator = new IDGenerator();

        this.leftBTSLayerRegistry = new BTSRegistry();
        this.rightBTSLayerRegistry = new BTSRegistry();

        this.bscRegistry = new BSCRegistry();
        bscRegistry.register(new BSC(rightBTSLayerRegistry, idGenerator));

        this.bscRegistriesList = new ArrayList<>();
        this.bscRegistriesList.add(bscRegistry);

        this.vrdRegistry = new VRDRegistry();

        leftBTSLayerRegistry.register(new BTS(null, bscRegistry, idGenerator));
        rightBTSLayerRegistry.register(new BTS(vrdRegistry, null, idGenerator));
    }
    public void addVBD(String message) {
        VBD vbd = new VBD(message, 1, this.leftBTSLayerRegistry, this.idGenerator, this.vrdRegistry);
        vbdList.add(vbd);
    }

    public void addVRD() {
        vrdRegistry.register(new VRD(idGenerator));
    }

    public Device getLastVRD() {
        return vrdRegistry.getLast();
    }

    public Unit getUnit(int ID) {
        for(int i = 0; i < leftBTSLayerRegistry.getSize(); i++) {
            BTS bts = leftBTSLayerRegistry.getBTS(i);
            if(bts.getID() == ID)
                return bts;
        }

        for(int i = 0; i < rightBTSLayerRegistry.getSize(); i++) {
            BTS bts = rightBTSLayerRegistry.getBTS(i);
            if(bts.getID() == ID)
                return bts;
        }

        for(int i = 0; i < bscRegistry.getSize(); i++) {
            BSC bsc = bscRegistry.getBSC(i);
            if(bsc.getID() == ID)
                return bsc;
        }

        return null;
    }

    @Override
    public Device getLastVBD() {
        return vbdList.get(vbdList.size() - 1);
    }

    @Override
    public void removeVBD(Device device) {
        for(int i = 0; i < vbdList.size(); i++) {
            VBD vbd = vbdList.get(i);
            if(device == vbd) {
                vbd.setTerminate(true);
                vbdList.remove(vbd);
            }
        }
    }

    @Override
    public void removeVRD(Device device) {
        for(int i = 0; i < vrdRegistry.getSize(); i++) {
            VRD vrd = vrdRegistry.getVRD(i);
            if(device == vrd) {
                vrd.setTerminate(true);
                vrdRegistry.deregister(vrd);
            }
        }
    }

    @Override
    public Registry getLastBSCRegistry() {
        return bscRegistriesList.get(bscRegistriesList.size() - 1);
    }

    @Override
    public Unit getLastBSC() {
        BSCRegistry lastRegistry = bscRegistriesList.get(bscRegistriesList.size() - 1);
        return lastRegistry.getBSC(0);
    }

    @Override
    public void addNewBSCRegistry() {
        BSCRegistry lastRegistry = bscRegistriesList.get(bscRegistriesList.size() - 1);

        BSCRegistry newRegistry = new BSCRegistry();
        newRegistry.register(new BSC(rightBTSLayerRegistry, idGenerator));
        bscRegistriesList.add(newRegistry);

        for(int i = 0; i < lastRegistry.getSize(); i++) {
            BSC bsc = lastRegistry.getBSC(i);
            bsc.setBTSRegistry(null);
            bsc.setBSCRegistry(newRegistry);
        }
    }

    @Override
    public void removeBSCRegistry() {
        if(bscRegistriesList.size() > 1) {
            BSCRegistry newLastRegistry = bscRegistriesList.get(bscRegistriesList.size() - 2);
            for(int i = 0; i < newLastRegistry.getSize(); i++) {
                BSC bsc = newLastRegistry.getBSC(i);
                bsc.setBSCRegistry(null);
                bsc.setBTSRegistry(rightBTSLayerRegistry);
            }

            BSCRegistry lastRegistry = bscRegistriesList.get(bscRegistriesList.size() - 1);
            for(int i = 0; i < lastRegistry.getSize(); i++) {
                BSC bsc = lastRegistry.getBSC(i);
                bsc.passEverything();
            }
            bscRegistriesList.remove(lastRegistry);
        }
    }

    @Override
    public Registry getBSCRegistry() {
        return bscRegistry;
    }

    @Override
    public Registry getLeftBTSRegistry() {
        return leftBTSLayerRegistry;
    }

    @Override
    public Registry getRightBTSRegistry() {
        return rightBTSLayerRegistry;
    }
}