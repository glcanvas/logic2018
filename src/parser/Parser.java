package parser;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Python on 27.03.2018.
 */


public class Parser {
    private static int cursor = 0;
    private static List<String> tokens = new ArrayList<>();

    public static Expression parse(String string) {
        cursor = 0;
        tokens.clear();
        normalSplit(string);
        return parseImpl();
    }

    private static Expression parseImpl() {
        Expression r_a = parseDisj();
        while (cursor < tokens.size() && tokens.get(cursor).equals("->")) {
            cursor++;
            r_a = new Binnary(r_a, parseImpl(), TYPE.IMPLICATION);
        }
        return r_a;
    }

    private static Expression parseDisj() {
        Expression r_a = parseConj();
        while (cursor < tokens.size() && tokens.get(cursor).equals("|")) {
            cursor++;
            r_a = new Binnary(r_a, parseConj(), TYPE.DISJUNCTION);
        }
        return r_a;
    }

    private static Expression parseConj() {
        Expression r_a = parseUnary();
        while (cursor < tokens.size() && tokens.get(cursor).equals("&")) {
            cursor++;
            r_a = new Binnary(r_a, parseUnary(), TYPE.CONJUCTION);
        }
        return r_a;
    }

    private static Expression parseUnary() {
        if (tokens.get(cursor).equals("!")) {
            cursor++;
            return new Negate(parseUnary());
        } else if (tokens.get(cursor).equals("(")) {
            cursor++;
            Expression r_a = parseImpl();
            cursor++;
            return r_a;
        } else {
            Expression r_a = new Variable(tokens.get(cursor));
            cursor++;
            return r_a;
        }
    }

    private static void normalSplit(String s) {
        s += " ";
        for (int p1 = 0; p1 < s.length(); p1++) {
            char c0 = s.charAt(p1);
            if (Character.isLetterOrDigit(c0)) {
                for (int p2 = p1; p2 < s.length(); p2++) {
                    char c = s.charAt(p2);
                    if (!Character.isLetterOrDigit(c)) {
                        tokens.add(s.substring(p1, p2));
                        p1 = p2;
                        c0 = s.charAt(p1);
                        break;
                    }
                }
            }

            if (c0 == '-') {
                tokens.add("->");
                p1++;
            } else {
                tokens.add(Character.toString(c0));
            }
        }
    }

}
