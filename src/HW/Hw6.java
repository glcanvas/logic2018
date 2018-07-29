package HW;

import parser.Expression;
import parser.Parser;
import utils.Utils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class Hw6 {
    private List<String> buildKripkeModel(BufferedReader reader,
                                          List<List<Integer>> tree, List<List<String>> forcedVariable)
            throws IOException {
        Set<String> variables = new HashSet<>();
        List<Integer> offsetVertex = new ArrayList<>();
        String input;

        int step = 0;
        while ((input = reader.readLine()) != null) {
            if (input.length() == 0) {
                continue;
            }
            int offset = getOffset(input);

            //find parent
            int parent = -1;
            int minOffset = Integer.MAX_VALUE;
            for (int i = step - 1; i >= 0; i--) {
                parent = i % 2 - 1;
                if (offsetVertex.get(i) + 1 == offset) {
                    parent = i;
                    break;
                }
                minOffset = min(offsetVertex.get(i), minOffset);
            }
            if (parent != -1) {
                tree.get(parent).add(step);
            }
            tree.add(new ArrayList<>());
            offsetVertex.add(offset);
            forcedVariable.add(getForcedVariable(input));
            variables.addAll(getForcedVariable(input));
            step++;
        }
        return new ArrayList<>(variables);
    }

    private int getOffset(String s) {
        int answer = 0;
        for (Character c : s.toCharArray()) {
            if (c.hashCode() == 9) {
                answer++;
            } else {
                break;
            }
        }
        return answer;
    }

    private List<String> getForcedVariable(String s) {
        StringBuilder sb = new StringBuilder();
        boolean second = false;
        for (Character c : s.toCharArray()) {
            if (c == '*') {
                second = true;
                continue;
            }
            if (second) {
                sb.append(c);
            }
        }
        String build = sb.toString().replace(',', ' ');
        String[] array = build.split("\\s+");
        return Arrays.asList(array).stream().filter(x -> x.length() > 0).collect(Collectors.toList());

    }

    private boolean isCorrectModel(int v, List<String> mustBeForced, List<Boolean> used,
                                   List<List<Integer>> tree, List<List<String>> forcedVariable) {
        for (String s : mustBeForced) {
            if (!forcedVariable.get(v).contains(s)) {
                return false;
            }
        }
        for (String s : forcedVariable.get(v)) {
            if (!mustBeForced.contains(s)) {
                mustBeForced.add(s);
            }
        }
        List<String> sendForced = new ArrayList<>();
        mustBeForced.forEach(x -> sendForced.add(x));
        mustBeForced.clear();
        boolean result = true;
        used.set(v, true);
        for (Integer i : tree.get(v)) {
            result &= isCorrectModel(i, sendForced, used, tree, forcedVariable);
        }
        return result;
    }

    public void main(BufferedReader reader, PrintWriter writer) throws IOException {
        String formula = reader.readLine();
        Expression expression = Parser.parse(formula);
        List<String> exprVars = Utils.getPropositionalVariables(expression);
        List<List<Integer>> tree = new ArrayList<>();
        List<List<String>> forcedVariable = new ArrayList<>();

        List<String> variables = buildKripkeModel(reader, tree, forcedVariable);

        for (String s : exprVars) {
            if (!variables.contains(s)) {
                variables.add(s);
            }
        }
        List<String> mustBeTrue = new ArrayList<>();
        List<Boolean> used = new ArrayList<>();
        for (int i = 0; i < tree.size(); i++) {
            used.add(false);
        }
        for (int i = 0; i < tree.size(); i++) {
            if (!used.get(i)) {
                mustBeTrue.clear();
                if (!isCorrectModel(0, mustBeTrue, used, tree, forcedVariable)) {
                    writer.write("Не модель Крипке");
                    return;
                }
            }
        }
        List<Map<Integer, Boolean>> evaluateVariable = new ArrayList<>();
        for (List<String> array : forcedVariable) {
            Map<Integer, Boolean> eval = new HashMap<>();
            for (String s : variables) {
                if (array.contains(s)) {
                    eval.put(s.hashCode(), true);
                } else {
                    eval.put(s.hashCode(), false);
                }
            }
            evaluateVariable.add(eval);
        }
        Hw5 hw5 = new Hw5();
        if (hw5.checkKripkeModel(tree, evaluateVariable, expression)) {
            writer.write("Не опровергает формулу");
            return;
        }
        List<Set<Integer>> topology = hw5.buildTopology(tree, evaluateVariable);
        boolean[][] graph = hw5.buildGraph(topology);
        String EvalVar = hw5.setVariables(topology, evaluateVariable, variables);
        hw5.printAnswer(graph, EvalVar, writer);

    }
}
