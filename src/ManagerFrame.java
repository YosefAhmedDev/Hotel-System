import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class ManagerFrame extends JFrame {

    private DefaultTableModel statsModel;
    private DefaultTableModel roomDetailModel;
    private JLabel totalRevenueLabel;

    public ManagerFrame() {
        setTitle("Manager Dashboard - Hotel Management System");
        setSize(1000, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        refreshUI();
    }

    public void refreshUI() {
        getContentPane().removeAll();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Employees", createEmployeeTab());
        tabs.addTab("Individual Rooms", createRoomTab());
        tabs.addTab("Hotel Services", createServiceTab());
        tabs.addTab("Statistical Reports", createStatsTab());

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 3) {
                refreshStats();
            }
        });

        add(tabs);
        revalidate();
        repaint();
    }

    private JPanel createStatsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        totalRevenueLabel = new JLabel("Total Service Revenue: $0.00");
        totalRevenueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        totalRevenueLabel.setForeground(new Color(41, 128, 185));
        panel.add(totalRevenueLabel, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 20));

        statsModel = new DefaultTableModel(new String[]{"Service Name", "Times Used", "Total Revenue"}, 0);
        JTable statsTable = new JTable(statsModel);
        JScrollPane scroll1 = new JScrollPane(statsTable);
        scroll1.setBorder(BorderFactory.createTitledBorder("Service Performance Summary"));

        String[] detailCols = {"Room #", "Price/Night", "Guest Name", "Checkout Date", "Active Services"};
        roomDetailModel = new DefaultTableModel(detailCols, 0);
        JTable detailTable = new JTable(roomDetailModel);
        JScrollPane scroll2 = new JScrollPane(detailTable);
        scroll2.setBorder(BorderFactory.createTitledBorder("Live Occupancy & Service Tracking"));

        tablesPanel.add(scroll1);
        tablesPanel.add(scroll2);
        panel.add(tablesPanel, BorderLayout.CENTER);

        return panel;
    }

    private void refreshStats() {
        statsModel.setRowCount(0);
        roomDetailModel.setRowCount(0);
        double totalServiceIncome = 0;

        for (Guest g : DataRegistry.allGuests) {
            Room r = DataRegistry.findRoom(g.getRoomNumber());
            String rPrice = (r != null) ? "$" + r.getPrice() : "N/A";

            StringBuilder sb = new StringBuilder();
            if (g.getServices() != null) {
                for (Service s : g.getServices()) {
                    sb.append(s.getName()).append(", ");
                    totalServiceIncome += s.getPrice();
                }
            }
            String services = sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "None";

            roomDetailModel.addRow(new Object[]{
                    g.getRoomNumber(), rPrice, g.getName(), g.getCheckOutDate(), services
            });
        }

        for (Service master : DataRegistry.allServices) {
            int usageCount = 0;
            for (Guest g : DataRegistry.allGuests) {
                for (Service guestSvc : g.getServices()) {
                    if (guestSvc.getName().equals(master.getName())) {
                        usageCount++;
                    }
                }
            }
            if (usageCount > 0) {
                double income = usageCount * master.getPrice();
                statsModel.addRow(new Object[]{
                        master.getName(), usageCount, "$" + String.format("%.2f", income)
                });
            }
        }
        totalRevenueLabel.setText("Total Service Revenue: $" + String.format("%.2f", totalServiceIncome));
    }

    private JPanel createEmployeeTab() {
        JPanel main = new JPanel(new BorderLayout());
        JButton addBtn = new JButton("+ Add New Employee");
        addBtn.setBackground(new Color(60, 179, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("Employee ID:");
            String name = JOptionPane.showInputDialog("Full Name:");
            if (id != null && name != null) {
                DataRegistry.addEmployee(new Employee(id, name, 0.0));
                refreshUI();
            }
        });
        main.add(addBtn, BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        for (Employee emp : DataRegistry.allEmployees) container.add(createEmployeeCard(emp));
        main.add(new JScrollPane(container), BorderLayout.CENTER);
        return main;
    }

    private JPanel createEmployeeCard(Employee emp) {
        JPanel card = createCardBase("Employee ID: " + emp.getId());
        JTextField nameF = new JTextField(emp.getName(), 15);
        JTextField salF = new JTextField(String.valueOf(emp.getSalary()), 15);
        layoutFields(card, new String[]{"Full Name:", "Monthly Salary ($):"}, new JComponent[]{nameF, salF});

        JButton save = new JButton("Save Data");
        save.addActionListener(e -> {
            emp.setName(nameF.getText());
            emp.setSalary(Double.parseDouble(salF.getText()));
            DataRegistry.syncEmployees();
            JOptionPane.showMessageDialog(this, "Updated!");
        });

        JButton del = new JButton("Delete");
        del.setBackground(new Color(220, 20, 60)); del.setForeground(Color.WHITE);
        del.addActionListener(e -> { DataRegistry.deleteEmployee(emp); refreshUI(); });

        addButtons(card, save, del);
        return card;
    }

    private JPanel createRoomTab() {
        JPanel main = new JPanel(new BorderLayout());
        JButton addBtn = new JButton("+ Add Specific Room");
        addBtn.setBackground(new Color(60, 179, 113)); addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            String num = JOptionPane.showInputDialog("Room Number:");
            if (num != null) {
                DataRegistry.addRoom(new Room(num, "Standard", 100.0, "Available"));
                refreshUI();
            }
        });
        main.add(addBtn, BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        for (Room r : DataRegistry.allRooms) container.add(createRoomCard(r));
        main.add(new JScrollPane(container), BorderLayout.CENTER);
        return main;
    }

    private JPanel createRoomCard(Room r) {
        JPanel card = createCardBase("Room #" + r.getRoomNumber());
        JTextField typeF = new JTextField(r.getType(), 15);
        JTextField priceF = new JTextField(String.valueOf(r.getPrice()), 15);
        layoutFields(card, new String[]{"Type:", "Price/Night:"}, new JComponent[]{typeF, priceF});

        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            r.setType(typeF.getText());
            r.setPrice(Double.parseDouble(priceF.getText()));
            DataRegistry.syncRooms();
        });

        JButton del = new JButton("Delete");
        del.setBackground(new Color(220, 20, 60)); del.setForeground(Color.WHITE);
        del.addActionListener(e -> { DataRegistry.deleteRoom(r); refreshUI(); });

        addButtons(card, save, del);
        return card;
    }

    private JPanel createServiceTab() {
        JPanel main = new JPanel(new BorderLayout());
        JButton addBtn = new JButton("+ Add New Service");
        addBtn.setBackground(new Color(60, 179, 113)); addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Service Name:");
            if (name != null) {
                DataRegistry.addService(new Service(name, "Description", 10.0));
                refreshUI();
            }
        });
        main.add(addBtn, BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        for (Service s : DataRegistry.allServices) container.add(createServiceCard(s));
        main.add(new JScrollPane(container), BorderLayout.CENTER);
        return main;
    }

    private JPanel createServiceCard(Service s) {
        JPanel card = createCardBase("Service: " + s.getName());
        JTextField descF = new JTextField(s.getDescription(), 15);
        JTextField priceF = new JTextField(String.valueOf(s.getPrice()), 15);
        layoutFields(card, new String[]{"Description:", "Price ($):"}, new JComponent[]{descF, priceF});

        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            s.setDescription(descF.getText());
            s.setPrice(Double.parseDouble(priceF.getText()));
            DataRegistry.syncServices();
        });

        JButton del = new JButton("Delete");
        del.setBackground(new Color(220, 20, 60)); del.setForeground(Color.WHITE);
        del.addActionListener(e -> { DataRegistry.deleteService(s); refreshUI(); });

        addButtons(card, save, del);
        return card;
    }

    private JPanel createCardBase(String title) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), title));
        card.setMaximumSize(new Dimension(850, 180));
        return card;
    }

    private void layoutFields(JPanel card, String[] labels, JComponent[] fields) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); gbc.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; card.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; card.add(fields[i], gbc);
        }
    }

    private void addButtons(JPanel card, JButton save, JButton del) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 5; gbc.gridx = 0; card.add(del, gbc);
        gbc.gridx = 1; card.add(save, gbc);
    }
}