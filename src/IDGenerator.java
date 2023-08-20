import java.util.*;

public class IDGenerator {
    private int lastUnitID;
    private int lastBSCID;
    private Set<String> generatedPhoneNumbers;

    public IDGenerator() {
        lastUnitID = 0;
        generatedPhoneNumbers = new HashSet<>();
    }

    public int getUnitID() { return ++lastUnitID; };

    public String getPhoneNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while(true) {
            sb.setLength(12);

            for(int i = 1; i < 12; i++) {
                if(i % 4 == 0) {
                    sb.append("-"); 
                    continue;
                }
                int digit = random.nextInt(10);
                sb.append(String.valueOf(digit));
            }

            String phoneNumber = sb.toString();

            if(!generatedPhoneNumbers.contains(phoneNumber)) {
                generatedPhoneNumbers.add(phoneNumber);
                return phoneNumber;
            }
        }
    }
}