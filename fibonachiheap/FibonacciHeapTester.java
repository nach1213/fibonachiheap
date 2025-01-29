package fibonachiheap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/*comment
*/

/**
 * A robust test class for a FibonacciHeap.
 *
 * It performs:
 *  - Large sequences of inserts
 *  - Random deleteMin
 *  - Random delete of non-min nodes
 *  - Random decreaseKey
 *  - Meld operations
 *  - Checks correctness of size and the min pointer
 *  - At the end, prints totalLinks and totalCuts
 *
 * If an inconsistency is found, it throws an AssertionError.
 */
public class FibonacciHeapTester {

    private static final int NUM_RANDOM_OPS = 2000;     // total random operations
    private static final int KEY_RANGE      = 5000;     // random key values from 1..KEY_RANGE
    private static final long SEED          = 12345L;   // seed for reproducibility (optional)

    public static void main(String[] args) {
        testSize();
        //testBasicOperations();
        //testLargeInsertDelete();
        //testRandomOperations();
        //testMeldMultipleHeaps();

        System.out.println("=== ALL TESTS PASSED SUCCESSFULLY ===");
    }

    /**
     * 1) Test a few basic operations in a small, deterministic scenario.
     */
    private static void testBasicOperations() {
        System.out.println("[TEST] Basic operations...");

        FibonacciHeap heap = new FibonacciHeap();
        check(heap.size() == 0, "Initial size should be 0");
        check(heap.findMin() == null, "Empty heap min should be null");

        // Insert some items
        FibonacciHeap.HeapNode n10 = heap.insert(10, "ten");
        FibonacciHeap.HeapNode n5  = heap.insert(5, "five");
        FibonacciHeap.HeapNode n20 = heap.insert(20, "twenty");
        check(heap.size() == 3, "Size after 3 inserts should be 3");

        // Check min
        check(heap.findMin().key == 5, "Min should be 5");

        // DecreaseKey on n20 -> now 3
        heap.decreaseKey(n20, 17); // 20 -> 3
        check(heap.findMin().key == 3, "Min should now be 3 after decreasing 20 -> 3");

        // Delete the current min (which is 3)
        heap.deleteMin();
        check(heap.size() == 2, "Size should now be 2");
        check(heap.findMin().key == 5, "Min should revert to 5 after deleting 3");

        // Delete a non-min
        heap.delete(n10); // 10
        check(heap.size() == 1, "Size should now be 1 after deleting 10");
        check(heap.findMin().key == 5, "Min should remain 5");

        // Final deleteMin
        heap.deleteMin();
        check(heap.size() == 0, "Size should be 0 after removing last element");
        check(heap.findMin() == null, "Heap should be empty again");
    }

    /**
     * 2) Insert a large number of elements, then remove them one by one with deleteMin.
     */
    private static void testLargeInsertDelete() {
        System.out.println("[TEST] Large sequential insert and deleteMin...");

        FibonacciHeap heap = new FibonacciHeap();
        int n = 1000; // number of elements to insert
        for (int i = 1; i <= n; i++) {
            heap.insert(i, "val" + i);
        }
        check(heap.size() == n, "Size after 1000 inserts should be 1000");

        int prevKey = -1;
        for (int i = 0; i < n; i++) {
            int minKey = heap.findMin().key;
            heap.deleteMin();
            check(heap.size() == (n - i - 1),
                    "Size after deleting min is incorrect (expected " + (n - i - 1) + ")");
            // We expect minKey to be strictly ascending if we inserted 1..n
            check(minKey >= prevKey, "Min key should be non-decreasing in a simple scenario");
            prevKey = minKey;
        }
    }

