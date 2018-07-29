package HW;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Hw4 {
    private static final int SIZE = 500;
    private static final int infinity = Integer.MAX_VALUE;
    private int n;

    private int[][] adding = new int[SIZE][SIZE];
    private int[][] multiply = new int[SIZE][SIZE];
    private int[][] implication = new int[SIZE][SIZE];

    private boolean[][] graph = new boolean[SIZE][SIZE];
    private boolean[][] invertGraph = new boolean[SIZE][SIZE];


    private static List<Integer> parseString(String c) {
        return Arrays.stream(c.split("\\s+")).map(Integer::parseInt).collect(Collectors.toList());
    }

    /*
     * get minimum in list using graph
     * */
    private static final BiFunction<boolean[][], List<Integer>, Integer> getMin = (x, y) -> {
        if (y.isEmpty()) {
            return infinity;
        }
        int current = y.get(0);
        for (Integer v : y) {
            if (x[v][current]) {
                current = v;
            }
        }
        return current;
    };

    private int applyFunction(int v, int u, boolean[][] graph, boolean[][] invertGraph,
                              BiFunction<boolean[][], List<Integer>, Integer> comparator) {
        List<Integer> unionVertex = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (graph[v][i] && graph[u][i]) {
                unionVertex.add(i);
            }
        }
        int result = comparator.apply(graph, unionVertex);
        for (Integer vertex : unionVertex) {
            if (!invertGraph[vertex][result]) {
                return infinity;
            }
        }
        return result;
    }


    private boolean isZero(int v, boolean[][] graph) {
        for (int i = 0; i < n; i++) {
            if (i == v) {
                continue;
            }
            if (graph[i][v]) {
                return false;
            }
        }
        return true;
    }

    private boolean isOne(int v, boolean[][] graph) {
        for (int i = 0; i < n; i++) {
            if (!graph[i][v]) {
                return false;
            }
        }
        return true;
    }

    private void writeResult(String s, PrintWriter out) {
        System.out.println(s);
        out.write(s);
    }

    public static int parserInput(BufferedReader in, boolean[][] graph, boolean[][] invertGraph) throws IOException {
        int GSize = Integer.parseInt(in.readLine());

        for (int i = 0; i < GSize; i++) {
            for (int j = 0; j < GSize; j++) {
                graph[i][j] = false;
                invertGraph[i][j] = false;
            }
        }
        for (int i = 0; i < GSize; i++) {
            graph[i][i] = true;
            invertGraph[i][i] = true;
        }
        for (int i = 0; i < GSize; i++) {
            for (int x : parseString(in.readLine())) {
                graph[i][x - 1] = true;
                invertGraph[x - 1][i] = true;
            }
        }
        for (int k = 0; k < GSize; k++) {
            for (int i = 0; i < GSize; i++) {
                for (int j = 0; j < GSize; j++) {
                    if (graph[i][k] && graph[k][j]) {
                        graph[i][j] = true;
                    }
                    if (invertGraph[i][k] && invertGraph[k][j]) {
                        invertGraph[i][j] = true;
                    }
                }
            }
        }
        return GSize;
    }

    public void main(BufferedReader in, PrintWriter out) throws IOException {
        n = parserInput(in, graph, invertGraph);


        //a+b<c
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int result = applyFunction(i, j, graph, invertGraph, getMin);
                if (result == infinity) {
                    writeResult("Операция '+' не определена: " + (i + 1) + "+" + (j + 1), out);
                    return;
                }
                adding[i][j] = result;
            }
        }

        //a*b>c
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int result = applyFunction(i, j, invertGraph, graph, getMin);
                if (result == infinity) {
                    writeResult("Операция '*' не определена: " + (i + 1) + "*" + (j + 1), out);
                    return;
                }
                multiply[i][j] = result;
            }
        }

        //i*(j+k) == i*j+i*k
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    int a = multiply[i][adding[j][k]];
                    int b = adding[multiply[i][j]][multiply[i][k]];
                    if (a != b) {
                        writeResult("Нарушается дистрибутивность: " + (i + 1) + "*(" + (j + 1) + "+" + (k + 1) + ")", out);
                        return;
                    }
                }
            }
        }

        //a->b = max(c | a*c < b)
        for (int a = 0; a < n; a++) {
            for (int b = 0; b < n; b++) {
                List<Integer> availableVertex = new ArrayList<>();
                for (int c = 0; c < n; c++) {
                    int mullAC = multiply[a][c];
                    //not true that a * c < b
                    if (!graph[mullAC][b]) {
                        continue;
                    }
                    availableVertex.add(c);
                }
                int implResult = getMin.apply(invertGraph, availableVertex);
                implication[a][b] = implResult;
            }
        }

        int zero = -1, one = -1;
        for (int i = 0; i < n; i++) {
            if (isOne(i, graph)) {
                one = i;
            }
            if (isZero(i, graph)) {
                zero = i;
            }
        }


        //                  a    b       one
        // a + (!a) = 1 <=> a + (a->0) = 1
        for (int a = 0; a < n; a++) {
            int b = implication[a][zero];
            int c = adding[a][b];
            if (c != one) {
                writeResult("Не булева алгебра: " + (a + 1) + "+~" + (a + 1), out);
                return;
            }
        }
        writeResult("Булева алгебра", out);
    }
}