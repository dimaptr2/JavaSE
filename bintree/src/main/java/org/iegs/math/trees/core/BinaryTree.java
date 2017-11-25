package org.iegs.math.trees.core;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree implements IBinaryTree {

    private Node root;
    private List<Integer> levels;
    private List<Node> nodes;

    public BinaryTree(Node root) {
        this.root = root;
        levels = new ArrayList<>();
        nodes = new ArrayList<>();
    }

    @Override
    public void addNode(int key, String name) {

    }

    @Override
    public int width(Node root, int level) {
        return 0;
    }

    @Override
    public int height(Node root, int level) {
        return 0;
    }

    @Override
    public int size(Node node) {
        return 0;
    }

    @Override
    public int maxWidthSize(Node root) {
        return 0;
    }

    @Override
    public int heightTree(Node root) {
        return 0;
    }

    @Override
    public void reverse(Node root) {

    }

}
