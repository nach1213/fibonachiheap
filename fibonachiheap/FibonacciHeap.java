package fibonachiheap.fibonachiheap;

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
        return; // should be replaced by student code

    }

    /**
     *
     * pre: 0<diff<x.key
     * Decrease the key of x by diff and fix the heap.
     *
     */
    public void decreaseKey(HeapNode x, int diff)
    {
        x.key -= diff;
        if (x.parent == null) {
            return;
        }
        else if (x.key < x.parent.key) {
            if (x.parent.child == x && x.next != x) {
                x.parent.child = x.prev;
                x.parent.rank = x.parent.child.rank+1;
            } else if (x.parent.child == x && x.next == x) {
                x.parent.rank = 0;
                x.parent.child = null;
                x.parent = null;
            }
            cut(x);
        }
        return; // should be replaced by student code
    }

    /**
     *
     * Delete the x from the heap.
     *
     */
    public void delete(HeapNode x)
    {
        if (x == this.min) {
            deleteMin();
            return;
        }
        if (x.child != null) {
            HeapNode minNext = this.min.next;
            HeapNode childPrev = x.child.prev;
            this.min.next = x.child;
            x.child.prev = this.min;
            childPrev.next = minNext;
            minNext.prev = childPrev;
        }
        if (x.parent != null && x.parent.child == x) {
            x.parent.child = x.prev;
            x.parent.rank = x.parent.child.rank+1;
        } else {
            this.numOfTrees--;
        }
        this.numOfTrees += x.numOfChild;
        x.next.prev = x.prev;
        x.prev.next = x.next;
        x = null;
        this.size--;
        return;
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
        this.size += heap2.size;
        this.totalCuts += heap2.totalCuts;
        this.totalLinks += heap2.totalLinks;
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
        if (nodeToCut.parent == null) {
            return;
        }
        nodeToCut.next.prev = nodeToCut.prev;
        nodeToCut.prev.next = nodeToCut.next;
        nodeToCut.next = min.next;
        nodeToCut.next.prev = nodeToCut;
        min.next = nodeToCut;
        nodeToCut.prev = min;
        if (nodeToCut.parent.child == nodeToCut) {
            nodeToCut.parent.child = nodeToCut.prev;
            nodeToCut.parent.rank = nodeToCut.child.rank+1;
        }
        if (nodeToCut.parent.mark) {
            cut(nodeToCut.parent);
        } else {
            nodeToCut.parent.mark = true;
        }
        nodeToCut.parent = null;
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

