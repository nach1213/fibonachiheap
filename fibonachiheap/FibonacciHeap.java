/*
first student - nadav cherno
id - 328242284
username - cherno
first student -
id -
username -
 */

package fibonachiheap.fibonachiheap;

//
public class FibonacciHeap
{
    public HeapNode min; // Pointer to the minimum node
    public int size; // Number of nodes in the heap
    public int totalLinks; // Total number of links
    public int totalCuts; // Total number of cuts
    public int numOfTrees; // Total number of trees
    /**
     * Constructor to initialize an empty heap.
     * Time Complexity: O(1)
     */
    public FibonacciHeap() {
        this.min = null; // Initialize the minimum node pointer to null, indicating an empty heap.
        this.size = 0; // Set the size of the heap to 0, as there are no nodes initially.
        this.totalCuts = 0; // Initialize the total number of cuts to 0.
        this.totalLinks = 0; // Initialize the total number of links to 0.
        this.numOfTrees = 0; // Initialize the number of trees in the heap to 0.
    }

    /**
     * Inserts a new node with a given key and info into the heap.
     * Updates the minimum node if necessary.
     * Time Complexity: O(1)
     * @param key The key of the new node.
     * @param info Additional information for the node.
     * @return The newly created HeapNode.
     */
    public HeapNode insert(int key, String info)
    {
        HeapNode newNode = new HeapNode(key, info);
        numOfTrees++;
        size++;
        if (this.min == null) {
            this.min = newNode;
        }
        HeapNode minNext = min.next;
        this.min.next = newNode;
        newNode.prev = this.min;
        newNode.next = minNext;
        minNext.prev = newNode;
        if (this.min.key > newNode.key) {
            this.min = newNode;
        }
        return newNode;
    }

    /**
     *
     * Return the minimal HeapNode, null if empty.
     * Time Complexity: O(1)
     */
    public HeapNode findMin()
    {
        return this.min;
    }

    /**
     *
     * Delete the minimal item
     * Time Complexity: O(log n) (Amortized)
     */
    public void deleteMin() {
        // In case the heap is empty or will be emptied after we reset it
        if (size == 1 || size == 0) {
            min = null;
            size = 0;
            numOfTrees = 0;
            return;
        }
        if (min == null) {
            return;
        }
        // Height update for the number of children
        size--;
        numOfTrees += get_number_of_minimum_children() - 1;
        // Divided into cases
        // If he has children but is the only root
        if (min.next == min && min.child != null) {
            HeapNode cornet = min.child;
            HeapNode newMin = min.child;
            do {
                cornet.parent = null;
                totalCuts++;
                cornet.mark = false;
                cornet = cornet.next;
                if (newMin.key > cornet.key) {
                    newMin = cornet;
                }
            } while (cornet != min.child);
            this.min = newMin;
            return;
            //If he is with children and not the only root
        } else if (min.child != null) {
            HeapNode connectFromTheEnd = min.next;
            HeapNode connectFromTheStart = min.prev;
            connectFromTheEnd.prev = min.child.prev;
            min.child.prev.next = connectFromTheEnd;
            connectFromTheStart.next = min.child;
            min.child.prev = connectFromTheStart;
            //If he is without children but with other roots
        } else {
            min.next.prev = min.prev;
            min.prev.next = min.next;
        }
        //find the new min
        HeapNode cornet = min.next;
        HeapNode stop = min.next;
        HeapNode new_min = min.next;
        int log_n = (int) (Math.log(size) / Math.log(2) + 1);
        HeapNode[] lstOfTree = new HeapNode[size + 1];
        int i=0;
        do {
            lstOfTree[i] = cornet;
            cornet.mark = false;
            i++;
            cornet = cornet.next;
            if (cornet.key < new_min.key){
                new_min = cornet;
            }
        } while (cornet != stop);
        this.min = new_min;
        //Performing link according to the rank of the roots using a list
        //If there are two nodes that are up for being the min, what happens is that the first of them will be the min according to the function that searches for the min and according to the order in which we check it will be the first one according to which we build the tree (node1) and therefore it will always be the root.
        HeapNode[] lstBySize = new HeapNode[log_n];
        for (int j=0; j<size; j++) {
            HeapNode heapNode = lstOfTree[j];
            if (heapNode == null){
                continue;
            }
            HeapNode newHeapNode = heapNode;

            while (lstBySize[newHeapNode.rank] != null){
                newHeapNode = merge(newHeapNode,lstBySize[newHeapNode.rank]);
                lstBySize[newHeapNode.rank-1] = null;
            }
            lstBySize[newHeapNode.rank] = newHeapNode;
        }
        return;
    }
    //Checks the number of children of min Written to prevent errors
    public int get_number_of_minimum_children(){
        int count = 0;
        if (min.child == null){
            return 0;
        }
        HeapNode current = min.child;
        do {
            count++;
            current = current.next;
        }
        while(current != min.child);
        return count;
    }

