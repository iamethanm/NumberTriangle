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
    // 先从类路径根部找（兼容 test/resources 与 main/resources）
    InputStream in = NumberTriangle.class.getResourceAsStream("/" + fname);
    if (in == null) {
        // 再试一次用上下文 ClassLoader（有些运行器用这个）
        in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fname);
    }
    if (in == null) {
        throw new FileNotFoundException("Resource not found on classpath: " + fname);
    }

    NumberTriangle top = null;
    java.util.List<NumberTriangle> prevRow = null;

    try (BufferedReader br = new BufferedReader(new InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8))) {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            java.util.List<NumberTriangle> currRow = new java.util.ArrayList<>(parts.length);
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
    }
    return top;
}


    public static void main(String[] args) throws IOException {
        NumberTriangle mt = NumberTriangle.loadTriangle("input_tree.txt");
        mt.maxSumPath();
        System.out.println(mt.getRoot());
    }
}
