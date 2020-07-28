package database;

public class TypeByMonth {
    private String type;
    private int amount;

    public TypeByMonth(String type) {
        this.type = type;
        this.amount = 1;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