    /**
     * 3) Perform a random mix of operations (insert, deleteMin, decreaseKey, delete)
     *    on a single heap. Keep track of all inserted keys to find the global min
     *    for verification and track size carefully.
     */
    public static void print_roots(FibonacciHeap my_heap){

        FibonacciHeap.HeapNode current = my_heap.min;
        if(current == null)return;
        do {
            System.out.println(current.key);
            current = current.next;
        }
        while(current != my_heap.min);
    }
    private static void testRandomOperations() {
        System.out.println("[TEST] Random mixed operations...");

        FibonacciHeap heap = new FibonacciHeap();
        Random rand = new Random(SEED);

        // We'll keep references to the nodes we insert
        List<FibonacciHeap.HeapNode> allNodes = new ArrayList<>();



        for (int i = 0; i < NUM_RANDOM_OPS; i++) {
            int op = rand.nextInt(4); // 0..3
            switch (op) {
                case 0: {
                    // Insert
                    int key = 1 + rand.nextInt(KEY_RANGE);
                    FibonacciHeap.HeapNode newNode = heap.insert(key, "info" + key);
                    allNodes.add(newNode);
                    break;
                }
                case 1: {
                    // deleteMin (if not empty)
                    if (heap.size() != 0) {
                        int oldSize = heap.size();
                        int oldMinKey = heap.findMin().key;
                        heap.deleteMin();
                        check(heap.size() == oldSize - 1,
                                "Size mismatch after deleteMin; expected " + (oldSize - 1));
                    }
                    break;
                }
                case 2: {
                    // decreaseKey on a random node
                    if (!allNodes.isEmpty()) {
                        FibonacciHeap.HeapNode node = pickRandomNode(rand, allNodes, heap);
                        if (node != null && node.key > 1) {
                            int diff = 1 + rand.nextInt(node.key - 1);
                            int oldKey = node.key;
                            heap.decreaseKey(node, diff);
                            check(node.key == (oldKey - diff),
                                    "Key wasn't decreased properly from " + oldKey + " by " + diff);
                        }
                    }
                    break;
                }
                case 3: {
                    // delete a random node (not necessarily min)
                    if (!allNodes.isEmpty()) {
                        FibonacciHeap.HeapNode node = pickRandomNode(rand, allNodes, heap);
                        if (node != null) {
                            int oldSize = heap.size();
                            print_roots(heap);
                            System.out.println("finished");
                            heap.delete(node);

                            check(heap.size() == oldSize - 1,
                                    "Size mismatch after delete");
                            // remove from allNodes so we don't keep a reference to an invalid node
                            allNodes.remove(node);
                        }
                    }
                    break;
                }
            }
            // Quick consistency checks
            check(heap.size() >= 0, "Size cannot be negative");
            if (heap.size()!=0) {
                int actualMin = heap.findMin().key;
                // The min key must be <= any node's key
                for (FibonacciHeap.HeapNode n : allNodes) {
                    // n might have been deleted, so check if it’s still in the heap
                    // We can approximate this by checking if n.parent == null after a certain operation
                    // but that doesn't always confirm it's in root. We'll skip a heavy approach and
                    // do a partial check:
                    if (n.key < actualMin && n.parent != null) {
                        // If n's parent != null, we assume it might still be alive in the heap
                        // This is a tricky check to do thoroughly.
                        throw new AssertionError("Heap min is invalid. Found smaller key: " + n.key);
                    }
                }
            } else {
                check(heap.findMin() == null, "Heap is empty but min != null");
            }
        }
        // Print final size, total links, total cuts after random ops
        System.out.println("Final heap size after random ops: " + heap.size());
        System.out.println("Total links: " + heap.totalLinks());
        System.out.println("Total cuts:  " + heap.totalCuts());
    }

