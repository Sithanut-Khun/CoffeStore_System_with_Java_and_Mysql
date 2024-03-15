import java.time.LocalDate;


public class Transaction {
    private LocalDate date;
    private int transactionID;
    private int customer_perDay;
    private double revenue_perDay;
    private int quantity_sale_perDay;
    private int total_customer;
    private double total_revenue;
    private int total_quantity_sale;

    public Transaction(LocalDate date, int transactionID, int customer_perDay, double revenue_perDay,
                        int quantity_sale_perDay, int total_customer, double total_revenue, int total_quantity_sale) {
        this.date = date;
        this.transactionID = transactionID;
        this.customer_perDay = customer_perDay;
        this.revenue_perDay = revenue_perDay;
        this.quantity_sale_perDay = quantity_sale_perDay;
        this.total_customer = total_customer;
        this.total_revenue = total_revenue;
        this.total_quantity_sale = total_quantity_sale;
    }


    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public int getTransactionID() {
        return transactionID;
    }
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }
    public int getCustomer_perDay() {
        return customer_perDay;
    }
    public void setCustomer_perDay(int customer_perDay) {
        this.customer_perDay = customer_perDay;
    }
    public double getRevenue_perDay() {
        return revenue_perDay;
    }
    public void setRevenue_perDay(double revenue_perDay) {
        this.revenue_perDay = revenue_perDay;
    }
    public int getQuantity_sale_perDay() {
        return quantity_sale_perDay;
    }
    public void setQuantity_sale_perDay(int quantity_sale_perDay) {
        this.quantity_sale_perDay = quantity_sale_perDay;
    }
    public int getTotal_customer() {
        return total_customer;
    }
    public void setTotal_customer(int total_customer) {
        this.total_customer = total_customer;
    }
    public double getTotal_revenue() {
        return total_revenue;
    }
    public void setTotal_revenue(double total_revenue) {
        this.total_revenue = total_revenue;
    }
    public int getTotal_quantity_sale() {
        return total_quantity_sale;
    }
    public void setTotal_quantity_sale(int total_quantity_sale) {
        this.total_quantity_sale = total_quantity_sale;
    }
}


