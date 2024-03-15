public class Coffee {
    private String type;
    private String size;
    private int sugarLevel;
    private String condition;
    private int stock;
    private double price;
    private String CoffeeID;

    // Constructor
    public Coffee(String CoffeeID, String type, String size, int sugarLevel, String condition, double price, int stock) {
        this.CoffeeID = CoffeeID;
        this.type = type;
        this.size = size;
        this.sugarLevel = sugarLevel;
        this.condition = condition;
        this.stock = stock;
        this.price = price;
    }

    // Getter methods
    public String getCoffeeID(){
        return CoffeeID;
    }
    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public int getSugarLevel() {
        return sugarLevel;
    }

    public String getCondition() {
        return condition;
    }

    public int getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }

    // Setter methods
    public void setCoffeeID(String CoffeeID) {
        this.CoffeeID = CoffeeID;
    }
    public void setType(String type) {
        this.type = type;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setSugarLevel(int sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public void setStock(int stock){
        this.stock = stock;
    }

    public void setPrice(double price) {
        this.price = price;
    }


}

