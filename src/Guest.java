import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Guest {
    private String name;
    private String phoneNumber;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<Service> assignedServices;

    public Guest(String name, String phoneNumber, String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.roomNumber = roomNumber;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
        this.assignedServices = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public List<Service> getServices() {
        return assignedServices;
    }

    public List<Service> getAssignedServices() {
        return assignedServices;
    }

    public void addService(Service service) {
        if (service != null) {
            this.assignedServices.add(service);
        }
    }

    public String toFileString() {
        StringBuilder servicesPart = new StringBuilder();
        if (assignedServices != null) {
            for (Service s : assignedServices) {
                servicesPart.append(s.getName()).append(",");
            }
        }

        return String.join(";",
                name,
                phoneNumber,
                roomNumber,
                checkInDate.toString(),
                checkOutDate.toString(),
                servicesPart.toString()
        );
    }
}