package parser;

import java.util.Map;

/**
 * Created by Python on 27.03.2018.
 */
public class Negate implements Expression {
    private Expression left;

    public Negate(Expression e) {
        this.left = e;
    }


    public String toTree() {
        return String.format("(!%s)", left.toTree());
    }

    private String normal = null;

    public String toNormalForm() {
        if (normal == null) {
            normal = String.format("!(%s)", left.toNormalForm());
        }
        return normal;
    }

    public Expression get(boolean x) {
        return left;
    }

    public TYPE getType() {
        return TYPE.NEGATE;
    }

    private boolean isHash = false;
    private int hashCode = 0;

    public int myHash() {
        if (!isHash) {
            isHash = true;
            hashCode = ("(!" + left.myHash() + ")").hashCode();
        }
        return hashCode;
    }
    private String get(Expression e) {
        if(e.getType() == TYPE.VARIABLE) {
            return e.toNormalForm();
        }
        return e.toJavaCode();
    }
    @Override
    public String toJavaCode() {
        return "new Negate(" + get(left) + ")";
    }

    @Override
    public boolean evaluate(Map<Integer, Boolean> propVar) {
        return !left.evaluate(propVar);
    }
}
