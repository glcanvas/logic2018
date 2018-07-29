package HW;


import parser.Expression;
import parser.Parser;
import parser.TYPE;
import utils.FastScanner;
import utils.Pair;
import utils.Utils;
import java.io.PrintWriter;
import java.util.*;

class Hw1 {

    private static final List<Pair<String, Integer>> inputData = new ArrayList<>();
    private  Map<Integer, Integer> assumptions;
    private static final Map<Integer, Integer> firstPos = new HashMap<>();
    private static final Map<Integer, Pair<Integer, Integer>> modus = new HashMap<>();

    private final StringBuilder sb = new StringBuilder();

    void main(FastScanner in, PrintWriter out){
        String s;
        int pos = 1;
        assumptions = Utils.parseHeader(in.next());
        while ((s = in.next()) != null) {
            inputData.add(new Pair<>(s, pos));
            pos++;
        }
        addFirst();
        for (Pair<String, Integer> i : inputData) {
            parseString(i.getKey(), i.getValue());
        }
        out.write(sb.toString());
    }

    private void parseString(String s, int pos) {
        Expression obj = Parser.parse(s);
        int he_obj = obj.myHash();
        boolean fnd = false;
        if (assumptions.containsKey(he_obj)) {
            fnd = true;
            sb.append("(").append(pos).
                    append(") ").
                    append(s).
                    append(" (Предп. ").
                    append(assumptions.get(he_obj)).append(")\n");
        }
        if (!fnd) {
            int c = Utils.isAxiom(obj);
            if (c > 0) {
                fnd = true;
                sb.append("(").
                        append(pos).
                        append(") ").
                        append(s).
                        append(" (Сх. акс. ").
                        append(c).append(")\n");
            }
        }
        if (!fnd) {
            if (modus.containsKey(he_obj)) {
                Pair<Integer, Integer> p = modus.get(he_obj);
                int big = p.getKey();
                int little = p.getValue();
                if (big < pos && little < pos) {
                    fnd = true;
                    sb.append("(").
                            append(pos).
                            append(") ").
                            append(s).
                            append(" (M.P. ").
                            append(big).
                            append(", ").
                            append(little).append(")\n");
                }
            }
        }
        if (!fnd) {
            sb.append("(").
                    append(pos).
                    append(") ").
                    append(s).append(" (Не доказано)\n");
        }
        if (obj.getType() == TYPE.IMPLICATION) {
            int rg = obj.get(false).myHash();
            int lf = obj.get(true).myHash();
            if (modus.containsKey(rg)) {
                int lfop = modus.get(rg).getValue();
                if (firstPos.containsKey(lf) && lfop > firstPos.get(lf)) {
                    modus.put(rg, new Pair<>(firstPos.get(he_obj), firstPos.get(lf)));
                }
            } else {
                if (firstPos.containsKey(lf)) {
                    modus.put(rg, new Pair<>(firstPos.get(he_obj), firstPos.get(lf)));
                }
            }
        }
    }


    private void addFirst() {
        for (Pair<String, Integer> i : inputData) {
            int t = Parser.parse(i.getKey()).myHash();
            if (!firstPos.containsKey(t)) {
                firstPos.put(t, i.getValue());
            }
        }
    }

}
