package parser;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Python on 27.03.2018.
 */
public class Binnary extends AbstractExpression {
    private String o;
    private String normal = null;

    public Binnary(Expression l, Expression r, TYPE p) {

        super(l, r, p);
        if (p == TYPE.CONJUCTION) {
            o = "&";
        } else if (p == TYPE.DISJUNCTION) {
            o = "|";
        } else if (p == TYPE.IMPLICATION) {
            o = "->";
        }
    }

    public String toTree() {
        return String.format("(%s,%s,%s)", o, left.toTree(), right.toTree());
    }

    public String toNormalForm() {
        if (normal == null) {
            normal = String.format("((%s)%s(%s))", left.toNormalForm(), o, right.toNormalForm());
        }
        return normal;
    }

    private String curType() {
        return (o.equals("&") ? "TYPE.CONJUCTION" : (o.equals("|") ? "TYPE.DISJUNCTION" : "TYPE.IMPLICATION"));
    }

    private String get(Expression e) {
        if(e.getType() == TYPE.VARIABLE) {
            return e.toNormalForm();
        }
        return e.toJavaCode();
    }
    public String toJavaCode() {
        return String.format("new Binnary(%s, %s, %s)", get(left), get(right), curType());
    }

    public boolean evaluate(Map<Integer, Boolean> propVar) {
        if (o.equals("&")) {
            return left.evaluate(propVar) & right.evaluate(propVar);
        } else if (o.equals("|")) {
            return left.evaluate(propVar) | right.evaluate(propVar);
        }
        return !(left.evaluate(propVar)) | right.evaluate(propVar);
    }
}
