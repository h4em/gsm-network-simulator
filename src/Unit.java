public interface Unit {

    int getID();

    String getType();
    int getMessagesProcessed();

    int getMessagesPending();

    void setPanel(Refreshable panel);
}
