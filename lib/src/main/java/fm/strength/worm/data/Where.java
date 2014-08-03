package fm.strength.worm.data;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import fm.strength.worm.sql.Expression;

import static fm.strength.worm.Data.where;

public class Where extends Detail {

    public Expression condition;

    public Where(Uri uri, String field) {
        super(Detail.WHERE);
        condition = new Expression(uri, field);
    }

    public Where isEqualTo(Object value) {
        if(value == null) return isNull();
        condition.$is = new Expression(value);
        return this;
    }
    public Where isEqualTo(Uri uri, String field) {
        condition.$is = new Expression(uri, field);
        return this;
    }
    public Where isNotEqualTo(Object value) {
        if(value == null) return isNotNull();
        condition.$ne = new Expression(value);
        return this;
    }
    public Where isNotEqualTo(Uri uri, String field) {
        condition.$ne = new Expression(uri, field);
        return this;
    }
    public Where isNull() {
        condition.$is = new Expression(Expression.NULL);
        return this;
    }
    public Where isNotNull() {
        condition.$not = new Expression(Expression.NULL);
        return this;
    }
    public Where isTrue() {
        condition.$is = new Expression(1);
        return this;
    }
    public Where isNotTrue() {
        condition.$ne = new Expression(1);
        return this;
    }
    public Where isFalse() {
        condition.$is = new Expression(0);
        return this;
    }
    public Where isNotFalse() {
        condition.$ne = new Expression(0);
        return this;
    }
    public Where isIn(Collection<?> values) {
        condition.$in = new ArrayList<Object>(values);
        return this;
    }
    public Where isIn(Object... values) {
        condition.$in = Arrays.asList(values);
        return this;
    }
    public Where isNotIn(Collection<?> values) {
        condition.$nin = new ArrayList<Object>(values);
        return this;
    }
    public Where isNotIn(Object... values) {
        condition.$nin = Arrays.asList(values);
        return this;
    }
    public Where isBlank() {
        return isNull().or(where(condition.uri, condition.field).isEqualTo(""));
    }
    public Where isNotBlank() {
        return isNotNull().and(where(condition.uri, condition.field).isNotEqualTo(""));
    }
    public Where isGreaterThan(Object value) {
        condition.$gt = new Expression(value);
        return this;
    }
    public Where isGreaterThan(Uri uri, String field) {
        condition.$gt = new Expression(uri, field);
        return this;
    }
    public Where isGreaterThanOrEqualTo(Object value) {
        condition.$gte = new Expression(value);
        return this;
    }
    public Where isGreaterThanOrEqualTo(Uri uri, String field) {
        condition.$gte = new Expression(uri, field);
        return this;
    }
    public Where isLessThan(Object value) {
        condition.$lt = new Expression(value);
        return this;
    }
    public Where isLessThan(Uri uri, String field) {
        condition.$lt = new Expression(uri, field);
        return this;
    }
    public Where isLessThanOrEqualTo(Object value) {
        condition.$lte = new Expression(value);
        return this;
    }
    public Where isLessThanOrEqualTo(Uri uri, String field) {
        condition.$lte = new Expression(uri, field);
        return this;
    }
    public Where isLike(Object value) {
        condition.$like = new Expression(value);
        return this;
    }
    public Where isNotLike(Object value) {
        condition.$nlike = new Expression(value);
        return this;
    }
    public Where hasId(long id) {
        condition.field = "_id";
        condition.$is = new Expression(id);
        return this;
    }
    public Where and(Where...others) {
        Expression condition = new Expression();
        condition.$and = new ArrayList<Expression>();
        condition.$and.add(this.condition);
        for(Where other : others) {
            condition.$and.add(other.condition);
        }
        this.condition = condition;
        return this;
    }
    public Where or(Where...others) {
        Expression condition = new Expression();
        condition.$or = new ArrayList<Expression>();
        condition.$or.add(this.condition);
        for(Where other : others) {
            condition.$or.add(other.condition);
        }
        this.condition = condition;
        return this;
    }
    public Where asLowerCase() {
        Expression e = new Expression();
        e.$lower = condition;
        condition = e;
        return this;
    }
    public Where asUpperCase() {
        Expression e = new Expression();
        e.$upper = condition;
        condition = e;
        return this;
    }
}
