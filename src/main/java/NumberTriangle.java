import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * NumberTriangle: a tiny DAG-like triangle where some nodes can have two parents.
 *
 *               a
 *             b   c
 *           d   e   f
 *         h   i   j   k
 *
 * Build it with loadTriangle(fname) and read values with retrieve("lr...").
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

    public boolean isLeaf() { return right == null && left == null; }

    /**
     * Follow a path ('l' for left, 'r' for right) and return the value at the end.
     * Empty path returns the root.
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
     * Build a NumberTriangle from a text file, one row per line, numbers separated by spaces.
     * Tries classpath first (recommended), then common repo paths for CI fallbacks.
     */
    public static NumberTriangle loadTriangle(String fname) throws IOException {
    // 1) 先按“主/测试 classpath 资源”找
    InputStream in = NumberTriangle.class.getClassLoader().getResourceAsStream(fname);
    if (in == null) {
        in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fname);
    }

    // 2) 再按常见磁盘路径兜底（本地/CI 都通用）
    if (in == null) {
        java.nio.file.Path[] candidates = new java.nio.file.Path[]{
                java.nio.file.Paths.get("src", "main", "resources", fname),
                java.nio.file.Paths.get("src", "test", "resources", fname),
                java.nio.file.Paths.get(fname)
        };
        for (java.nio.file.Path p : candidates) {
            if (java.nio.file.Files.exists(p)) {
                in = java.nio.file.Files.newInputStream(p);
                break;
            }
        }
    }

    if (in == null) {
        throw new java.io.FileNotFoundException("Resource not found: " + fname +
                " (checked classpath and common disk locations)");
    }

    try (BufferedReader br = new BufferedReader(new InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8))) {
        NumberTriangle top = null;
        java.util.List<NumberTriangle> prev = null;

        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            java.util.List<NumberTriangle> curr = new java.util.ArrayList<>(parts.length);
            for (String s : parts) {
                curr.add(new NumberTriangle(Integer.parseInt(s)));
            }

            if (top == null) top = curr.get(0);

            if (prev != null) {
                for (int i = 0; i < prev.size(); i++) {
                    prev.get(i).setLeft(curr.get(i));
                    prev.get(i).setRight(curr.get(i + 1));
                }
            }
            prev = curr;
        }

        if (top == null) {
            throw new IOException("Resource '" + fname + "' is empty or unreadable.");
        }
        return top;
    }
}

    

    /** Try several ways to locate the resource so CI 不再 NPE */
    private static InputStream openResource(String fname) throws IOException {
        // 1) classpath via context ClassLoader
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = NumberTriangle.class.getClassLoader();
        if (cl != null) {
            InputStream in = cl.getResourceAsStream(fname);
            if (in != null) return in;
        }

        // 2) classpath via NumberTriangle.class (leading slash)
        URL url = NumberTriangle.class.getResource("/" + fname);
        if (url != null) return url.openStream();

        // 3) Fallback to repo paths (for GitHub Actions or running from project root)
        Path[] candidates = new Path[] {
                Paths.get(fname),
                Paths.get("src", "main", "resources", fname),
                Paths.get("src", "test", "resources", fname)
        };
        for (Path p : candidates) {
            if (Files.exists(p)) return Files.newInputStream(p);
        }

        throw new FileNotFoundException(
                "Could not find resource \"" + fname + "\" on classpath or in "
                        + candidates[0] + ", " + candidates[1] + ", " + candidates[2]
        );
    }

    public static void main(String[] args) throws IOException {
        NumberTriangle mt = NumberTriangle.loadTriangle("input_tree.txt");
        mt.maxSumPath();
        System.out.println(mt.getRoot());
    }
}
