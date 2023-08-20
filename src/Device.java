public interface Device {

    String getID();

    boolean getStatus();

    String getType();

    void setTerminate(boolean b);

    void setActive(boolean b);

    int getFrequency();

    void setFrequency(int frequency);

    int getMessagesCount();
    void setMessageRemoval(boolean b);
    void setPanel(Refreshable panel);
}
