package fibonachiheap;

/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over positive integers.
 *1
 */
public class FibonacciHeap
{
	/**
	 * A pointer to the current minimum node in the heap.
	 * (Public per the skeleton requirement.)
	 */
	public HeapNode min;

	/** The total number of elements in the heap. */
	private int size;

	/** Static counters for links and cuts. */
	private static int linkCount = 0;
	private static int cutCount = 0;

	/**
	 *
	 * Constructor to initialize an empty heap.
	 *
	 */
	public FibonacciHeap()
	{
		// By default, min = null, size = 0, counters = 0 (static).
		this.min = null;
		this.size = 0;
	}

	/**
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info)
	{
		HeapNode node = new HeapNode(key, info);

		// If the heap is empty, new node becomes min
		if (this.min == null) {
			this.min = node;
		} else {
			// Insert node into the root list (circular list) next to min
			spliceIntoList(this.min, node);
			// Update min pointer if necessary
			if (key < this.min.key) {
				this.min = node;
			}
		}
		this.size++;
		return node;
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
		if (this.min == null) {
			return; // Heap is empty
		}
		HeapNode z = this.min;

		// Move each child of z into the root list ???
		if (z.child != null) {
			HeapNode c = z.child;
			do {
				HeapNode nextC = c.next;
				// Detach c from z and add to root list
				c.parent = null;
				spliceIntoList(this.min, c);
				c = nextC;
			} while (c != z.child);
		}

		// Remove z from the root list
		removeNodeFromList(z);
		this.size--;

		// If that was the only node, heap is now empty
		if (this.size == 0) {
			this.min = null;
		} else {
			// Arbitrarily set min to a root in the list, then consolidate ???
			this.min = z.next;
			consolidate();
		}
	}

	/**
	 *
	 *
	 * Decrease the key of x by diff and fix the heap.
	 *
	 */
	public void decreaseKey(HeapNode x, int diff)
	{
		if (x == null || diff <= 0) {
			return;
		}
		x.key -= diff;
		HeapNode y = x.parent;

		// If x now violates the heap property with respect to its parent
		if (y != null && x.key < y.key) {
			cut(x, y);
			cascadingCut(y);
		}

		// Possibly update global min ???
		if (this.min != null && x.key < this.min.key) {
			this.min = x;
		}
	}

	/**
	 *
	 * Delete the x from the heap.
	 *
	 */
	public void delete(HeapNode x)
	{
		if (x == null) {
			return;
		}
		// If x is already the min, standard deleteMin
		if (x == this.min) {
			deleteMin();
		} else {
			// Force x's key to become smaller than current min
			int diff = x.key - (this.min != null ? this.min.key - 1 : 1);
			decreaseKey(x, diff);

			// Now x should be the min or at least in the root list
			// Remove x from the root list directly, skipping consolidation
			removeNodeFromList(x);
			this.size--;

			// If x was forcibly the 'min' pointer, we may need to reassign min
			if (x == this.min) {
				if (this.size == 0) {
					this.min = null;
				} else {
					// Just pick an adjacent root. We do NOT do a full search for the actual min.
					this.min = x.next;
				}
			}
		}
	}

	/**
	 *
	 * Return the total number of links.
	 *
	 */
	public int totalLinks()
	{
		return linkCount;
	}

	/**
	 *
	 * Return the total number of cuts.
	 *
	 */
	public int totalCuts()
	{
		return cutCount;
	}

	/**
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2)
	{
		if (heap2 == null || heap2.min == null) {
			return; // Nothing to meld
		}
		if (this.min == null) {
			// Current heap is empty, just adopt heap2
			this.min = heap2.min;
			this.size = heap2.size;
		} else {
			// Splice the two root lists
			spliceLists(this.min, heap2.min);
			// Update min if needed
			if (heap2.min.key < this.min.key) {
				this.min = heap2.min;
			}
			this.size += heap2.size;
		}
		// Make heap2 unusable
		heap2.min = null;
		heap2.size = 0;
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
		if (this.min == null) {
			return 0;
		}
		int count = 0;
		HeapNode current = this.min;
		do {
			count++;
			current = current.next;
		} while (current != this.min);
		return count;
	}

	// --------------- Private Helper Methods --------------- //

	/**
	 * Consolidate the root list so that there is at most one root with any rank.
	 */
	private void consolidate() {
		int arraySize = (int)Math.floor(Math.log(this.size) / Math.log(2)) + 2;
		HeapNode[] rankTable = new HeapNode[arraySize];

		// Collect the current roots into an array to avoid concurrency issues
		HeapNode[] rootList = getRootListAsArray();

		for (HeapNode w : rootList) {
			HeapNode x = w;
			int d = x.rank;
			while (rankTable[d] != null) {
				HeapNode y = rankTable[d];
				if (y.key < x.key) {
					HeapNode temp = x;
					x = y;
					y = temp;
				}
				link(y, x);
				rankTable[d] = null;
				d++;
			}
			rankTable[d] = x;
		}

		// Rebuild the root list from rankTable
		this.min = null;
		for (HeapNode node : rankTable) {
			if (node != null) {
				if (this.min == null) {
					this.min = node;
					this.min.prev = this.min;
					this.min.next = this.min;
				} else {
					spliceIntoList(this.min, node);
					if (node.key < this.min.key) {
						this.min = node;
					}
				}
			}
		}
	}

