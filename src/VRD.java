public class VRD extends Thread implements Receivable, Device {
    private Refreshable panel;
    private String phoneNumber;
    private int messagesReceived;
    private boolean messageRemoval;
    private boolean terminate;

    public VRD(IDGenerator idGenerator) {
        this.terminate = false;
        this.messageRemoval = false;

        this.phoneNumber = idGenerator.getPhoneNumber();
        this.messagesReceived = 0;

        start();
    }

    @Override
    public void receive(SMS sms) {
        messagesReceived++;
        panel.refresh();
    }

    @Override
    public void run() {
        while(!terminate) {
            if(messageRemoval)
                messagesReceived = 0;
            try {
                Thread.sleep(10000);
            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void setPanel(Refreshable panel) {
        this.panel = panel;
    }

    @Override
    public String getID() {
        return this.phoneNumber;
    }

    @Override
    public String getType() {
        return "VRD";
    }

    @Override
    public void setTerminate(boolean b) {
        terminate = b;
    }
    @Override
    public int getMessagesCount() {
        return messagesReceived;
    }
    @Override
    public void setMessageRemoval(boolean b) {
        messageRemoval = b;
    }

    @Override
    public void setFrequency(int frequency) {
        ;
    }
    @Override
    public int getFrequency() {
        return 0;
    }
    @Override
    public boolean getStatus() {
        return true;
    }
    @Override
    public void setActive(boolean b) {
        ;
    }
}