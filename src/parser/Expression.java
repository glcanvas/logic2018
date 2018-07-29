package parser;

import java.util.Map;

/**
 * Created by Python on 27.03.2018.
 */
public interface Expression {
    String toTree();

    Expression get(boolean x);

    TYPE getType();

    int myHash();

    boolean evaluate(Map<Integer, Boolean> propositionalVar);

    String toNormalForm();

    String toJavaCode();
}
