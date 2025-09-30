import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * NumberTriangle - tree-like structure where some nodes have two parents.
 *
 *                 a
 *               b   c
 *             d   e   f
 *           h   i   j   k
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
    public boolean isLeaf() { return right == null && left == null; }

    /**
     * Follow 'l' (left) / 'r' (right) and return the value at the end.
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

    /**
     * Load a NumberTriangle from a resource/file (robust for CI & local).
     */
    public static NumberTriangle loadTriangle(String fname) throws IOException {
        try (BufferedReader br = openReader(fname)) {
            String line;
            NumberTriangle top = null;
            List<NumberTriangle> prevRow = null;

            while ((line = br.readLine()) != null) {
                line = stripBom(line).trim();
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

            if (top == null) {
                throw new IOException("Empty triangle file: " + fname);
            }
            return top;
        }
    }

    // ---------- helpers ----------

    /** Try classpath (多种方式) -> 若无，再尝试常见文件系统路径。 */
    private static BufferedReader openReader(String fname) throws IOException {
        InputStream in = null;

        // 1) Context ClassLoader
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) in = cl.getResourceAsStream(fname);

        // 2) 相对类路径（带 / 的绝对）
        if (in == null) in = NumberTriangle.class.getResourceAsStream("/" + fname);

        // 3) 仍然找不到，走文件系统的常见位置
        if (in == null) {
            String[] candidates = new String[]{
                fname,
                "src/test/resources/" + fname,
                "src/main/resources/" + fname,
                // 兼容你仓库层级里出现过的子目录名
                "NumberTriangle/src/test/resources/" + fname,
                "NumberTriangle/src/main/resources/" + fname
            };
            for (String p : candidates) {
                Path path = Paths.get(p);
                if (Files.exists(path)) {
                    return Files.newBufferedReader(path);
                }
            }
            throw new FileNotFoundException("Could not locate resource: " + fname);
        }

        return new BufferedReader(new InputStreamReader(in));
    }

    /** 去掉可能的 UTF-8 BOM，避免第一行第一个数字解析失败。 */
    private static String stripBom(String s) {
        return (!s.isEmpty() && s.charAt(0) == '\uFEFF') ? s.substring(1) : s;
    }

    // [not for credit]
    public void maxSumPath() {
        // optional
    }

    public static void main(String[] args) throws IOException {
        NumberTriangle mt = NumberTriangle.loadTriangle("input_tree.txt");
        mt.maxSumPath();
        System.out.println(mt.getRoot());
    }
}

