package fm.strength.worm.data;

public class Order extends Detail {

    public final String col;
    public String dir;

    public Order(String col) {
        super(Detail.ORDER);
        this.col = col;
        this.dir = "A";
    }

    public Order ASC() {
        dir = "A";
        return this;
    }

    public Order DESC() {
        dir = "D";
        return this;
    }

}
