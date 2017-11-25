package org.iegs.math.trees.core;

public interface IBinaryTree {

    void addNode(int key, String name);
    int width(Node root, int level);
    int height(Node root, int level);
    int size(Node node);
    int maxWidthSize(Node root);
    int heightTree(Node root);
    void reverse(Node root);


}
