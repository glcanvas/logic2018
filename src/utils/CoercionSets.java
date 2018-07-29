package utils;


import java.util.*;


/*
 * 1 - propositional variabel vinuzdena
 * 0 - ------//------------- ne vinuzdena
 */
public class CoercionSets {
    public String variable;
    public List<List<Boolean>> coercionList;

    public CoercionSets(String s) {
        this.variable = s;
        coercionList = new ArrayList<>();
    }

    public void addCoercionVertexToTree(List<List<Integer>> tree, int displayToTree) {
        List<Boolean> coercion = new ArrayList<>();
        int treeSize = tree.size();
        for (int i = 0; i < treeSize; i++) {
            coercion.add(false);
        }
        for (int i = 0; i < treeSize; i++) {
            if ((displayToTree & (1 << i)) > 0) {
                addToChild(i, true, tree, coercion);
            }
        }
        coercionList.add(coercion);
    }

    private static void addToChild(int begin, boolean value, List<List<Integer>> tree, List<Boolean> coercionVertex) {
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(begin);
        while (!q.isEmpty()) {
            int v = q.poll();
            coercionVertex.set(v, value);
            for (int to : tree.get(v)) {
                q.offer(to);
            }
        }
    }
    /*
    private static boolean haveChildrenCoercionValue(int begin, List<List<Integer>> tree, List<Integer> coercionVertex) {
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(begin);
        while (!q.isEmpty()) {
            int v = q.poll();
            if (coercionVertex.get(v) == 1) {
                return true;
            }
            for (int to : tree.get(v)) {
                q.offer(to);
            }
        }
        return false;
    }*/
}
