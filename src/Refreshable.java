public interface Refreshable {
    void refresh();

    void addNewUnitPanel(Unit unit) throws IllegalArgumentException;
}