	/**
	 * Link y to become a child of x (assuming x.key <= y.key).
	 * Remove y from root list, increment x.rank.
	 */
	private void link(HeapNode y, HeapNode x) {
		removeNodeFromList(y);
		y.parent = x;
		y.mark = false;

		if (x.child == null) {
			x.child = y;
			y.prev = y;
			y.next = y;
		} else {
			spliceIntoList(x.child, y);
		}

		x.rank++;
		linkCount++; // Increment our static link counter
	}

	/**
	 * Cut x from its parent y, add x as a root.
	 */
	private void cut(HeapNode x, HeapNode y) {
		// Remove x from y's child list
		if (x.next == x) {
			// x was the only child
			y.child = null;
		} else {
			if (y.child == x) {
				y.child = x.next;
			}
			removeNodeFromList(x);
		}
		y.rank--;
		x.parent = null;
		x.mark = false;

		// Add x to root list
		spliceIntoList(this.min, x);

		cutCount++; // Increment the cut counter
	}

	/**
	 * Cascading cut: if y is marked, cut it from its parent as well, recursively.
	 */
	private void cascadingCut(HeapNode y) {
		HeapNode z = y.parent;
		if (z != null) {
			if (!y.mark) {
				y.mark = true;
			} else {
				cut(y, z);
				cascadingCut(z);
			}
		}
	}

	/**
	 * Convert the current root list into an array of nodes for safe iteration.
	 */
	private HeapNode[] getRootListAsArray() {
		if (this.min == null) {
			return new HeapNode[0];
		}
		int n = numTrees();
		HeapNode[] result = new HeapNode[n];
		HeapNode current = this.min;
		for (int i = 0; i < n; i++) {
			result[i] = current;
			current = current.next;
		}
		return result;
	}

	/**
	 * Inserts 'nodeToAdd' into the circular list headed by 'listHead'.
	 * Both remain part of the same circular list after splicing.
	 */
	private static void spliceIntoList(HeapNode listHead, HeapNode nodeToAdd) {
		if (listHead == null || nodeToAdd == null) {
			return;
		}
		// listHead <-> nodeToAdd <-> listHead.next
		HeapNode listHeadNext = listHead.next;
		listHead.next = nodeToAdd;
		nodeToAdd.prev = listHead;
		nodeToAdd.next = listHeadNext;
		listHeadNext.prev = nodeToAdd;
	}

	/**
	 * Merges two circular lists: one headed by 'a', one by 'b'.
	 * After merging, they form one circular list containing all nodes from both.
	 */
	private static void spliceLists(HeapNode a, HeapNode b) {
		if (a == null || b == null) {
			return;
		}
		// "A next" <-> "B" and "B next" <-> "A"
		HeapNode aNext = a.next;
		HeapNode bNext = b.next;

		a.next = b;
		b.prev = a;
		bNext.prev = aNext.prev;
		aNext.prev.next = bNext;
		b.next = aNext;
		aNext.prev = b;
	}

	/**
	 * Removes 'node' from its circular list. If node is alone in its list,
	 * the pointer to that list can become null (handled outside if needed).
	 */
	private static void removeNodeFromList(HeapNode node) {
		node.prev.next = node.next;
		node.next.prev = node.prev;
		// Make 'node' a singleton to avoid confusion
		node.next = node;
		node.prev = node;
	}

	/**
	 * Class implementing a node in a Fibonacci Heap.
	 */
	public static class HeapNode {
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
			this.next = this; //???
			this.prev = this; //???
			this.rank = 0;
			this.mark = false;
			this.parent = null;
			this.child = null;
		}
	}
}
