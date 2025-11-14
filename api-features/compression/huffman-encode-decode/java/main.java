package com.example;

// TODO: Test all cases

public class Tree implements Comparable {

    interface BaseNode {
        boolean isLeaf();
        int weight();
    }
    
    class LeafNode implements BaseNode {
    
        private int count;
        private char label;
        
        LeafNode (int v, char c) { // v = count | c = char
            this.count = v;
            this.label = c;
        }
    
        int count() { return this.count; }
    
        void count(int v) { this.count = v; }
    
        int label() { return this.label; }
    
        void label(char c) { this.label = c; }
    
        boolean isLeaf() { return true; }
    }
    
    class TreeNode implements BaseNode {
    
        private int count;
        private BaseNode left_child;
        private BaseNode right_child;
        private int left_code = 0
        private int right_code = 1
        
        TreeNode (int v, BaseNode l, BaseNode r) {
            this.count = v;
            this.left_child = l;
            this.right_child = r;
        }
    
        int count() { return this.count; }
    
        void count(int v) { this.count = v; }
    
        BaseNode left_child() { return this.left_child; }
    
        void left_child(BaseNode l) { this.left_child = l; }
    
        BaseNode right_child() { return this.right_child; }
    
        void right_child(BaseNode r) { this.right_child = r; }
    
        boolean isLeaf() { return false; }
    
        int left_code() { return this.left_code; }
    
        int right_code() { return this.right_code; }
    }

    private BaseNode root;

    Tree (int v, char c) {
        this.root = new LeafNode(v, c);
    }

    Tree (int v, BaseNode l, BaseNode r) {
        this.root = new TreeNode(v, l, r);
    }

    BaseNode root() { return this.root; }

    int count() { return this.root.count(); }

    int compareTo(Object obj) {
        Tree that = (Tree) obj;
        if (this.root.count() < that.count()) return -1;
        else if (this.root.count() == that.count()) return 0;
        else return 1;
    }

    static Tree buildTree(minHeap) {
        Tree tmp1, tmp2, tmp3 = null;

        while (minHeap.heapsize() > 1) { // while 2 items left
            tmp1 = minHeap.removemin();
            tmp2 = minHeap.removemin();
            tmp3 = new Tree(tmp1.root(), tmp2.root(), tmp1.count() + tmp2.count());
            minHeap.insert(tmp3);
        }
        return tmp3;
    }

}


public class Main {


    public static void main(String[] args) {
        // TODO: Work with Tree.class
    }
}
