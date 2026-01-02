import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EmployeeFrame extends JFrame {
    private JComboBox<String> roomCombo;
    private JTextField nameField, phoneField, stayField;
    private JList<String> serviceList;

    public EmployeeFrame() {
        setTitle("Hotel Operations - Employee Dashboard");
        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        refreshEmployeeUI();
    }

    public void refreshEmployeeUI() {
        getContentPane().removeAll();
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Register Guest", createRegisterTab());
        tabs.addTab("Filter Rooms", createFilterTab());
        tabs.addTab("Checkout / Billing", createCheckoutTab());
        tabs.addTab("Near Checkout Alerts", createCheckoutAlertTab());

        add(tabs);
        revalidate();
        repaint();
    }
    private JPanel createRegisterTab() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("<html><b>Guest Full Name:</b></html>"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("<html><b>Phone Number:</b></html>"), gbc);
        phoneField = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(phoneField, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("<html><b>Available Room:</b></html>"), gbc);

        roomCombo = new JComboBox<>();
        for (Room r : DataRegistry.allRooms) {
            if (r.getStatus().equals("Available")) roomCombo.addItem(r.getRoomNumber());
        }
        gbc.gridx = 1;
        formPanel.add(roomCombo, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("<html><b>Nights to Stay:</b></html>"), gbc);
        stayField = new JTextField(5);
        gbc.gridx = 3;
        formPanel.add(stayField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("<html><b>Extra Services:</b></html>"), gbc);

        DefaultListModel<String> sModel = new DefaultListModel<>();
        for (Service s : DataRegistry.allServices) sModel.addElement(s.getName() + " ($" + s.getPrice() + ")");
        serviceList = new JList<>(sModel);
        serviceList.setVisibleRowCount(4);
        JScrollPane serviceScroll = new JScrollPane(serviceList);

        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(serviceScroll, gbc);

        JButton regBtn = new JButton("Complete Check-In");
        regBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        regBtn.setBackground(new Color(46, 204, 113));
        regBtn.setForeground(Color.WHITE);
        regBtn.setOpaque(true);
        regBtn.setBorderPainted(false);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.insets = new Insets(30, 0, 0, 0);
        formPanel.add(regBtn, gbc);

        regBtn.addActionListener(e -> handleRegistration());

        mainPanel.add(formPanel, BorderLayout.NORTH);
        return mainPanel;
    }

    private void handleRegistration() {
        try {
            String roomNum = (String) roomCombo.getSelectedItem();
            String name = nameField.getText();
            String phone = phoneField.getText();
            int nights = Integer.parseInt(stayField.getText());

            if(name.isEmpty() || phone.isEmpty()) throw new Exception("Please fill Name and Phone.");

            Guest g = new Guest(name, phone, roomNum, LocalDate.now(), LocalDate.now().plusDays(nights));

            for (int i : serviceList.getSelectedIndices()) {
                g.addService(DataRegistry.allServices.get(i));
            }

            Room r = DataRegistry.findRoom(roomNum);
            if (r != null) r.setStatus("Occupied");

            DataRegistry.allGuests.add(g);
            DataRegistry.syncGuests();
            DataRegistry.syncRooms();

            JOptionPane.showMessageDialog(this, "Registration Successful for " + name);
            refreshEmployeeUI();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private JPanel createCheckoutTab() {
        JPanel main = new JPanel(new BorderLayout());
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        for (Guest g : DataRegistry.allGuests) {
            container.add(createCheckoutCard(g));
            container.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        main.add(new JScrollPane(container), BorderLayout.CENTER);
        return main;
    }

    private JPanel createCheckoutCard(Guest g) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(950, 140));
        card.setBackground(Color.WHITE);

        JPanel left = new JPanel(new GridLayout(3, 2, 10, 5));
        left.setOpaque(false);
        left.add(new JLabel("<html><b>Room: #" + g.getRoomNumber() + "</b></html>"));
        left.add(new JLabel("Guest: " + g.getName()));
        left.add(new JLabel("Phone: " + g.getPhoneNumber()));
        left.add(new JLabel("Check-in: " + g.getCheckInDate()));
        left.add(new JLabel("Check-out: " + g.getCheckOutDate()));

        JButton checkoutBtn = new JButton("View Invoice & Checkout");
        checkoutBtn.setBackground(new Color(231, 76, 60));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.addActionListener(e -> showInvoice(g));

        card.add(left, BorderLayout.WEST);
        card.add(checkoutBtn, BorderLayout.EAST);

        return card;
    }

    private void showInvoice(Guest g) {
        Room r = DataRegistry.findRoom(g.getRoomNumber());
        double roomPrice = (r != null) ? r.getPrice() : 0;
        long nights = ChronoUnit.DAYS.between(g.getCheckInDate(), g.getCheckOutDate());
        if (nights <= 0) nights = 1;

        double totalRoomCost = nights * roomPrice;
        double totalServiceCost = 0;

        StringBuilder bill = new StringBuilder();
        bill.append("--- HOTEL INVOICE ---\n");
        bill.append("Guest: ").append(g.getName()).append("\n");
        bill.append("Phone: ").append(g.getPhoneNumber()).append("\n");
        bill.append("Stay: ").append(nights).append(" nights @ $").append(roomPrice).append("\n\n");

        bill.append("Services:\n");
        for (Service s : g.getAssignedServices()) {
            bill.append("- ").append(s.getName()).append(": $").append(s.getPrice()).append("\n");
            totalServiceCost += s.getPrice();
        }

        double finalTotal = totalRoomCost + totalServiceCost;
        bill.append("\n----------------------\n");
        bill.append("TOTAL PAYABLE: $").append(finalTotal);

        JTextArea textArea = new JTextArea(bill.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        int choice = JOptionPane.showConfirmDialog(this, new JScrollPane(textArea),
                "Final Billing - Room " + g.getRoomNumber(), JOptionPane.OK_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {
            handleCheckout(g);
        }
    }

    private void handleCheckout(Guest g) {
        Room r = DataRegistry.findRoom(g.getRoomNumber());
        if (r != null) r.setStatus("Available");
        DataRegistry.allGuests.remove(g);
        DataRegistry.syncRooms();
        DataRegistry.syncGuests();
        refreshEmployeeUI();
    }

    private JPanel createFilterTab() {
        JPanel main = new JPanel(new BorderLayout());
        String[] columns = {"Room #", "Type", "Price", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Available", "Occupied"});
        statusFilter.addActionListener(e -> {
            model.setRowCount(0);
            String selected = (String) statusFilter.getSelectedItem();
            for (Room r : DataRegistry.allRooms) {
                if (selected.equals("All") || r.getStatus().equals(selected)) {
                    model.addRow(new Object[]{r.getRoomNumber(), r.getType(), "$" + r.getPrice(), r.getStatus()});
                }
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Show Rooms:"));
        top.add(statusFilter);

        main.add(top, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        statusFilter.setSelectedIndex(0);
        return main;
    }

    private JPanel createCheckoutAlertTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        p.add(new JLabel("<html><h2>Upcoming Checkouts (Next 48 Hours)</h2></html>"));
        p.add(Box.createRigidArea(new Dimension(0,15)));

        LocalDate threshold = LocalDate.now().plusDays(2);
        boolean found = false;

        for (Guest g : DataRegistry.allGuests) {
            if (!g.getCheckOutDate().isAfter(threshold)) {
                p.add(new JLabel("⚠️ Room " + g.getRoomNumber() + ": " + g.getName() + " (Checkout: " + g.getCheckOutDate() + ")"));
                found = true;
            }
        }
        if(!found) p.add(new JLabel("No immediate checkouts scheduled."));
        return p;
    }
}