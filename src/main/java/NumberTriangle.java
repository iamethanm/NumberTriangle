import java.io.*;

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
     *
     * Hint: think recursively and use the idea of partial tracing from first year :)
     *
     * Note: a NumberTriangle contains at least one value.
     */
    public void maxSumPath() {
        // for fun [not for credit]:
    }


    public boolean isLeaf() {
        return right == null && left == null;
    }


    /**
     * Follow path through this NumberTriangle structure ('l' = left; 'r' = right) and
     * return the root value at the end of the path. An empty string will return
     * the root of the NumberTriangle.
     *
     * You can decide if you want to use a recursive or an iterative approach in your solution.
     *
     * You can assume that:
     *      the length of path is less than the height of this NumberTriangle structure.
     *      each character in the string is either 'l' or 'r'
     *
     * @param path the path to follow through this NumberTriangle
     * @return the root value at the location indicated by path
     *
     */
    public int retrieve(String path) {
        // TODO implement this method
        return -1;
    }

    /** Read in the NumberTriangle structure from a file.
     *
     * You may assume that it is a valid format with a height of at least 1,
     * so there is at least one line with a number on it to start the file.
     *
     * See resources/input_tree.txt for an example NumberTriangle format.
     *
     * @param fname the file to load the NumberTriangle structure from
     * @return the topmost NumberTriangle object in the NumberTriangle structure read from the specified file
     * @throws IOException may naturally occur if an issue reading the file occurs
     */
    public static NumberTriangle loadTriangle(String fname) throws IOException {
    // 1) 先从 classpath 读（最标准的方式，覆盖 test/main 的 resources）
    InputStream in = NumberTriangle.class.getClassLoader().getResourceAsStream(fname);
    if (in == null) {
        in = NumberTriangle.class.getResourceAsStream("/" + fname);
    }

    // 2) 如果没读到，再尝试常见的源码路径（CI、本地都适用）
    if (in == null) {
        String[] candidates = new String[] {
                "src/test/resources/" + fname,
                "src/main/resources/" + fname,
                fname // 当前工作目录下
        };
        for (String p : candidates) {
            java.io.File f = new java.io.File(p);
            if (f.exists()) {
                in = new java.io.FileInputStream(f);
                break;
            }
        }
    }

    if (in == null) {
        throw new java.io.FileNotFoundException(
                "Could not find resource '" + fname + "' on classpath or filesystem.");
    }

    // 3) 按行读取并构建三角结构
    try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
        NumberTriangle top = null;
        java.util.List<NumberTriangle> prevRow = null;

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
        return top;
    }
}

