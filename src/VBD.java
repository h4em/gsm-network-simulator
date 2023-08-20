public class VBD extends Thread implements Device {
    private String phoneNumber;
    private String message;
    private int messagesSent;    
    private int sendingFrequency;
    private boolean isActive;
    private boolean terminate;

    private BTSRegistry btsRegistry;
    private VRDRegistry vrdRegistry;

    public VBD(String message, int frequency, BTSRegistry btsRegistry, IDGenerator idGenerator, VRDRegistry vrdRegistry) {
        this.terminate = false;
        this.isActive = true;
        
        this.btsRegistry = btsRegistry;
        this.vrdRegistry = vrdRegistry;

        this.phoneNumber = idGenerator.getPhoneNumber();
        this.message = message;

        this.messagesSent = 0;
        this.sendingFrequency = frequency;
        
        start();
    }

    @Override
    public void run() {
        while(!terminate) {
            if(!isActive) {
                synchronized(this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            if(vrdRegistry.isEmpty() || sendingFrequency == 0) {
                try {
                    Thread.sleep(1000);
                    continue;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                long interval = (long)(1000.0 / sendingFrequency);
                Thread.sleep(interval);
            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }

            BTS bts = btsRegistry.getBTS();
            VRD vrd = vrdRegistry.getRandom();
            
            if(vrd != null) {
                bts.receive(new SMS(phoneNumber, vrd.getID(), message));
                messagesSent++;
            }
        }
    }

    public void proceed() {
        synchronized (this) {
            this.notify();
        }
        this.isActive = true;
    }

    public void hold() {
        this.isActive = false;
    }

    public int phoneNumAsInt() {
        int res = 0;
        int pow = (int) Math.pow(10, 8);
        for(int i = 0; i < phoneNumber.length(); i++) {
            char d = phoneNumber.charAt(i);
            if(Character.isDigit(d)) {
                res += (d - '0') * pow;
                pow /= 10;
            }
        }
        return res;
    }
    @Override
    public int getFrequency() {
        return sendingFrequency;
    }

    @Override
    public void setActive(boolean b) {
        this.isActive = b;
        if(isActive) {
            synchronized (this) {
                this.notify();
            }
        }
    }

    @Override
    public String getType() {
        return "VBD";
    }

    @Override
    public void setFrequency(int frequency) {
        if(sendingFrequency == 0) {
            synchronized (this) {
                this.notify();
            }
        }
        this.sendingFrequency = frequency;
    }

    @Override
    public void setTerminate(boolean b) {
        this.terminate = b;
    }

    @Override
    public boolean getStatus() {
        return this.isActive;
    }

    @Override
    public String getID() {
        return this.phoneNumber;
    }

    @Override
    public int getMessagesCount() {
        return this.messagesSent;
    }
    @Override
    public void setMessageRemoval(boolean b) {
        ;
    }

    @Override
    public void setPanel(Refreshable panel) {
        ;
    }
}