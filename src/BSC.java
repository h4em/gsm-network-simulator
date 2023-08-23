import java.util.concurrent.ConcurrentLinkedQueue;


public class BSC extends Thread implements Receivable, Unit {
    private int ID;
    private int messagesProcessed;
    private ConcurrentLinkedQueue<SMS> messages;

    private BTSRegistry btsRegistry;
    private BSCRegistry bscRegistry;
    private IDGenerator idGenerator;

    private Refreshable panel;

    public BSC(BTSRegistry btsRegistry, IDGenerator idGenerator) {
        messages = new ConcurrentLinkedQueue<>();

        this.btsRegistry = btsRegistry;
        this.idGenerator = idGenerator;

        this.ID = idGenerator.getUnitID();
        this.messagesProcessed = 0;
    
        start();
    }

    @Override
    public void run() {
        while(!terminate) {
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
                long interval = 1000 * (int)(Math.random() * 11) + 5;
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

            if(btsRegistry != null) {
                BTS bts = btsRegistry.getBTS();
                bts.receive(sms);
            }
        }
    }

    private boolean terminate = false;
    public void passEverything() {
        while(getLoad() != 0) {
            SMS sms = messages.poll();

            if(bscRegistry != null) {
                BSC bsc = bscRegistry.getBSC();
                bsc.receive(sms);
            }

            if(btsRegistry != null) {
                BTS bts = btsRegistry.getBTS();
                bts.receive(sms);
            }
        }
        this.terminate = true;
    }

    public void setBTSRegistry(BTSRegistry btsRegistry) {
        synchronized (this) {
            this.btsRegistry = btsRegistry;
        }
    }

    public void setBSCRegistry(BSCRegistry bscRegistry) {
        synchronized (this) {
            this.bscRegistry = bscRegistry;
        }
    }

    public BSCRegistry getBSCRegistry() {
        return this.bscRegistry;
    }

    @Override
    public void receive(SMS sms) {
        if(sms != null) {
            messages.add(sms);
            panel.refresh();
            this.proceed();
        }
    }

    public void proceed() {
        synchronized (this) {
            this.notify();
        }
    }

    public boolean moreThan5MessagesPending() {
        return messages.size() > 5;
    }

    public int getLoad() {
        return messages.size();
    }

    public IDGenerator getIdGenerator() {
        return idGenerator;
    }

    public BTSRegistry getBTSRegistry() {
        return btsRegistry;
    }

    public boolean isNew() {
        return getLoad() == 0 && getMessagesProcessed() == 0;
    }

    @Override
    public String getType() {
        return "BSC";
    }

    @Override
    public int getMessagesPending() {
        return this.getLoad();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public int getMessagesProcessed() {
        return messagesProcessed;
    }

    public void setPanel(Refreshable panel) {
        this.panel = panel;
    }
}