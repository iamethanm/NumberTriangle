import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the provided NumberTriangle class to be used in this coding task.
 *
 * Note: This is like a tree, but some nodes in the structure have two parents.
 *
 *                  a
 *                b   c
 *              d   e   f
 *            h   i   j   k
 *
 * See NumberTriangleTest.java for a few basic test cases.
 */
public class NumberTriangle {

    private int root;

    private NumberTriangle left;
    private NumberTriangle right;

    public NumberTriangle(int root) {
        this.root = root;
    }

    public void setLeft(NumberTriangle left) { this.left = left; }
    public void setRight(NumberTriangle right) { this.right = right; }
    public int getRoot() { return root; }

    public void maxSumPath() {
        // [not for credit]
    }

    public boolean isLeaf() {
        return right == null && left == null;
    }

    /**
     * Follow path through this NumberTriangle structure ('l' = left; 'r' = right)
     * and return the root value at the end of the path. Empty string returns the root.
     */
    public int retrieve(String path) {
        NumberTriangle cur = this;
        if (path == null || path.isEmpty()) return cur.root;

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

    /**
     * Load a NumberTriangle from a text file bundled as a resource or present on disk.
     * Tries classpath first, then common filesystem fallbacks, and throws a clear error
     * if the file cannot be found.
     */
    public static NumberTriangle loadTriangle(String fname) throws IOException {
        InputStream in = null;

        // --- Try classpath (several ways) ---
        ClassLoader ctx = Thread.currentThread().getContextClassLoader();
        if (ctx != null) in = ctx.getResourceAsStream(fname);
        if (in == null) in = NumberTriangle.class.getClassLoader().getResourceAsStream(fname);
        if (in == null) in = NumberTriangle.class.getResourceAsStream("/" + fname);
        if (in == null) in = NumberTriangle.class.getResourceAsStream(fname);

        // --- Fallback to common filesystem locations (for CI quirks) ---
        if (in == null) {
            String[] candidates = {
                    fname,
                    "src/main/resources/" + fname,
                    "src/test/resources/" + fname,
                    "resources/" + fname
            };
            for (String p : candidates) {
                Path path = Paths.get(p);
                if (Files.exists(path)) {
                    in = Files.newInputStream(path);
                    break;
                }
            }
        }

        if (in == null) {
            throw new FileNotFoundException(
                    "Could not locate resource '" + fname + "' on classpath or filesystem.");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            NumberTriangle top = null;
            List<NumberTriangle> prevRow = null;

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                List<NumberTriangle> currRow = new ArrayList<>(parts.length);
                for (String s : parts) {
                    currRow.add(new NumberTriangle(Integer.parseInt(s)));
                }

                if (top == null) top = currRow.get(0);

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
