package HW;

import HW.*;
import utils.FastScanner;

import java.io.*;
import java.math.BigInteger;
import java.nio.Buffer;
import java.util.StringTokenizer;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (Exception ignored) {
        }
    }

    void run() throws IOException {
      FastScanner in = new FastScanner(new File("input.txt"));
        PrintWriter out = new PrintWriter(new File("output.txt"));
        Hw5 hw = new Hw5();
        hw.main(in, out);
        in.close();
        out.close();
    }


}
