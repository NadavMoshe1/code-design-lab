package test;

import java.util.concurrent.RecursiveTask;

public class ParMaxSearcher extends RecursiveTask<Integer> {

    private static final long serialVersionUID = 1L;

    private final BinTree tree;

    public ParMaxSearcher(BinTree tree) {
        this.tree = tree;
    }

    @Override
    protected Integer compute() {
        if (tree == null) {
            return Integer.MIN_VALUE;
        }

        BinTree left = tree.getLeft();
        BinTree right = tree.getRight();

        if (left == null && right == null) {
            return tree.get();
        }

        ParMaxSearcher leftTask = null;
        if (left != null) {
            leftTask = new ParMaxSearcher(left);
            leftTask.fork();
        }

        int rightMax = right == null
                ? Integer.MIN_VALUE
                : new ParMaxSearcher(right).compute();

        int leftMax = leftTask == null
                ? Integer.MIN_VALUE
                : leftTask.join();

        return Math.max(tree.get(), Math.max(leftMax, rightMax));
    }
}
