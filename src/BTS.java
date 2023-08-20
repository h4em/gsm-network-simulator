import java.util.concurrent.ConcurrentLinkedQueue;

public class BTS extends Thread implements Receivable, Unit {
    private int ID;
    private int messagesProcessed;
    private ConcurrentLinkedQueue<SMS> messages;

    private BSCRegistry bscRegistry; //tylko bts-y z lewej warstwy
    private VRDRegistry vrdRegistry; //tylko bts-y z prawej warstwy;
    private IDGenerator idGenerator;

    private Refreshable panel;

    public BTS(VRDRegistry vrdRegistry, BSCRegistry bscRegistry, IDGenerator idGenerator) {
        this.messages = new ConcurrentLinkedQueue<>();

        this.vrdRegistry = vrdRegistry;
        this.bscRegistry = bscRegistry;
        this.idGenerator = idGenerator;

        this.ID = idGenerator.getUnitID();
        this.messagesProcessed = 0;

        start();
    }

    @Override
    public void run() {
        while(true) {
            if(this.getLoad() == 0) {
                synchronized(this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            try {
                long interval = 3000;
                Thread.sleep(interval);
            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }

            SMS sms = messages.poll();
            messagesProcessed++;

            panel.refresh();

            if(bscRegistry != null) {
                BSC bsc = bscRegistry.getBSC();
                bsc.receive(sms);
            }

            if(vrdRegistry != null) {
                try {
                    VRD vrd = vrdRegistry.getVRDOfNumber(sms.getReceiver());
                    vrd.receive(sms);
                } catch(VRDNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }    
    }

    @Override
    public void receive(SMS sms) {
        messages.add(sms);
        panel.refresh();
        this.proceed();
    }

    public void proceed() {
        synchronized (this) {
            this.notify();
        }
    }

    public boolean isNew() {
        return getLoad() == 0 && getMessagesProcessed() == 0;
    }

    public boolean moreThan5MessagesPending() {
        return messages.size() > 5;
    }

    public int getLoad() {
        return messages.size();
    }

    public VRDRegistry getVRDRegistry() {
        return vrdRegistry;
    }

    public BSCRegistry getBSCRegistry() {
        return bscRegistry;
    }

    public IDGenerator getIdGenerator() {
        return idGenerator;
    }

    @Override
    public int getID() {
        return ID;
    }
    @Override
    public String getType() {
        return "BTS";
    }

    @Override
    public void setPanel(Refreshable panel) {
        this.panel = panel;
    }

    @Override
    public int getMessagesPending() {
        return this.getLoad();
    }

    @Override
    public int getMessagesProcessed() {
        return messagesProcessed;
    }
}