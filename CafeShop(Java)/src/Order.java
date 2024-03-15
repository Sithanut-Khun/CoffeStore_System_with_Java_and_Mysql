public class Order {
        private String coffeeType;
        private String size;
        private int sugarLevel;
        private String condition;
        private static int nextId = 1;
        private int id;
        private double totalPrice;
        private int quantity;
        private String CoffeeID;

    
        // Constructor
        public Order(String coffeeType, String condition,String size, int sugarLevel, String CoffeeID, int quantity ) {
            this.coffeeType = coffeeType;
            this.size = size;
            this.sugarLevel = sugarLevel;
            this.condition = condition;
            this.CoffeeID = CoffeeID;
            this.quantity = quantity;
            this.id = nextId++;

        }
        // Getter methods
        public int getId() {
            return id;
        }
        public String getCoffeeType() {
            return coffeeType;
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

        public String getCoffeeID() {
            return CoffeeID;
        }

        public int getQuantity(){
            return quantity;
        }
        public double getTotalPrice(){
            return totalPrice;
        }
    
        // Setter methods
        public void setId(int id) {
            this.id = id;
        }
        public void setCoffeeType(String coffeeType) {
            this.coffeeType = coffeeType;
        }
    
        public void setSize(String size) {
            this.size = size;
        }
    
        public void setSugarLevel(int sugarLevel) {
            this.sugarLevel = sugarLevel;
        }
    
        public void setCondition(String condition) {
            this.condition = condition;
        }

        public void setQuantity(String CoffeeID) {
            this.CoffeeID = CoffeeID;
        }

        public void setQuantity(int quantity){
            this.quantity = quantity;
        }
        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

   }
  

