public class Service {
    private String name, description;
    private double price;

    public Service(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public void setName(String n) { name = n; }
    public void setDescription(String d) { description = d; }
    public void setPrice(double p) { price = p; }

    public String toCSV() { return name + "," + description + "," + price; }
}