package parser;

/**
 * Created by Python on 27.03.2018.
 */
public abstract class AbstractExpression implements Expression {
    public Expression left;
    public Expression right;
    public TYPE cl;

    protected AbstractExpression(Expression l, Expression r, TYPE o) {
        this.left = l;
        this.right = r;
        this.cl = o;
    }

    public Expression get(boolean x) {
        return x ? left : right;
    }


    public TYPE getType() {
        return cl;
    }

    private boolean isHash = false;
    private int hashCode = 0;

    public int myHash() {
        if (!isHash) {
            isHash = true;
            hashCode = ("(" + cl.hashCode() + ", " + "("+left.myHash() + "), (" + right.myHash() + "))").hashCode();
        }
        return hashCode;
    }
}
