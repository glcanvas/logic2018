package HW;

import parser.Expression;
import parser.Parser;
import parser.TYPE;
import utils.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Hw5 {

    private String buildSequence(int v, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int value = v & (1 << i);
            sb.append(
                    (value > 0) ? '(' : ')'
            );
        }
        return sb.toString();
    }

    private boolean isCorrect(String s) {
        int p = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                p++;
            } else {
                p--;
                if (p < 0) {
                    return false;
                }
            }
        }
        return p == 0;
    }

    /*
     * esli ia hochu derevo s 5 varshinami to ai need sdelat n = 5 * 2
     * */
    private List<String> generateAllRightBracket(int n) {
        List<String> bracketString = new ArrayList<>();
        for (int i = 0; i < (1 << n); i++) {
            String currentString = buildSequence(i, n);
            if (isCorrect(currentString)) {
                bracketString.add(currentString);
            }
        }
        return bracketString;
    }

    /*
     * tyt ne nado ymnozat na 2
     *
     * */
    private List<List<Integer>> buildTreeFromString(String s, int n) {
        List<List<Integer>> tree = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            tree.add(new ArrayList<>());
        }
        int vertex = 0;
        int parent = 0;

        for (char c : s.toCharArray()) {
            if (c == '(') {
                tree.get(parent).add(++vertex);
                parent++;
            } else {
                parent--;
            }
        }
        return tree;
    }

    private void setVertexToChild(int start, String s, List<List<Integer>> tree, List<Set<String>> compelTree) {
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(start);
        while (!q.isEmpty()) {
            int v = q.poll();
            compelTree.get(v).add(s);
            for (int to : tree.get(v)) {
                q.offer(to);
            }
        }
    }

    private List<Set<String>> setCompelVariable(List<List<Integer>> tree, List<String> variables,
                                                int displayToVertex, int compelVariables) {
        List<Set<String>> compelTree = new ArrayList<>();
        for (int k = 0; k < tree.size(); k++) {
            compelTree.add(new HashSet<>());
        }
        for (int i = 0; i < tree.size(); i++) {
            for (int j = 0; j < variables.size(); j++) {
                if ((compelVariables & (1 << j)) == 0 || ((displayToVertex & (1 << i)) == 0)) {
                    continue;
                }
                setVertexToChild(i, variables.get(j), tree, compelTree);
            }
        }
        return compelTree;
    }


    private void printTree(List<List<Integer>> l) {
        System.out.println("tree: with " + l.size() + " vertex");
        for (int i = 0; i < l.size(); i++) {
            System.out.print("v " + (i + 1) + " : ");
            for (int j = 0; j < l.get(i).size(); j++) {
                System.out.print((l.get(i).get(j) + 1) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printAllWorlds(List<Map<Integer, Boolean>> l, CoercionSets[] coercion) {
        Map<Integer, String> mapped = new HashMap<>();
        for (CoercionSets c : coercion) {
            mapped.put(c.variable.hashCode(), c.variable);
        }

        for (int i = 0; i < l.size(); i++) {
            System.out.print("v " + (i + 1) + " : ");
            for (Map.Entry j : l.get(i).entrySet()) {
                System.out.print(mapped.get(j.getKey()) + " " + j.getValue() + " | ");
            }
            System.out.println();
        }
    }


    private void printAllWorlds(List<Map<Integer, Boolean>> l) {
        System.out.println("coercion with = " + l.size() + " vertex");
        for (int i = 0; i < l.size(); i++) {
            System.out.print("v " + (i + 1) + " : ");
            for (Map.Entry j : l.get(i).entrySet()) {
                System.out.print(j.getKey() + " " + j.getValue() + " | ");
            }
            System.out.println();
        }
    }

    /*
     * в каждом мире формула(хеш формулы) либо трю либо фолс
     * */
    private List<Map<Integer, Boolean>> mergeKripkeModels(CoercionSets[] variable, List<Boolean>... model) {
        List<Map<Integer, Boolean>> merged = new ArrayList<>();
        int vertexNum = model[0].size();
        for (int i = 0; i < vertexNum; i++) {
            merged.add(new HashMap<>());
            for (int w = 0; w < model.length; w++) {
                merged.get(i).put(variable[w].variable.hashCode(), model[w].get(i));
            }
        }
        return merged;
    }


    /*
     *
     * chto za ebola takaya
     * */
    private Pair<Boolean, List<Map<Integer, Boolean>>> calculateForTree(int varCount, CoercionSets[] availableVariants, List<List<Integer>> tree,
                                                                        Expression expression) {

        if (varCount == 1) {
            for (List<Boolean> model1 : availableVariants[0].coercionList) {
                List<Map<Integer, Boolean>> merged = mergeKripkeModels(availableVariants, model1);
                if (!checkKripkeModel(tree, merged, expression)) {
                    return new Pair<>(false, merged);
                }
            }
        } else if (varCount == 2) {
            for (List<Boolean> model1 : availableVariants[0].coercionList) {
                for (List<Boolean> model2 : availableVariants[1].coercionList) {
                    List<Map<Integer, Boolean>> merged = mergeKripkeModels(availableVariants, model1, model2);
                    if (!checkKripkeModel(tree, merged, expression)) {
                        return new Pair<>(false, merged);
                    }

                }
            }
        } else if (varCount == 3) {
            for (List<Boolean> model1 : availableVariants[0].coercionList) {
                for (List<Boolean> model2 : availableVariants[1].coercionList) {
                    for (List<Boolean> model3 : availableVariants[2].coercionList) {
                        List<Map<Integer, Boolean>> merged = mergeKripkeModels(availableVariants, model1, model2, model3);
                        if (!checkKripkeModel(tree, merged, expression)) {
                            return new Pair<>(false, merged);
                        }
                    }
                }
            }
        }
        return new Pair<>(true, null);

    }

    /*
     * переберем все миры модели, в каждом мире будем рекурсиво разбирать выражение
     * если a&b, то очевидно
     *   a|b тоже
     *   a->b то для всех детей этого мира должно быть верно \\-!a \/ \\-b
     *   !a то для всех детей этого мира должно быть верно \\-а \\- - эта хуйня должна быть перечеркнута
     *   в мапе будем обновлять формулы т.е. если рассмартиваем щас a->b то во всех мирах выше когда будем их чекать
     *   добавим либо hash(a->b)=1 если выполняется уловие выше либо hash(a->b)=0
     *   пока никак не буду обновлять, буду тупо чекать че да как
     * */
    public boolean checkKripkeModel(List<List<Integer>> tree, List<Map<Integer, Boolean>> EvaluateExpression,
                                    Expression expression) {
        for (int v = 0; v < tree.size(); v++) {
            if (!checkKripkeWorld(v, tree, EvaluateExpression, expression)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkKripkeWorld(int currentWorld, List<List<Integer>> tree,
                                     List<Map<Integer, Boolean>> EvaluateExpression, Expression ex) {
        if (ex.getType() == TYPE.VARIABLE) {
            if (EvaluateExpression.get(currentWorld).containsKey(ex.myHash())) {
                return EvaluateExpression.get(currentWorld).get(ex.myHash()); //вернуть 1 если вынуждена 0 - иначе ( но это хранится в мапе)
            }
            return false;
        }
        if (ex.getType() == TYPE.NEGATE) {
            Queue<Integer> q = new ArrayDeque<>();
            q.offer(currentWorld);
            boolean isChildTrue = false;
            while (!q.isEmpty()) {
                int v = q.poll();
                isChildTrue = isChildTrue | checkKripkeWorld(v, tree, EvaluateExpression, ex.get(true));
                for (int to : tree.get(v)) {
                    q.offer(to);
                }
            }
            return !isChildTrue;
        }
        if (ex.getType() == TYPE.CONJUCTION) {
            return checkKripkeWorld(currentWorld, tree, EvaluateExpression, ex.get(true)) &
                    checkKripkeWorld(currentWorld, tree, EvaluateExpression, ex.get(false));
        }
        if (ex.getType() == TYPE.DISJUNCTION) {
            return checkKripkeWorld(currentWorld, tree, EvaluateExpression, ex.get(true)) |
                    checkKripkeWorld(currentWorld, tree, EvaluateExpression, ex.get(false));
        }
        //here is implication
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(currentWorld);
        boolean isChildTrue = true;
        while (!q.isEmpty()) {
            int v = q.poll();
            isChildTrue = isChildTrue &
                    (!checkKripkeWorld(v, tree, EvaluateExpression, ex.get(true)) | checkKripkeWorld(v, tree, EvaluateExpression, ex.get(false)));
            for (int to : tree.get(v)) {
                q.offer(to);
            }
        }
        return isChildTrue;
    }

    private Set<Integer> getChildren(int s, List<List<Integer>> tree) {
        Set<Integer> include = new HashSet<>();
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(s);
        while (!q.isEmpty()) {
            int v = q.poll();
            include.add(v);
            for (int i : tree.get(v)) {
                q.offer(i);
            }
        }
        return include;
    }

    private boolean equalsTwoSet(Set<Integer> a, Set<Integer> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i : a) {
            if (!b.contains(i)) {
                return false;
            }
        }
        return true;
    }

    /*
     * 1 - a - subset b
     * 0 -озервайс
     * */
    private boolean isSubSet(Set<Integer> a, Set<Integer> b) {
        if (a.size() > b.size()) {
            return false;
        }
        for (int i : a) {
            if (!b.contains(i)) {
                return false;
            }
        }
        return true;
    }

    public List<Set<Integer>> buildTopology(List<List<Integer>> tree, List<Map<Integer, Boolean>> EvaluateVariable) {
        List<Set<Integer>> base = new ArrayList<>();//это ващет базис но я его щас дополню до топологии
        base.add(new HashSet<>());//ага, это пустое множество
        for (int i = 0; i < tree.size(); i++) {
            base.add(getChildren(i, tree));//ага тут добавится весь Х
        }
        List<Set<Integer>> topology1 = new ArrayList<>();
        for (int n = 0; n < (1 << base.size()); n++) {
            Set<Integer> union = new HashSet<>();
            for (int i = 0; i < base.size(); i++) {
                if ((n & (1 << i)) == 0) {
                    continue;
                }
                union.addAll(base.get(i));
            }
            topology1.add(union);
        }
        List<Set<Integer>> topology = new ArrayList<>();

        for (Set<Integer> aTopology1 : topology1) {
            boolean exist = false;
            for (Set<Integer> aTopology : topology) {
                if (equalsTwoSet(aTopology1, aTopology)) {
                    exist = true;
                }
            }
            if (!exist) {
                topology.add(aTopology1);
            }
        }
        return topology;
    }

    public boolean[][] buildGraph(List<Set<Integer>> topology) {
        boolean graph[][] = new boolean[topology.size()][topology.size()];

        for (int i = 0; i < topology.size(); i++) {
            for (int j = 0; j < topology.size(); j++) {
                if (isSubSet(topology.get(i), topology.get(j))) {
                    graph[i][j] = true;
                }
            }
        }
        return graph;
    }


    private boolean isOnlyTrueInVertex(Set<Integer> vertex,
                                       Set<Integer> worldWhereTrue) {
        for (int i : vertex) {
            if (!worldWhereTrue.contains(i)) {
                return false;
            }
        }
        return true;
    }

    public String setVariables(List<Set<Integer>> topology, List<Map<Integer, Boolean>> evaluateVariables,
                               List<String> variables) {
        StringBuilder result = new StringBuilder();
        int pos = 0;
        for (String s : variables) {
            int hashCode = s.hashCode();
            Set<Integer> trueWorld = new HashSet<>();
            for (int w = 0; w < evaluateVariables.size(); w++) {
                if (evaluateVariables.get(w).get(hashCode)) {
                    trueWorld.add(w);
                }
            }
            int maxTopology = -1;
            for (int i = 0; i < topology.size(); i++) {
                if (isOnlyTrueInVertex(topology.get(i), trueWorld)) {
                    maxTopology = maxTopology == -1 ? i : (topology.get(maxTopology).size() < topology.get(i).size() ? i : maxTopology);
                }
            }
            if (pos != 0) {
                result.append(",");
            }
            result.append(s).append("=").append((maxTopology + 1));
            pos++;
        }
        return result.toString();
    }

    public void printAnswer(boolean[][] graph, String vars, PrintWriter out) {
        out.write(Integer.toString(graph.length));
        out.write("\n");
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                if (graph[i][j]) {
                    out.write(Integer.toString(j + 1));
                    out.write(" ");
                }
            }
            out.write("\n");
        }
        out.write(vars);
    }

    public void main(FastScanner in, PrintWriter out) throws IOException {

        int n = 5;

        String stringExpression = in.next();

        Expression parseExpression = Parser.parse(stringExpression);
        List<String> variables = Utils.getPropositionalVariables(parseExpression);


        //2**(n*2) + (Catalan n'th number)*(check model)
        for (String s : generateAllRightBracket(n * 2)) {

            List<List<Integer>> tree = buildTreeFromString(s, n);
            CoercionSets availableVariants[] = new CoercionSets[variables.size()];

            for (int i = 0; i < variables.size(); i++) {
                availableVariants[i] = new CoercionSets(variables.get(i));
            }
            for (int i = 0; i < (1 << n); i++) {
                for (int j = 0; j < variables.size(); j++) {
                    availableVariants[j].addCoercionVertexToTree(tree, i);
                }
            }
            Pair<Boolean, List<Map<Integer, Boolean>>> mergedResult =
                    calculateForTree(variables.size(), availableVariants, tree, parseExpression);
            if (!mergedResult.getKey()) {
                List<Set<Integer>> topology = buildTopology(tree, mergedResult.getValue());
                boolean[][] graph = buildGraph(topology);
                String EvalVar = setVariables(topology, mergedResult.getValue(), variables);
                printAnswer(graph, EvalVar, out);
                return;
            }
        }
        out.write("Формула общезначима");
    }

}


