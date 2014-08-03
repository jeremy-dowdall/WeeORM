package fm.strength.worm.data;

import java.util.Arrays;

public class Detail {

    public static Detail[] append(Detail[] details, Detail last) {
        Detail[] merged = Arrays.copyOf(details, details.length + 1);
        merged[merged.length - 1] = last;
        return merged;
    }

    public static Detail[] merge(Detail[] original, Detail[] additional) {
        if(original == null) return additional;
        if(additional == null) return original;
        Detail[] merged = new Detail[original.length + additional.length];
        System.arraycopy(original,   0, merged, 0,               original.length);
        System.arraycopy(additional, 0, merged, original.length, additional.length);
        return merged;
    }

    public static Detail[] prepend(Detail first, Detail[] details) {
        Detail[] merged = new Detail[details.length + 1];
        merged[0] = first;
        System.arraycopy(details, 0, merged, 1, details.length);
        return merged;
    }


    public static final int VALUE  = 0;
    public static final int SELECT = 1;
    public static final int WHERE  = 2;
    public static final int ORDER  = 3;
    public static final int LIMIT  = 4;


    public final int type;

    public Detail(int type) {
        this.type = type;
    }
}
