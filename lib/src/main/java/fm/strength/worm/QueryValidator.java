package fm.strength.worm;

import java.util.regex.Pattern;

import fm.strength.worm.util.Err;

public interface QueryValidator {

    public static final String ERR_INVALID_ALIAS = "invalid alias '%s'";
    public static final String ERR_INVALID_COLUMN = "invalid column '%s' in model %s";
    public static final String ERR_INVALID_VALUE = "invalid value '%s' for column '%s' in model %s";
    public static final String ERR_NULL_VALUE = "value cannot be null for column '%s' in model %s";

    public static final Pattern validAlias = Pattern.compile("[\\w\\d_]+");
    public static final Pattern validLiteral = Pattern.compile("'[\\w\\d_]+'");


    String checkAlias(String alias) throws IllegalArgumentException;
    String checkColumn(String column, int...ixs) throws IllegalArgumentException;


    public static class Default implements QueryValidator {

        @Override
        public String checkAlias(String alias) throws IllegalArgumentException {
            if(validAlias.matcher(alias).matches()) {
                return alias;
            }
            throw Err.get(ERR_INVALID_ALIAS, alias);
        }

        @Override
        public String checkColumn(String column, int...ixs) throws IllegalArgumentException {
            for(int ix : ixs) {
                Contracts.Contract contract = Contracts.getContract(ix);
                if(contract.columns.contains(column) || validLiteral.matcher(column).matches()) {
                    return column;
                }
            }
            throw Err.get(ERR_INVALID_COLUMN, column, ixs);
        }

    }

}
