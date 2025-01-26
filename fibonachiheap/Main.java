/**
 * A test class for the FibonacciHeap.
 * It runs several tests and throws an error if any test fails.
 */
package fibonachiheap;
public class Main {

    public static void main(String[] args) {
        testEmptyHeap();
        testSingleInsert();
        testMultipleInserts();
        testDeleteMin();
        testDecreaseKey();
        testDeleteNonMin();
        testMeld();
        testLinkCutCounters();

        System.out.println("All tests passed successfully!");
    }

    private static void testEmptyHeap() {
        FibonacciHeap heap = new FibonacciHeap();
        assertEquals("size of empty heap should be 0", 0, heap.size());
        assertNull("min of empty heap should be null", heap.findMin());
        assertEquals("numTrees of empty heap should be 0", 0, heap.numTrees());

        // deleteMin or decreaseKey on empty heap shouldn't crash
        heap.deleteMin();
        heap.decreaseKey(null, 1);
        heap.delete(null);

        // still empty
        assertEquals("still size 0 after invalid ops", 0, heap.size());
    }

    private static void testSingleInsert() {
        FibonacciHeap heap = new FibonacciHeap();
        heap.insert(42, "forty-two");

        assertEquals("size should be 1 after one insert", 1, heap.size());
        assertEquals("min key", 42, heap.findMin().key);
        assertEquals("numTrees after single insert", 1, heap.numTrees());
    }

    private static void testMultipleInserts() {
        FibonacciHeap heap = new FibonacciHeap();
        heap.insert(10, "ten");
        heap.insert(5, "five");
        heap.insert(30, "thirty");
        heap.insert(2, "two");

        assertEquals("size after multiple inserts", 4, heap.size());
        assertEquals("min key after multiple inserts", 2, heap.findMin().key);
        // We have 4 roots if we never performed a deleteMin
        assertEquals("numTrees after multiple inserts", 4, heap.numTrees());
    }

    private static void testDeleteMin() {
        FibonacciHeap heap = new FibonacciHeap();
        heap.insert(10, "ten");
        heap.insert(5, "five");
        heap.insert(30, "thirty");
        heap.insert(2, "two");

        // min is key=2
        heap.deleteMin(); // remove the node with key=2
        assertEquals("size after deleteMin", 3, heap.size());
        // new min must be the smallest among 10, 5, 30 => 5
        assertEquals("new min key should be 5", 5, heap.findMin().key);

        // second deleteMin
        heap.deleteMin(); // remove key=5
        assertEquals("size after second deleteMin", 2, heap.size());
        // next min is 10
        assertEquals("min after removing 5", 10, heap.findMin().key);
    }

    private static void testDecreaseKey() {
        FibonacciHeap heap = new FibonacciHeap();
        FibonacciHeap.HeapNode n10 = heap.insert(10, "ten");
        FibonacciHeap.HeapNode n20 = heap.insert(20, "twenty");
        FibonacciHeap.HeapNode n5  = heap.insert(5, "five");

        // min is 5
        assertEquals("initial min", 5, heap.findMin().key);

        // Decrease n20 by 16 => new key = 4, should become min
        heap.decreaseKey(n20, 16);
        assertEquals("after decreaseKey(20->4)", 4, n20.key);
        assertEquals("new min after decreasing 20->4", 4, heap.findMin().key);

        // Decrease n10 by 6 => new key=4 as well
        // Ties are possible, but min pointer might or might not change to n10
        heap.decreaseKey(n10, 6);
        assertEquals("n10 new key=4", 4, n10.key);
        // The min is still 4, but we don't know which node is min if tie
        assertEquals("heap min after tie", 4, heap.findMin().key);
    }

    private static void testDeleteNonMin() {
        FibonacciHeap heap = new FibonacciHeap();
        FibonacciHeap.HeapNode n7  = heap.insert(7, "seven");
        FibonacciHeap.HeapNode n3  = heap.insert(3, "three");
        FibonacciHeap.HeapNode n10 = heap.insert(10, "ten");

        // min is key=3
        // delete the node with key=7 (not the min)
        heap.delete(n7);

        assertEquals("size after delete(non-min)", 2, heap.size());
        // min should remain 3
        assertEquals("min should still be 3", 3, heap.findMin().key);

        // check that the node 7 is gone
        // if we do repeated deleteMin calls, we'll see what's left
        heap.deleteMin(); // removes 3
        assertEquals("size after removing min(3)", 1, heap.size());
        // the only node left is key=10
        assertEquals("remaining key after removing 3", 10, heap.findMin().key);
    }

    private static void testMeld() {
        FibonacciHeap h1 = new FibonacciHeap();
        h1.insert(1, "one");
        h1.insert(4, "four");

        FibonacciHeap h2 = new FibonacciHeap();
        h2.insert(2, "two");
        h2.insert(10, "ten");
        h2.insert(0, "zero");

        // Meld h1 + h2 => everything should go into h1, h2 is invalidated
        h1.meld(h2);

        assertEquals("size of h1 after meld", 5, h1.size());
        assertEquals("min of h1 after meld", 0, h1.findMin().key);

        // h2 is not usable anymore (by spec)
        assertEquals("size of h2 after meld should be 0", 0, h2.size());
        assertNull("min of h2 after meld", h2.findMin());
    }

    private static void testLinkCutCounters() {
        // We'll do some operations, then check totalLinks() and totalCuts().
        // We won't check exact values, but we want to ensure no negative or nonsense results
        // Otherwise just note the final counters after some ops.

        FibonacciHeap h = new FibonacciHeap();
        for (int i = 1; i <= 10; i++) {
            h.insert(i, "val" + i);
        }
        // This might do no links/cuts yet

        int linksBefore = h.totalLinks();
        int cutsBefore = h.totalCuts();

        // Now do some decreaseKeys (should cause cuts if parents exist)
        FibonacciHeap.HeapNode node9 = h.findMin().next; // not guaranteed to be key=9, but let's see
        // better approach: keep references to inserted nodes
        // but let's just do some random manipulations:

        h.deleteMin(); // triggers consolidation => some links likely
        h.deleteMin(); // triggers more consolidation => more links

        int linksAfter = h.totalLinks();
        int cutsAfter = h.totalCuts();

        // Check that linksAfter >= linksBefore and cutsAfter >= cutsBefore
        assertTrue("links should not decrease", linksAfter >= linksBefore);
        assertTrue("cuts should not decrease", cutsAfter >= cutsBefore);
    }

    // -------------- Helper Assertions -------------- //

    private static void assertEquals(String message, int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError(message + " (expected " + expected + ", got " + actual + ")");
        }
    }

    private static void assertEquals(String message, Object expected, Object actual) {
        if ((expected == null && actual != null)
                || (expected != null && !expected.equals(actual))) {
            throw new AssertionError(message + " (expected " + expected + ", got " + actual + ")");
        }
    }

    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertNull(String message, Object obj) {
        if (obj != null) {
            throw new AssertionError(message + " (expected null, got " + obj + ")");
        }
    }
}
