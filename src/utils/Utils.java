package utils;

import parser.*;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    private static Expression[] e_axioms = {
            Parser.parse("A->(B->A)"),
            Parser.parse("(A->B)->(A->B->C)->(A->C)"),
            Parser.parse("A->B->A&B"),
            Parser.parse("A&B->A"),
            Parser.parse("A&B->B"),
            Parser.parse("A->A|B"),
            Parser.parse("A->B|A"),
            Parser.parse("(A->B)->(C->B)->(A|C->B)"),
            Parser.parse("(A->B)->(A->!B)->!A"),
            Parser.parse("!!A->A")
    };
    public static final Comparator<Expression> ComparePropositionalVariable = new Comparator<Expression>() {
        @Override
        public int compare(Expression expression, Expression t1) {
            if (expression.getType() == TYPE.VARIABLE && t1.getType() == TYPE.VARIABLE) {
                return expression.toNormalForm().compareTo(t1.toNormalForm());
            }
            if (expression.getType() == TYPE.NEGATE && t1.getType() == TYPE.NEGATE) {
                return expression.get(true).toNormalForm().compareTo(t1.get(true).toNormalForm());
            }
            if (expression.getType() == TYPE.VARIABLE && t1.getType() == TYPE.NEGATE) {
                return expression.toNormalForm().compareTo(t1.get(true).toNormalForm());
            }
            return expression.get(true).toNormalForm().compareTo(t1.toNormalForm());
        }
    };

    public static Expression[] getE_axioms() {
        return e_axioms;
    }

    private static Map<Integer, Integer> exist = new HashMap<>();

    public static int isAxiom(Expression e) {
        int num = 1;
        for (Expression s : e_axioms) {
            exist.clear();
            if (recAx(s, e)) {
                return num;
            }
            num++;
        }
        return -1;
    }

    private static boolean recAx(Expression ax, Expression my) {
        if (ax.getType() == TYPE.VARIABLE) {
            int axe = ax.myHash();
            int mye = my.myHash();
            if (exist.containsKey(axe)) {
                return exist.get(axe).equals(mye);
            } else {
                exist.put(axe, mye);
                return true;
            }
        }
        if (ax.getType() == my.getType()) {
            if (ax.getType() == TYPE.NEGATE) {
                return recAx(ax.get(true), my.get(true));
            }
            if (ax.getType() == my.getType()) {
                return recAx(ax.get(false), my.get(false)) && recAx(ax.get(true), my.get(true));
            }
        }
        return false;
    }

    private static String suggestionStr = "";
    private static String lastS = "";
    private static List<String> arrayOfStr = new ArrayList<>();
    private static String header = "";
    private static Map<Integer, Expression> assumption = new HashMap<>();
    private static List<Expression> assumptionsForThirdHomeWork = new ArrayList<>();

    public static Map<Integer, Integer> parseHeader(String s) {
        header = s;
        Map<Integer, Integer> hassumptions = new HashMap<>();
        String[] splited = s.split("\\|-|\\|=");
        if (splited.length >= 2) {
            suggestionStr = splited[1];
        }
        if (splited[0].isEmpty()) {
            return Collections.emptyMap();
        }
        int num = 1;
        String[] v = splited[0].split(",");
        for (String j : v) {
            if (j.length() > 0) {
                arrayOfStr.add(j);
                lastS = j;
                Expression e = Parser.parse(j);
                assumption.put(e.myHash(), e);
                assumptionsForThirdHomeWork.add(e);
                hassumptions.put(e.myHash(), num);
                num++;
            }
        }
        return hassumptions;
    }

    public static Map<Integer, Expression> getAssumption() {
        return assumption;
    }


    public static Map<Integer, Integer> withoutLast(String s) {
        Map<Integer, Integer> a = parseHeader(s);
        a.remove(Parser.parse(lastS).myHash());
        return a;
    }

    public static String suggestionString() {
        return suggestionStr;
    }

    public static String lastString() {
        return lastS;
    }

    public static String getHeader() {
        return header;
    }

    public static String withoutLast() {
        arrayOfStr.remove(arrayOfStr.size() - 1);
        return arrayOfStr.stream().collect(Collectors.joining(",", "", ""));
    }

    public static Map<Integer, String> hashVar(Expression exp) {
        Map<Integer, String> hVar = new HashMap<>();
        Queue<Expression> q = new ArrayDeque<>();
        q.offer(exp);
        while (!q.isEmpty()) {
            Expression e = q.poll();
            if (e.getType() == TYPE.VARIABLE) {
                hVar.put(e.myHash(), e.toTree());
            } else if (e.getType() == TYPE.NEGATE) {
                q.offer(e.get(true));
            } else {
                q.offer(e.get(true));
                q.offer(e.get(false));
            }
        }
        return hVar;
    }

    public static Map<Integer, String> propositionalVariables(Map<Integer, Expression> assumptions) {
        Map<Integer, String> variables = new HashMap<>();
        for (Map.Entry<Integer, Expression> i : assumptions.entrySet()) {
            Map<Integer, String> currentVariables = hashVar(i.getValue());
            variables.putAll(currentVariables);
        }
        return variables;
    }

    public static boolean isTautology(Expression expression, Map<Integer,
            String> propositionalVariables, StringBuilder sb, List<Proof> listOfProof) {
        expression.myHash();
        final Map<Integer, Boolean> boolVariables = new HashMap<>();
        for (int i = 0; i < 1 << propositionalVariables.size(); i++) {
            int pos = 0;
            for (Integer j : propositionalVariables.keySet()) {
                boolVariables.put(j, (i & (1 << pos)) > 0);
                pos++;
            }
            List<Expression> currentEvaluation = new ArrayList<>();
            for (Map.Entry<Integer, Boolean> j : boolVariables.entrySet()) {
                if (j.getValue()) {
                    currentEvaluation.add(
                            new Variable(propositionalVariables.get(j.getKey()))
                    );
                } else {
                    currentEvaluation.add(
                            new Negate(
                                    new Variable(propositionalVariables.get(j.getKey()))
                            )
                    );
                }
            }
            listOfProof.add(new Proof(expression,
                    currentEvaluation, null));
            if (!expression.evaluate(boolVariables)) {
                sb.append("Высказывание ложно при ");
                String statement = propositionalVariables.entrySet().stream().
                        map(x -> x.getValue() + "=" + (boolVariables.get(x.getKey()) ? "И" : "Л")).
                        collect(Collectors.joining(","));
                sb.append(statement).append("\n");
                return false;
            }
        }
        return true;
    }


    public static void addToModusPonens(Expression e, Map<Integer, Pair<Integer, Integer>> modus,
                                        Map<Integer, Integer> firstPos) {
        if (e.getType() == TYPE.IMPLICATION) {
            int rg = e.get(false).myHash();
            int lf = e.get(true).myHash();

            if (modus.containsKey(rg)) {
                int lfop = modus.get(rg).getValue();
                if (firstPos.containsKey(lf) && lfop > firstPos.get(lf)) {
                    modus.put(rg, new Pair<>(firstPos.get(e.myHash()), firstPos.get(lf)));
                }
            } else {
                if (firstPos.containsKey(lf)) {
                    modus.put(rg, new Pair<>(firstPos.get(e.myHash()), firstPos.get(lf)));
                }
            }
        }
    }

    public static Expression buildUnionExpression(Expression e, Map<Integer, Expression> b) {
        if (b.isEmpty()) {
            return e;
        }
        String s = b.entrySet().stream().map(x -> x.getValue().toNormalForm()).
                collect(Collectors.joining(")->(", "(", ")")) + "->" + e.toNormalForm();
        return Parser.parse(s);
    }

    public static void BuildIfExistAssumption(Expression end, Map<Integer, Expression> assumptions,
                                              StringBuilder sb) {
        for (int i = 0; i <= assumptions.size(); i++) {
            sb.append(end.toNormalForm()).append("\n");
            end = end.get(false);
        }
    }

    public static void writeAssumptionsForThirdHomework(StringBuilder sb) {
        sb.append(assumptionsForThirdHomeWork.stream().map(Expression::toNormalForm)
                .collect(Collectors.joining(",", "", "")));
    }

    public static List<String> getPropositionalVariables(Expression exp) {
        Set<String> exists = new HashSet<>();
        Queue<Expression> q = new ArrayDeque<>();
        q.offer(exp);
        while (!q.isEmpty()) {
            Expression v = q.poll();
            if (v.getType() == TYPE.VARIABLE) {
                exists.add(v.toNormalForm());
            } else if (v.getType() == TYPE.NEGATE) {
                q.offer(v.get(true));
            } else {
                q.offer(v.get(false));
                q.offer(v.get(true));
            }
        }
        return Arrays.stream(exists.toArray()).map(Object::toString).collect(Collectors.toList());
    }


}

