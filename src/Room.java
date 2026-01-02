public class Room {
    private String roomNumber;
    private String type;
    private double price;
    private String status; // "Available", "Occupied"

    public Room(String roomNumber, String type, double price, String status) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = status;
    }

    public String getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }

    public void setType(String type) { this.type = type; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }

    // CSV format: RoomNumber,Type,Price,Status
    public String toCSV() {
        return roomNumber + "," + type + "," + price + "," + status;
    }
}