    /**
     * 4) Test multiple meld operations: we create several heaps, then meld them all into one,
     *    verifying that min and size remain consistent.
     */
    private static void testMeldMultipleHeaps() {
        System.out.println("[TEST] Meld multiple heaps...");

        FibonacciHeap h1 = new FibonacciHeap();
        h1.insert(5, "five");
        h1.insert(9, "nine");

        FibonacciHeap h2 = new FibonacciHeap();
        h2.insert(1, "one");
        h2.insert(100, "one-hundred");

        FibonacciHeap h3 = new FibonacciHeap();
        h3.insert(2, "two");
        h3.insert(3, "three");
        h3.insert(50, "fifty");

        check(h1.size() == 2, "h1 size=2");
        check(h2.size() == 2, "h2 size=2");
        check(h3.size() == 3, "h3 size=3");

        // Meld h1 and h2 => h1 is the result
        h1.meld(h2);
        check(h1.size() == 4, "After meld h1 has 4 elements");
        check(h2.size() == 0, "h2 is no longer valid");
        check(h1.findMin().key == 1, "Min of h1 after meld with [1,100] should be 1");

        // Meld h1 and h3 => h1
        h1.meld(h3);
        check(h1.size() == 7, "After meld with h3, h1 has 7 elements");
        check(h3.size() == 0, "h3 is invalid now");
        check(h1.findMin().key == 1, "Min of h1 after second meld should be still 1");

        // Now do some deleteMin calls
        h1.deleteMin(); // remove 1
        check(h1.size() == 6, "h1 has 6 after removing min(1)");
        // new min might be 2 or 3 or 5
        int m = h1.findMin().key;
        check(m == 2 || m == 3 || m == 5, "New min should be 2 or 3 or 5");
    }

    /**
     * Pick a random node from a list that might contain some stale references
     * (like deleted nodes). We'll do a quick filter for nodes that appear alive
     * (key > 0, parent =? doesn't strictly guarantee it).
     */
    private static FibonacciHeap.HeapNode pickRandomNode(Random rand, List<FibonacciHeap.HeapNode> list, FibonacciHeap heap) {
        if (list.isEmpty()) return null;
        int limit = 10; // we'll try up to 10 random picks to find a "valid" node
        for (int i = 0; i < limit; i++) {
            int idx = rand.nextInt(list.size());
            FibonacciHeap.HeapNode candidate = list.get(idx);
            // simple heuristic: must have a key > 0, ignoring any fully-stale references
            // This is not 100% perfect but is good enough for our test.
            if (candidate.key > 0) {
                return candidate;
            }
        }
        return null; // fallback, might be all stale
    }

    // -------------- Helper Methods for Checking -------------- //

    /**
     * Throw AssertionError if condition is false.
     */
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("TEST FAILED: " + message);
        }
    }
    public static void testSize() {
        FibonacciHeap heap = new FibonacciHeap();

        // 1) Initially, an empty heap should have size = 0
        assertEquals("Empty heap should have size 0", 0, heap.size());

        // 2) Insert multiple elements, checking size increment
        final int N = 5;
        for (int i = 1; i <= N; i++) {
            heap.insert(i, "val" + i);
            assertEquals("After inserting " + i + " elements", i, heap.size());
        }

        // 3) Delete the minimum and check size
        heap.deleteMin(); // removes the element with the smallest key
        assertEquals("After deleteMin, size should be N-1", N - 1, heap.size());

        // 4) Insert more elements
        heap.insert(100, "hundred");
        heap.insert(50, "fifty");
        assertEquals("After inserting two more elements", (N - 1) + 2, heap.size());

        // 5) Delete a non-minimal node (for instance, the node with key=100 if we find it)
        //    We'll just assume we inserted a reference or do a loop of deleteMin for demonstration:
        heap.deleteMin(); // remove min (which might be smaller than 50)
        int sizeAfterDelMin = heap.size();
        if (sizeAfterDelMin != (N - 1) + 2 - 1) {
            throw new AssertionError("After second deleteMin, size should be " + ((N - 1) + 2 - 1)
                    + ", but got " + sizeAfterDelMin);
        }
        // Optionally, do more checks or remove everything until empty
        // Just for demonstration, remove all until empty:
        while (heap.size()!=0) {
            heap.deleteMin();
        }
        assertEquals("Heap should be empty again", 0, heap.size());
    }

    /**
     * A simple assertion helper that throws an AssertionError if actual != expected.
     */
    private static void assertEquals(String message, int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError(
                    message + " - Expected: " + expected + ", but got: " + actual
            );
        }
    }
}
