package fm.strength.worm.data;

import android.net.Uri;

import fm.strength.worm.sql.Expression;

public class Select extends Detail {

    public Expression expression;

    public Select(Uri uri, String field) {
        super(Detail.SELECT);
        expression = new Expression();
        expression.uri = uri;
        expression.field = field;
    }

    public Select withAlias(String alias) {
        expression.$as = alias;
        return this;
    }

    public Select asCount() {
        Expression e = new Expression();
        e.$count = expression;
        expression = e;
        return this;
    }

    public Select asMax() {
        Expression e = new Expression();
        e.$max = expression;
        expression = e;
        return this;
    }

    public Select asMin() {
        Expression e = new Expression();
        e.$min = expression;
        expression = e;
        return this;
    }

    public Select asLowerCase() {
        Expression e = new Expression();
        e.$lower = expression;
        expression = e;
        return this;
    }

    public Select asUpperCase() {
        Expression e = new Expression();
        e.$upper = expression;
        expression = e;
        return this;
    }

}
