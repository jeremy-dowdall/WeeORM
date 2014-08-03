package fm.strength.worm.util;

import static fm.strength.worm.util.StringUtils.format;

public class Err {

    public static final String ERR_NO_CONTRACT = "%s does not specify a contract (see Data.Model.Contract)";
    public static final String ERR_NO_SUCH_FIELD = "%s must declare a public field '%s'";
    public static final String ERR_NO_SUCH_COLUMN = "no column name without a contract (see Data.Model.Contract)";
    public static final String ERR_ILLEGAL_ACCESS = "'%s' in %s must be public";
    public static final String ERR_NO_URI_WITHOUT_CONTRACT = "cannot get CONTENT_URI without a contract (see Data.Model.Contract)";
    public static final String ERR_NEED_CONTRACT_FOR_COLUMN = "cannot get column name without a contract (see Data.Model.Contract)";
    public static final String ERR_INCLUDE_HAS_NO_CONTRACT = "type of included field '%s' does not specifiy a contract";
    public static final String ERR_CANNOT_UPDATE_NEW_OBJECT = "cannot update a new object: %s";
    public static final String ERR_CANNOT_DESTROY_NEW_OBJECT = "cannot destroy a new object: %s";


    public static IllegalArgumentException get(String msg, Object...args) {
        return new IllegalArgumentException(format(msg, args));
    }

    public static IllegalArgumentException get(Throwable cause, String msg, Object...args) {
        return new IllegalArgumentException(format(msg, args), cause);
    }


    private Err() {
        // private constructor
    }

}
