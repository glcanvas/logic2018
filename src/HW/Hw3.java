package HW;

import parser.*;
import utils.*;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Hw3 {
    private static final StringBuilder sb = new StringBuilder();
    private Expression suggestion, begin;
    private Map<Integer, String> propositionalVariables;
    private Map<Integer, Expression> assumptions;
    private List<Proof> listOfProof = new ArrayList<>();

    public void main(FastScanner in, PrintWriter out) {
        String input = in.next();
        Utils.parseHeader(input);
        assumptions = Utils.getAssumption();
        suggestion = Parser.parse(Utils.suggestionString());
        begin = suggestion;
        propositionalVariables = Utils.propositionalVariables(assumptions);
        propositionalVariables.putAll(Utils.hashVar(suggestion));
        suggestion = Utils.buildUnionExpression(suggestion, assumptions);
        if (!Utils.isTautology(suggestion, propositionalVariables, sb, listOfProof)) {
            out.write(sb.toString());
            return;
        }
        Utils.writeAssumptionsForThirdHomework(sb);
        sb.append("|-").append(begin.toNormalForm()).append("\n");

        for (Proof i : listOfProof) {
            i.expressionsList = new ArrayList<>();
            createProof(i, suggestion);
        }
        int loopa = ((1 << propositionalVariables.size()) - 1) * 2;
        for (int i = 0; i < loopa; i += 2) {
            union(listOfProof.get(i + 1), listOfProof.get(i));
            listOfProof.add(listOfProof.get(i + 1));
        }
        for (Expression e : listOfProof.get(listOfProof.size() - 1).expressionsList) {
            sb.append(e.toNormalForm()).append("\n");
        }
        for (Map.Entry<Integer, Expression> e : assumptions.entrySet()) {
            sb.append(e.getValue().toNormalForm()).append("\n");
        }
        Utils.BuildIfExistAssumption(suggestion, assumptions, sb);
        out.write(sb.toString());
    }

    private void union(Proof first, Proof second) {

        Expression P = first.assumptions.get(0);//.toNormalForm();
        Expression NP = second.assumptions.get(0);//.toNormalForm();
        Expression A = first.expression;//.toNormalForm();

        deduction(first);
        deduction(second);

        first.expressionsList.addAll(second.expressionsList);

        addAllProofs(Proofs.A_or_NotA(P, P), first.expressionsList);
        Expression e1 = new Binnary(new Binnary(P, A, TYPE.IMPLICATION), new Binnary(new Binnary(NP, A, TYPE.IMPLICATION), new Binnary(new Binnary(P, NP, TYPE.DISJUNCTION), A, TYPE.IMPLICATION), TYPE.IMPLICATION), TYPE.IMPLICATION);
        Expression e2 = new Binnary(new Binnary(NP, A, TYPE.IMPLICATION), new Binnary(new Binnary(P, NP, TYPE.DISJUNCTION), A, TYPE.IMPLICATION), TYPE.IMPLICATION);
        Expression e3 = new Binnary(new Binnary(P, NP, TYPE.DISJUNCTION), A, TYPE.IMPLICATION);
        first.expressionsList.add(e1);
        first.expressionsList.add(e2);
        first.expressionsList.add(e3);
        first.expressionsList.add(A);
    }

    private void deduction(Proof proof) {

        List<Expression> newExpression = new ArrayList<>();

        List<Pair<Expression, Integer>> inputData = new ArrayList<>();
        Map<Integer, Pair<Integer, Integer>> modus = new HashMap<>();
        Map<Integer, Integer> firstPos = new HashMap<>();


        int pos = 1;
        for (Expression e : proof.expressionsList) {
            inputData.add(new Pair<>(e, pos));
            if (!firstPos.containsKey(e.myHash())) {
                firstPos.put(e.myHash(), pos);
            }
            pos++;
        }

        for (Expression e : proof.expressionsList) {

            boolean ok = false;

            if (e.myHash() == proof.assumptions.get(0).myHash()) {
                addAllProofs(Proofs.A_to_A(e, e), newExpression);
                ok = true;
            }

            if (!ok && (Utils.isAxiom(e) != -1 || proof.assumptions.stream().anyMatch(x -> x.myHash() == e.myHash()))) {
                newExpression.add(e);
                newExpression.add(new Binnary(e, new Binnary(proof.assumptions.get(0), e, TYPE.IMPLICATION), TYPE.IMPLICATION));
                newExpression.add(new Binnary(proof.assumptions.get(0), e, TYPE.IMPLICATION));
                ok = true;
            }
            if (!ok) {
                Pair<Integer, Integer> p = modus.get(e.myHash());
                int big = p.getKey();
                int little = p.getValue();
                Expression ls = inputData.get(little - 1).getKey();//.toNormalForm();
                Expression bg = inputData.get(big - 1).getKey();//.toNormalForm();
                Expression a1 = new Binnary(new Binnary(proof.assumptions.get(0), ls, TYPE.IMPLICATION), new Binnary(new Binnary(proof.assumptions.get(0), bg, TYPE.IMPLICATION),
                        new Binnary(proof.assumptions.get(0), e, TYPE.IMPLICATION), TYPE.IMPLICATION), TYPE.IMPLICATION);
                Expression a2 = new Binnary(new Binnary(proof.assumptions.get(0), bg, TYPE.IMPLICATION),
                        new Binnary(proof.assumptions.get(0), e, TYPE.IMPLICATION), TYPE.IMPLICATION);
                Expression a3 =
                        new Binnary(proof.assumptions.get(0), e, TYPE.IMPLICATION);
                newExpression.add(a1);
                newExpression.add(a2);
                newExpression.add(a3);

            }
            Utils.addToModusPonens(e, modus, firstPos);
        }
        proof.assumptions.remove(0);
        proof.expressionsList = newExpression;
    }

    private boolean createProof(Proof currentProof, Expression current) {
        if (current.getType() == TYPE.VARIABLE) {
            for (Expression e : currentProof.assumptions) {
                if (e.myHash() == current.myHash()) {
                    currentProof.expressionsList.add(e);
                    return true;
                }
            }
            currentProof.expressionsList.add(new Negate(current));
            return false;

        }
        if (current.getType() == TYPE.NEGATE) {
            boolean a = createProof(currentProof, current.get(true));
            if (a) {
                addAllProofs(Proofs.A_to_NotA(current.get(true), current.get(true)), currentProof.expressionsList);
            }
            return !a;
        }
        boolean a = createProof(currentProof, current.get(true));
        boolean b = createProof(currentProof, current.get(false));
        if (current.getType() == TYPE.CONJUCTION) {//&
            if (a && b) {
                addAllProofs(Proofs.A_and_B(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            } else if (a && !b) {
                addAllProofs(Proofs.A_and_NotB(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            } else if (!a && b) {
                addAllProofs(Proofs.notA_and_B(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            } else {
                addAllProofs(Proofs.notA_and_NotB(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            }
        }
        if (current.getType() == TYPE.DISJUNCTION) {//|
            if (a && b) {
                addAllProofs(Proofs.A_or_B(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            } else if (a && !b) {
                addAllProofs(Proofs.A_or_B(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            } else if (!a && b) {
                addAllProofs(Proofs.notA_or_B(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            } else {
                addAllProofs(Proofs.notA_or_NotB(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            }
        }
        if (current.getType() == TYPE.IMPLICATION) {//->
            if (a && b) {
                addAllProofs(Proofs.A_to_B(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            } else if (a && !b) {
                addAllProofs(Proofs.A_to_NotB(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            } else if (!a && b) {
                addAllProofs(Proofs.notA_to_B(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            } else {
                addAllProofs(Proofs.notA_to_NotB(current.get(true), current.get(false)),
                        currentProof.expressionsList);
            }
        }
        return currentProof.expressionsList.get(currentProof.expressionsList.size() - 1).myHash() == current.myHash();
    }

    private void addAllProofs(Expression[] statements, List<Expression> arrayOfProof) {
        arrayOfProof.addAll(Arrays.asList(statements));

    }
}
