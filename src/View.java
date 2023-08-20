import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View extends JFrame {
    private Intermediate inter;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(View::new);
    }

    public void init(Intermediate inter) {
        this.inter = inter;
        this.add(new MiddlePanel(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public View() {
        this.setTitle("S28546Projekt03");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(1280, 960));

        this.add(new LeftPanel(), BorderLayout.LINE_START);
        this.add(new RightPanel(), BorderLayout.LINE_END);

        this.setVisible(true);
    }

    class LeftPanel extends JPanel {
        public LeftPanel() {
            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(256,100));

            JPanel vbdLayerPanel = new JPanel();
            vbdLayerPanel.setLayout(new BoxLayout(vbdLayerPanel, BoxLayout.Y_AXIS));
            vbdLayerPanel.setOpaque(false);

            this.add(new JScrollPane(vbdLayerPanel), BorderLayout.CENTER);

            JButton jButton = new JButton("Add");
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String message = JOptionPane.showInputDialog(null, "Enter message:", "Add VBD", JOptionPane.PLAIN_MESSAGE);
                    if(message != null) {
                        inter.addVBD(message);
                        vbdLayerPanel.add(new VBDPanel(inter.getLastVBD(), vbdLayerPanel));
                        vbdLayerPanel.revalidate();
                        vbdLayerPanel.repaint();
                    }
                }
            });
            this.add(jButton, BorderLayout.PAGE_END);
        }
    }

    class VBDPanel extends JPanel {
        public VBDPanel(Device device, JPanel outerPanel) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            this.setOpaque(false);

            JPanel IDPanel = new JPanel();
            IDPanel.add(new JLabel(device.getType() + "-ID: " + device.getID()));

            JPanel frequencyPanel = new JPanel();
            frequencyPanel.setLayout(new BoxLayout(frequencyPanel, BoxLayout.Y_AXIS));
            JLabel frequencyLabel = new JLabel("Frequency: " + device.getFrequency());
            frequencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            frequencyPanel.add(frequencyLabel);

            JSlider jSlider = new JSlider(JSlider.HORIZONTAL, 0, 20, device.getFrequency());
            jSlider.setPaintTicks(true);
            jSlider.setPaintLabels(true);
            jSlider.setMajorTickSpacing(2);
            jSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    device.setFrequency(jSlider.getValue());
                    frequencyLabel.setText("Frequency: " + device.getFrequency());
                }
            });
            frequencyPanel.add(jSlider);

            JPanel statusPanel = new JPanel();
            statusPanel.add(new JLabel("Status: "));
            String[] options = {"Active", "Waiting"};
            JComboBox<String> jComboBox = new JComboBox<>(options);
            jComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String option = (String) jComboBox.getSelectedItem();
                    device.setActive(option.equals("Active"));
                }
            });
            statusPanel.add(jComboBox);

            JButton jButton = new JButton("Remove");
            jButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inter.removeVBD(device);
                    outerPanel.remove(VBDPanel.this);
                    outerPanel.revalidate();
                    outerPanel.repaint();
                }
            });

            this.add(IDPanel);
            this.add(frequencyPanel);
            this.add(statusPanel);
            this.add(jButton);
        }
    }

    //TODO: dodawanie, usuwanie BSC
    class MiddlePanel extends JPanel  {
        public MiddlePanel() {
            this.setLayout(new BorderLayout());
            this.setOpaque(false);

            JPanel layersWrapPanel = new JPanel();
            layersWrapPanel.setLayout(new BoxLayout(layersWrapPanel, BoxLayout.X_AXIS));
            layersWrapPanel.setOpaque(false);

            //leftBTSLayer
            RegistryPanel leftBTSPanel = new RegistryPanel(inter.getLeftBTSRegistry());
            leftBTSPanel.add(new UnitPanel(inter.getUnit(2)));

            //BSCLayer
            RegistryPanel BSCPanel = new RegistryPanel(inter.getBSCRegistry());
            BSCPanel.add(new UnitPanel(inter.getUnit(1)));

            //rightBTSLayer
            RegistryPanel rightBTSPanel = new RegistryPanel(inter.getRightBTSRegistry());
            rightBTSPanel.add(new UnitPanel(inter.getUnit(3)));

            layersWrapPanel.add(new JScrollPane(leftBTSPanel));
            layersWrapPanel.add(new JScrollPane(BSCPanel));
            layersWrapPanel.add(new JScrollPane(rightBTSPanel));


            JPanel buttonsPanel = new JPanel();
            JButton addButton = new JButton("+");
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inter.addNewBSCRegistry();
                    RegistryPanel newRegistryPanel = new RegistryPanel(inter.getLastBSCRegistry());
                    newRegistryPanel.add(new UnitPanel(inter.getLastBSC()));

                    int n = layersWrapPanel.getComponentCount();
                    layersWrapPanel.add(new JScrollPane(newRegistryPanel), n - 1);

                    MiddlePanel.super.revalidate();
                    MiddlePanel.super.repaint();
                }
            });

            JButton removeButton = new JButton("-");
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inter.removeBSCRegistry();
                    int n = layersWrapPanel.getComponentCount();
                    if(n > 3) {
                        layersWrapPanel.remove(n - 2);
                    }
                    MiddlePanel.super.revalidate();
                    MiddlePanel.super.repaint();
                }
            });

            buttonsPanel.add(addButton);
            buttonsPanel.add(removeButton);

            this.add(layersWrapPanel, BorderLayout.CENTER);
            this.add(buttonsPanel, BorderLayout.PAGE_END);
        }
    }

    class RegistryPanel extends JPanel implements Refreshable {
        public RegistryPanel(Registry registry) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            registry.setPanel(this);
        }

        @Override
        public void addNewUnitPanel(Unit unit) throws IllegalArgumentException {
            this.add(new UnitPanel(unit));
        }

        @Override
        public void refresh() {

        }
    }

    class UnitPanel extends JPanel implements Refreshable {
        private final Unit unit;
        private final JPanel pendingSMSPanel;
        private final JPanel processedSMSPanel;
        public UnitPanel(Unit unit){
            this.unit = unit;
            unit.setPanel(this);

            this.setLayout(new BorderLayout());
            this.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            this.setOpaque(false);

            JPanel IDPanel = new JPanel();
            IDPanel.add(new JLabel(unit.getType() + "-ID: " + unit.getID()));

            pendingSMSPanel = new JPanel();
            pendingSMSPanel.add(new JLabel("Pending SMS: " + unit.getMessagesPending()));

            processedSMSPanel = new JPanel();
            processedSMSPanel.add(new JLabel("Processed SMS: " + unit.getMessagesProcessed()));

            this.add(IDPanel, BorderLayout.PAGE_START);
            this.add(pendingSMSPanel, BorderLayout.CENTER);
            this.add(processedSMSPanel, BorderLayout.PAGE_END);
        }
        @Override
        public void refresh() {
            Component comp = pendingSMSPanel.getComponent(0);
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText("SMS pending: " + unit.getMessagesPending());
            }

            comp = processedSMSPanel.getComponent(0);
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText("SMS processed: " + unit.getMessagesProcessed());
            }
        }
        @Override
        public void addNewUnitPanel(Unit unit) throws IllegalArgumentException { }
    }

    class RightPanel extends JPanel {
        public RightPanel(){
            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(256,100));

            JPanel vrdLayerPanel = new JPanel();
            vrdLayerPanel.setLayout(new BoxLayout(vrdLayerPanel, BoxLayout.Y_AXIS));
            vrdLayerPanel.setOpaque(false);

            this.add(new JScrollPane(vrdLayerPanel), BorderLayout.CENTER);

            JButton jButton = new JButton("Add");
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inter.addVRD();
                    vrdLayerPanel.add(new VRDPanel(inter.getLastVRD(), vrdLayerPanel));
                    vrdLayerPanel.revalidate();
                    vrdLayerPanel.repaint();
                }
            });
            this.add(jButton, BorderLayout.PAGE_END);
        }
    }
    class VRDPanel extends JPanel implements Refreshable {
        private final Device device;
        private final JPanel receivedSMSPanel;


        public VRDPanel(Device device, JPanel outerPanel) {
            this.device = device;
            device.setPanel(this);

            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            this.setOpaque(false);


            JPanel IDPanel = new JPanel();
            IDPanel.add(new JLabel(device.getType() + "-ID: " + device.getID()));

            receivedSMSPanel = new JPanel();
            receivedSMSPanel.add(new JLabel("SMS received: " + device.getMessagesCount()));

            JPanel autoRemovePanel = new JPanel();
            JCheckBox jCheckBox = new JCheckBox();
            jCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    device.setMessageRemoval(jCheckBox.isSelected());
                }
            });
            autoRemovePanel.add(new JLabel("Auto remove SMS: "));
            autoRemovePanel.add(jCheckBox);

            JButton jButton = new JButton("Remove");
            jButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inter.removeVRD(device);
                    outerPanel.remove(VRDPanel.this);
                    outerPanel.revalidate();
                    outerPanel.repaint();
                }
            });

            this.add(IDPanel);
            this.add(receivedSMSPanel);
            this.add(autoRemovePanel);
            this.add(jButton);
        }
        @Override
        public void refresh() {
            Component comp = receivedSMSPanel.getComponent(0);
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText("SMS received: " + device.getMessagesCount());
            }
        }
        @Override
        public void addNewUnitPanel(Unit unit) {

        }
    }
}