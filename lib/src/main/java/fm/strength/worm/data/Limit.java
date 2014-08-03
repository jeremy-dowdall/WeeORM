package fm.strength.worm.data;

public class Limit extends Detail {

    public int l;
    public int o;

    public Limit(int l) {
        super(Detail.LIMIT);
        this.l = l;
    }

    public Limit withOffset(int offset) {
        this.o = offset;
        return this;
    }

}
