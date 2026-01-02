import java.time.LocalDate;

public class Reservation {
    private String customerName;
    private String phone;
    private String roomType;
    private String services;
    private LocalDate checkOutDate;

    public Reservation(String name, String phone, String room, String services, LocalDate date) {
        this.customerName = name;
        this.phone = phone;
        this.roomType = room;
        this.services = services;
        this.checkOutDate = date;
    }

    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public String getRoomType() { return roomType; }
    public String getServices() { return services; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
}