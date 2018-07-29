package HW;

import parser.Expression;
import parser.Parser;
import parser.TYPE;
import utils.FastScanner;
import utils.Pair;
import utils.Utils;

import javax.rmi.CORBA.Util;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hw2 {
    private Map<Integer, Integer> assumptions;
    private static StringBuilder sb = new StringBuilder();
    private static final Map<Integer, Pair<Integer, Integer>> modus = new HashMap<>();
    private static final List<Pair<String, Integer>> inputData = new ArrayList<>();
    private static final Map<Integer, Integer> firstPos = new HashMap<>();

    public void main(FastScanner in, PrintWriter out) {
        String header = in.next();
        assumptions = Utils.withoutLast(header);
        String scd = Utils.suggestionString();
        String alpha = Utils.lastString();
        String withoutLast = Utils.withoutLast();
        Expression Ealpha = Parser.parse(alpha);
        String s;
        int pos = 1;
        sb.append(withoutLast + "|-(" + alpha + ")->(" + scd + ")" + "\n");
        while ((s = in.next()) != null) {
            inputData.add(new Pair<>(s, pos));
            int h = Parser.parse(s).myHash();
            if (!firstPos.containsKey(h)) {
                firstPos.put(h, pos);
            }
            pos++;
        }
        for (Pair<String, Integer> i : inputData) {
            s = i.getKey();
            Expression cur = Parser.parse(s);
            boolean proved = false;

            if (assumptions.containsKey(cur.myHash()) || Utils.isAxiom(cur) != -1) {
                sb.append(s).append("\n").
                        append("(" + "(" + s + ")" + ")->((" + alpha + ")->(" + s + "))\n").
                        append("(" + alpha + ")->(" + s + ")\n");
                proved = true;
            }
            if (!proved && Ealpha.myHash() == cur.myHash()) {
                String a = "(" + "(" + s + ")" + "->(" + "(" + s + ")" + "->" + "(" + s + ")" + "))\n";
                String b = "(" + "(" + s + ")" + "->((" + "(" + s + ")" + "->" + "(" + s + ")" + ")->" + "(" + s + ")" + "))\n";
                String c = "((" + "(" + s + ")" + "->(" + "(" + s + ")" + "->" + "(" + s + ")" + "))->((" + "(" + s + ")" + "->((" + "(" + s + ")" + "->" + "(" + s + ")" + ")->" + "(" + s + ")" + "))->(" + "(" + s + ")" + "->" + "(" + s + ")" + ")))\n";
                String d = "((" + "(" + s + ")" + "->((" + "(" + s + ")" + "->" + "(" + s + ")" + ")->" + "(" + s + ")" + "))->(" + "(" + s + ")" + "->" + "(" + s + ")" + "))\n";
                String e = "(" + "(" + s + ")" + "->" + "(" + s + ")" + ")\n";
                sb.append(a).append(b).append(c).append(d).append(e);
                proved = true;
            }
            if (!proved) {
                Pair<Integer, Integer> p = modus.get(cur.myHash());
                int big = p.getKey();
                int little = p.getValue();
                String ls = inputData.get(little - 1).getKey();
                String bg = inputData.get(big - 1).getKey();
                String a = "(" + "(" + alpha + ")" + "->" + "(" + ls + ")" + ")->(" + "(" + alpha + ")" + "->" + "(" + bg + ")" + ")->(" + "(" + alpha + ")" + "->" + "(" + s + ")" + ")\n";
                String b = "(" + "(" + alpha + ")" + "->" + "(" + bg + ")" + ")->(" + "(" + alpha + ")" + "->" + "(" + s + ")" + ")\n";
                String c = "(" + alpha + ")" + "->(" + s + ")\n";
                sb.append(a).append(b).append(c);
            }
            Utils.addToModusPonens(cur, modus, firstPos);

        }
        out.write(sb.toString());
    }


}
