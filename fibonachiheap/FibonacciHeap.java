package fibonachiheap;

public class FibonacciHeap
{
    public HeapNode min; // Pointer to the minimum node
    public int size; // Number of nodes in the heap
    public int totalLinks; // Total number of links
    public int totalCuts; // Total number of cuts
    public int numOfTrees; // Total number of trees

    /**
     *
     * Constructor to initialize an empty heap.
     *
     */
    public FibonacciHeap()
    {
        this.min = null;
        this.size = 0;
        this.totalCuts = 0;
        this.totalLinks = 0;
        this.numOfTrees = 0;
    }

    /**
     *
     * pre: key > 0
     * Insert (key,info) into the heap and return the newly generated HeapNode.
     *
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
     *
     */
    public HeapNode findMin()
    {
        return this.min;
    }

    /**
     *
     * Delete the minimal item
     *
     */

    public void deleteMin()
    {
        if (size == 1 || size == 0){
            min = null;
            size = 0;
            numOfTrees = 0;
            return;
        }
        if (min == null){
            return;
        }
        size--;
        numOfTrees += get_number_of_minimum_children()-1;
        if(get_number_of_minimum_children() == 0)System.out.println("get_number_of_minimum_children() == 0");
        if (min.next == min && min.child != null){
            HeapNode cornet = min.child;
            HeapNode newMin = min.child;
            do{
                cornet.parent = null;
                cornet = cornet.next;
                if (newMin.key > cornet.key){
                    newMin = cornet;
                }
            } while (cornet != min.child);
            this.min = newMin;
            return;
        }
        else if (min.child != null){
            min.next.prev = min.child;
            min.child.next = min.next;
            min.prev.next = min.child.prev;
            min.child.prev.prev = min.prev;
        } else {
            min.next.prev = min.prev;
            min.prev.next = min.next;
        }
        //find the new min
        HeapNode cornet = min.next;
        HeapNode stop = min.next;
        HeapNode new_min = min.next;
        int log_n = (int) (Math.log(size)/Math.log(2)+1);
        HeapNode[] lstOfTree = new HeapNode[size+1];
        if(numOfTrees==0)System.out.println("numOfTrees==0");
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
        HeapNode[] lstBySize = new HeapNode[log_n];
        for (int j=0; j<size; j++) {
            HeapNode heapNode = lstOfTree[j];
            if (heapNode == null){
                break;
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
    public HeapNode merge(HeapNode node1, HeapNode node2){
        //step 1
        if(node1.key > node2.key){
            return merge(node2,node1);
        }
        //step 2
        numOfTrees--;
        totalLinks++;
        //step 3
        node2.parent = node1;
        //step 4
        node1.numOfChild++;
        node1.rank++;
        //step 5
        node2.next.prev = node2.prev;
        node2.prev.next = node2.next;
        //step 6
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
     *
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
            if (x.parent.child == x && x.next == x) {
                x.parent.child = null;
                x.parent = null;
            }
            cut(x);
            numOfTrees++;
        }
        return;
    }

    /**
     *
     * Delete the x from the heap.
     *
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
     *
     */
    public int totalLinks()
    {
        return this.totalLinks;
    }


    /**
     *
     * Return the total number of cuts.
     *
     */
    public int totalCuts()
    {
        return this.totalCuts;
    }


    /**
     *
     * Meld the heap with heap2
     *
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
        this.size += Math.abs(heap2.size);
        this.totalCuts += heap2.totalCuts;
        this.totalLinks += heap2.totalLinks;
        numOfTrees += heap2.numOfTrees;
        heap2.size = 0;
        return;
    }

    /**
     *
     * Return the number of elements in the heap
     *
     */
    public int size()
    {
        return this.size;
    }


    /**
     *
     * Return the number of trees in the heap.
     *
     */
    public int numTrees()
    {
        return numOfTrees;
    }

    /**
     *
     * cut the node and then check the parent and cut if mark if not mark
     */
    public void cut(HeapNode nodeToCut) {
        nodeToCut.mark = false;
        if (nodeToCut.parent == null) {
            return;
        }
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