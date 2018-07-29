package utils;

import java.util.ArrayList;
import java.util.List;

public class AllTree {
    public List<List<List<Integer>>> trees = new ArrayList<>();

    public AllTree() {
    }

    public List<List<Integer>> getTrees(int v) {
        return trees.get(v);
    }

    public void addTrees(List<List<Integer>> list) {
        trees.add(list);
    }
}