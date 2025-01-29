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
        if (size == 1){
            min = null;
            size--;
            return;
        }
        if (min == null){
            return;
        }
        if (min.child != null){
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
        min = cornet;
        HeapNode[] lstOfTree = new HeapNode[numOfTrees];
        do {
            lstOfTree[lstOfTree.length - 1] = cornet;
            cornet = cornet.next;
            if (cornet.key < min.key){
                min = cornet;
            }
        } while (cornet != stop);
        HeapNode[] lstBySize = new HeapNode[(int) (Math.ceil(Math.log(size)/Math.log(2))+1)];
        for (HeapNode heapNode: lstOfTree) {
            if (heapNode == null){
                continue;
            }
            HeapNode newHeapNode = heapNode;
            while (lstBySize[newHeapNode.rank] != null){
                newHeapNode = merge(newHeapNode,lstBySize[newHeapNode.rank]);
                lstBySize[newHeapNode.rank - 1] = null;
            }
            lstBySize[newHeapNode.rank] = newHeapNode;
        }
        size--;
        return;
    }
	public HeapNode merge(HeapNode node1, HeapNode node2){
		if(node1.key > node2.key){
			return merge(node2,node1);
		}
		numOfTrees--;
		totalLinks++;
		node2.parent = node1;
		node1.rank++;

		if (node1.child == null)node1.child = node2;
		node2.next = node1.child;
		node2.prev = node1.child.prev;
		node1.child.prev.next = node2;
		node1.child.prev = node2;

		HeapNode start = node1.child, end = start.prev;

		node2.next = node1.child;
		node1.child.prev.next = node2;

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
     *
     */
    public boolean rankAdjustment(HeapNode heapNode){
        if (heapNode.parent == null) {
            return true;
        }
        heapNode.parent.numOfChild--;
        if (heapNode.parent.child == heapNode && heapNode.prev != heapNode) {
            heapNode.parent.child = heapNode.prev;
            heapNode.parent.rank = heapNode.child.rank + 1;
        } else if (heapNode.parent.child == heapNode && heapNode.prev == heapNode) {
            heapNode.parent.rank = 0;
        }
        return false;
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

