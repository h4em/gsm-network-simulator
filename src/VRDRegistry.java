import java.util.*;

public class VRDRegistry {
    List<VRD> vrdList;
    public VRDRegistry() {
        vrdList = new ArrayList<>();
    }
    public void register(VRD vrd) {
        vrdList.add(vrd);
    }

    public void deregister(VRD vrd) {
        vrdList.remove(vrd);
    }

    public VRD getVRDOfNumber(String phoneNumber) throws VRDNotFoundException {
        for(int i = 0; i < vrdList.size(); i++) {
            VRD vrd = vrdList.get(i);
            if(vrd.getID().equals(phoneNumber))
                return vrd;
        }
        throw new VRDNotFoundException("VRD does not exist.");
    }

    public boolean isEmpty() {
        return vrdList.size() == 0;
    }
    public VRD getRandom() {
        if(vrdList.size() != 0) {
            int r = (int)(Math.random() * vrdList.size());
            return vrdList.get(r);
        }
        return null;
    }

    public VRD getVRD(int i) {
        return vrdList.get(i);
    }

    public int getSize() {
        return vrdList.size();
    }

    public VRD getLast() {
        if(vrdList.size() == 0)
            return null;
        return vrdList.get(vrdList.size() - 1);
    }
}
