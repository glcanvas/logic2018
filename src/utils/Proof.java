package utils;

import parser.Expression;

import java.util.List;
import java.util.Map;

public class Proof {

    public Expression expression;
    public List<Expression> assumptions;
    public List<Expression> expressionsList;

    public Proof(Expression expression, List<Expression> assumptions, List<Expression> expressionsList) {
        this.expression = expression;
        this.assumptions = assumptions;
        this.expressionsList = expressionsList;
    }
}
