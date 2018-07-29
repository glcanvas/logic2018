package parser;

import java.util.Map;

/**
 * Created by Python on 27.03.2018.
 */
public class Variable implements Expression {
    private String v;

    public Variable(String var) {
        super();
        this.v = var;
    }

    public String toTree() {
        return v;
    }

    public String toNormalForm() {
        return v;
    }

    public Expression get(boolean x) {
        return null;
    }

    public TYPE getType() {
        return TYPE.VARIABLE;
    }

    private boolean isHash = false;
    private int hashCode = 0;

    public int myHash() {
        if (!isHash) {
            isHash = true;
            hashCode = v.hashCode();
        }
        return hashCode;
    }

    @Override
    public String toJavaCode() {
        return v;
    }

    @Override
    public boolean evaluate(Map<Integer, Boolean> propositionalVar) {
        //infa 146%
        return propositionalVar.get(myHash());
    }
}
