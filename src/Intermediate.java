public interface Intermediate {

    void addVRD();
    void addVBD(String message);

    void addNewBSCRegistry();

    void removeBSCRegistry();

    Registry getLastBSCRegistry();

    Unit getLastBSC();

    Device getLastVRD();

    Device getLastVBD();

    Unit getUnit(int ID);

    Registry getLeftBTSRegistry();

    Registry getBSCRegistry();

    Registry getRightBTSRegistry();

    void removeVBD(Device device);

    void removeVRD(Device device);
}