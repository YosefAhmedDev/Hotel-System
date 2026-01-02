import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class DataRegistry {
    public static List<User> allUsers = new ArrayList<>();
    public static List<Employee> allEmployees = new ArrayList<>();
    public static List<Room> allRooms = new ArrayList<>();
    public static List<Service> allServices = new ArrayList<>();
    public static List<Guest> allGuests = new ArrayList<>();

    public static void loadFromFiles() {
        allUsers = loadUsers();
        allEmployees = loadEmployees();
        allRooms = loadRooms();
        allServices = loadServices();
        allGuests = loadGuests();
    }

    public static Room findRoom(String roomNum) {
        for (Room r : allRooms) {
            if (r.getRoomNumber().equals(roomNum)) return r;
        }
        return null;
    }

    private static List<Guest> loadGuests() {
        List<Guest> list = new ArrayList<>();
        File file = new File("guests.txt");
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";", -1);

                if (p.length >= 5) {

                    Guest g = new Guest(p[0], p[1], p[2], LocalDate.parse(p[3]), LocalDate.parse(p[4]));

                    if (p.length > 5 && !p[5].trim().isEmpty()) {
                        String[] sNames = p[5].split(",");
                        for (String sName : sNames) {
                            for (Service s : allServices) {
                                if (s.getName().equalsIgnoreCase(sName.trim())) {
                                    g.addService(s);
                                }
                            }
                        }
                    }
                    list.add(g);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading guests: " + e.getMessage());
        }
        return list;
    }

    private static List<User> loadUsers() {
        List<User> list = new ArrayList<>();
        File file = new File("users.txt");
        if (!file.exists()) {
            list.add(new User("admin", "admin", "MANAGER"));
            list.add(new User("staff", "1234", "EMPLOYEE"));
            return list;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 3) list.add(new User(p[0], p[1], p[2]));
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private static List<Employee> loadEmployees() {
        List<Employee> list = new ArrayList<>();
        File file = new File("employees.txt");
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 3) list.add(new Employee(p[0], p[1], Double.parseDouble(p[2])));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private static List<Room> loadRooms() {
        List<Room> list = new ArrayList<>();
        File file = new File("rooms.txt");
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 4) list.add(new Room(p[0], p[1], Double.parseDouble(p[2]), p[3]));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private static List<Service> loadServices() {
        List<Service> list = new ArrayList<>();
        File file = new File("services.txt");
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 3) list.add(new Service(p[0], p[1], Double.parseDouble(p[2])));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static void syncGuests() {
        save("guests.txt", allGuests.stream().map(Guest::toFileString).toList());
    }

    public static void syncRooms() {
        save("rooms.txt", allRooms.stream().map(Room::toCSV).toList());
    }

    public static void syncServices() {
        save("services.txt", allServices.stream().map(Service::toCSV).toList());
    }

    public static void syncEmployees() {
        save("employees.txt", allEmployees.stream().map(Employee::toCSV).toList());
    }

    public static void addRoom(Room r) { allRooms.add(r); syncRooms(); }
    public static void deleteRoom(Room r) { allRooms.remove(r); syncRooms(); }
    public static void addService(Service s) { allServices.add(s); syncServices(); }
    public static void deleteService(Service s) { allServices.remove(s); syncServices(); }
    public static void addEmployee(Employee e) { allEmployees.add(e); syncEmployees(); }
    public static void deleteEmployee(Employee e) { allEmployees.remove(e); syncEmployees(); }

    private static void save(String filename, List<String> lines) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (String line : lines) out.println(line);
        } catch (IOException e) { e.printStackTrace(); }
    }
}