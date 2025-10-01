package NumberTriangle;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the provided NumberTriangle class to be used in this coding task.
 *
 * Note: This is like a tree, but some nodes in the structure have two parents.
 *
 * The structure is shown below. Observe that the parents of e are b and c, whereas
 * d and f each only have one parent. Each row is complete and will never be missing
 * a node. So each row has one more NumberTriangle object than the row above it.
 *
 *                  a
 *                b   c
 *              d   e   f
 *            h   i   j   k
 *
 * Also note that this data structure is minimally defined and is only intended to
 * be constructed using the loadTriangle method, which you will implement
 * in this file. We have not included any code to enforce the structure noted above,
 * and you don't have to write any either.
 *
 * See NumberTriangleTest.java for a few basic test cases.
 *
 * Extra: If you decide to solve the Project Euler problems (see main),
 *        feel free to add extra methods to this class. Just make sure that your
 *        code still compiles and runs so that we can run the tests on your code.
 *
 */
public class NumberTriangle {

    private int root;

    private NumberTriangle left;
    private NumberTriangle right;

    public NumberTriangle(int root) {
        this.root = root;
    }

    public void setLeft(NumberTriangle left) {
        this.left = left;
    }

    public void setRight(NumberTriangle right) {
        this.right = right;
    }

    public int getRoot() {
        return root;
    }

    /**
     * [not for credit]
     * Set the root of this NumberTriangle to be the max path sum
     * of this NumberTriangle, as defined in Project Euler problem 18.
     * After this method is called, this NumberTriangle should be a leaf.
     */
    public void maxSumPath() {
        // optional
    }

    public boolean isLeaf() {
        return right == null && left == null;
    }

    /**
     * Follow path through this NumberTriangle structure ('l' = left; 'r' = right) and
     * return the root value at the end of the path. An empty string will return
     * the root of the NumberTriangle.
     */
    public int retrieve(String path) {
        NumberTriangle cur = this;
        if (path == null || path.isEmpty()) {
            return cur.root;
        }
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == 'l') {
                cur = cur.left;
            } else if (c == 'r') {
                cur = cur.right;
            } else {
                throw new IllegalArgumentException("Invalid path char: " + c);
            }
            if (cur == null) {
                throw new IllegalStateException("Path goes past a leaf at index " + i);
            }
        }
        return cur.root;
    }

    /* ===================== Resource helpers ===================== */

    /** Open a resource by trying classpath first, then common project-relative paths. */
    private static InputStream openResource(String fname) throws IOException {
        // 1) classpath: context loader
        ClassLoader ctx = Thread.currentThread().getContextClassLoader();
        if (ctx != null) {
            InputStream in = tryClasspath(ctx, fname);
            if (in != null) return in;
        }
        // 2) classpath: this class's loader
        ClassLoader here = NumberTriangle.class.getClassLoader();
        if (here != null) {
            InputStream in = tryClasspath(here, fname);
            if (in != null) return in;
        }
        // 3) filesystem fallbacks (MarkUs 不用 Maven，文件常在这些位置)
        String[] prefixes = new String[] {
                "", "./", "/", // 直接工作目录
                "src/test/resources/", "src/main/resources/",
                "resources/",
                // 有些同学项目根目录外面还包了一层目录名
                "NumberTriangle/src/test/resources/",
                "NumberTriangle/src/main/resources/",
                "NumberTriangle/resources/"
        };
        for (String p : prefixes) {
            File f = new File(p + fname);
            if (f.exists() && f.isFile()) {
                return new FileInputStream(f);
            }
        }
        throw new FileNotFoundException("Resource not found: " + fname);
    }

    private static InputStream tryClasspath(ClassLoader cl, String name) {
        InputStream in = cl.getResourceAsStream(name);
        if (in == null && name.startsWith("/")) {
            in = cl.getResourceAsStream(name.substring(1));
        }
        return in;
    }

    /** Read in the NumberTriangle structure from a file. */
    public static NumberTriangle loadTriangle(String fname) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(openResource(fname), StandardCharsets.UTF_8))) {

            NumberTriangle top = null;
            List<NumberTriangle> prevRow = null;

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                List<NumberTriangle> currRow = new ArrayList<>(parts.length);

                for (String s : parts) {
                    int v = Integer.parseInt(s);
                    currRow.add(new NumberTriangle(v));
                }

                if (top == null) {
                    top = currRow.get(0);
                }

                if (prevRow != null) {
                    for (int i = 0; i < prevRow.size(); i++) {
                        prevRow.get(i).setLeft(currRow.get(i));
                        prevRow.get(i).setRight(currRow.get(i + 1));
                    }
                }

                prevRow = currRow;
            }
            return top;
        }
    }

    public static void main(String[] args) throws IOException {
        NumberTriangle mt = NumberTriangle.loadTriangle("input_tree.txt");
        mt.maxSumPath();
        System.out.println(mt.getRoot());
    }
}