    /**
     * The function that merges the trees receives two pointers to nodes and swaps them if it received them in the wrong order and connects the larger one to be the child of the smaller one while updating all relevant facts and reducing the tree by 1.
     * @param node1 - the small node(now how to deal even if he is the big one)
     * @param node2 - the big node
     * @return the link node
     * Time Complexity: O(1)
     */
    public HeapNode merge(HeapNode node1, HeapNode node2){
        //step 1 - Swapping them if the nodes arrived in the wrong order
        if(node1.key > node2.key){
            return merge(node2,node1);
        }
        //step 2 - Heap update
        numOfTrees--;
        totalLinks++;
        //step 3 - node2 update parent
        node2.parent = node1;
        //step 4 node1 -update
        node1.numOfChild++;
        node1.rank++;
        //step 5 - removing node2 from its current location
        node2.next.prev = node2.prev;
        node2.prev.next = node2.next;
        //step 6 - Placing it in its new location
        if (node1.child == null){
            node1.child = node2;
            node2.next = node2;
            node2.prev = node2;
        } else {
            HeapNode childNode = node1.child;
            childNode.next.prev = node2;
            node2.next = childNode.next;
            childNode.next = node2;
            node2.prev = childNode;
        }
        return node1;
    }
    /**
     *
     * pre: 0<diff<x.key
     * Decrease the key of x by diff and fix the heap.
     * Time Complexity: O(1) (Amortized)
     */
    public void decreaseKey(HeapNode x, int diff)
    {
        if (x == null){
            return;
        }
        if (min == null){
            x.key -= diff;
            return;
        }
        x.key -= diff;
        if (x.key < min.key){
            this.min = x;
        }
        if (x.parent == null) {
            return;
        }
        else if (x.key < x.parent.key) {
            rankAdjustment(x);
            numOfTrees++;
            if (x.parent.child == x && x.next == x) {
                x.parent.child = null;
                x.parent = null;
            }
            cut(x);
        }
        return;
    }

    /**
     *
     * Delete the x from the heap.
     * Time Complexity: O(1) (Amortized)
     */
    public void delete(HeapNode x)
    {
        if (min == null){
            return;
        }
        if (x == this.min) {
            deleteMin();
            return;
        }
        size--;
        int diff = x.key + Math.abs(min.key);
        x.key -= diff;
        if (x.parent == null) {
            numOfTrees--;
            if (x.child == null){
                x.next.prev = x.prev;
                x.prev.next = x.next;
                return;
            }
            min.next.prev = x.child.prev;
            x.child.prev.next = min.next;
            x.child.prev = min;
            min.next = x.child;
            x = null;
            return;
        }
        else if (x.key < x.parent.key) {
            rankAdjustment(x);
            if (x.parent.child == x && x.next == x) {
                x.parent.child = null;
                x.parent = null;
            }
            cut(x);
        }
    }


    /**
     *
     * Return the total number of links.
     * Time Complexity: O(1)
     */
    public int totalLinks()
    {
        return this.totalLinks;
    }


    /**
     *
     * Return the total number of cuts.
     * Time Complexity: O(1)
     */
    public int totalCuts()
    {
        return this.totalCuts;
    }


    /**
     *
     * Meld the heap with heap2
     * Time Complexity: O(1)
     */
    public void meld(FibonacciHeap heap2)
    {
        if (heap2.min == null || heap2.size == 0) {
            return;
        }
        if (this.min == null || this.size == 0) {
            this.min = heap2.min;
        } else {
            HeapNode minNext = this.min.next;
            heap2.min.prev.next = minNext;
            minNext.prev = heap2.min.prev.next;
            this.min.next = heap2.min;
            heap2.min.prev = this.min;
        }
        if (this.min.key > heap2.min.key) {
            this.min = heap2.min;
        }
        this.size += heap2.size;
        this.totalCuts += heap2.totalCuts;
        this.totalLinks += heap2.totalLinks;
        numOfTrees += heap2.numOfTrees;
        heap2.size = 0;
        return;
    }

    /**
     *
     * Return the number of elements in the heap
     * Time Complexity: O(1)
     */
    public int size()
    {
        return this.size;
    }


    /**
     *
     * Return the number of trees in the heap.
     * Time Complexity: O(1)
     */
    public int numTrees()
    {
        return numOfTrees;
    }

    /**
     *
     * cut the node and then check the parent and cut if mark if not mark
     * Time Complexity: O(1) (Amortized)
     */
    public void cut(HeapNode nodeToCut) {
        nodeToCut.mark = false;
        if (nodeToCut.parent == null) {
            return;
        }
        nodeToCut.parent.rank--;
        nodeToCut.numOfChild--;
        nodeToCut.next.prev = nodeToCut.prev;
        nodeToCut.prev.next = nodeToCut.next;
        nodeToCut.next = min.next;
        nodeToCut.next.prev = nodeToCut;
        min.next = nodeToCut;
        nodeToCut.prev = min;
        rankAdjustment(nodeToCut);
        if (nodeToCut.parent.mark) {
            cut(nodeToCut.parent);
        } else {
            nodeToCut.parent.mark = true;
        }
        nodeToCut.parent = null;
    }

    /**
     * adjusting the rank of the parents of the node before moving him
     * Time Complexity: O(1)
     */
    public void rankAdjustment(HeapNode heapNode){
        if (heapNode.parent == null) {
            return;
        }
        heapNode.parent.numOfChild--;
        heapNode.parent.rank = heapNode.parent.numOfChild;
    }

    /**
     * Class implementing a node in a Fibonacci Heap.
     *
     */
    public static class HeapNode{
        public int key;
        public String info;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public int rank;
        public boolean mark;
        public int numOfChild;

        public HeapNode(int key, String info) {
            this.key = key;
            this.info = info;
            this.child = null;
            this.next = this;
            this.prev = this;
            this.parent = null;
            this.rank = 0;
            this.mark = false;
            this.numOfChild = 0;
        }
    }
}