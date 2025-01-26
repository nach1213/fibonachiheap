package fibonachiheap;

/**
 * FibonacciHeap
 * An implementation of Fibonacci heap over positive integers.
 *
 */
public class FibonacciHeap
{
	public HeapNode min; // Pointer to the minimum node
	public int size; // Number of nodes in the heap
	public int totalLinks; // Total number of links
	public int totalCuts; // Total number of cuts

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
	}

	/**
	 *
	 * pre: key > 0
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info)
	{
		return null; // should be replaced by student code
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
		return; // should be replaced by student code
	}

	/**
	 *
	 * Delete the x from the heap.
	 *
	 */
	public void delete(HeapNode x)
	{
		return; // should be replaced by student code
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
		return; // should be replaced by student code
	}

	/**
	 *
	 * Return the number of elements in the heap
	 *
	 */
	public int size()
	{
		return this.size; // should be replaced by student code
	}


	/**
	 *
	 * Return the number of trees in the heap.
	 *
	 */
	public int numTrees()
	{
		return (this.numInserts+this.totalCuts - this.totalLinks);
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

		public HeapNode(int key, String info) {
			this.key = key;
			this.info = info;
			this.child = null;
			this.next = this;
			this.prev = this;
			this.parent = null;
			this.rank = 0;
			this.mark = false;
		}
	}
}