/**
 * A tester for the FibonacciHeap class.
 *
 * It runs a series of insert, findMin, deleteMin, decreaseKey, delete, meld, and so on,
 * printing out intermediate results.
 */


package fibonachiheap.fibonachiheap;


public class Main {
    public static void main(String[] args) {
        System.out.println("======== Fibonacci Heap Tester ========");

        /*
        // 1. Test empty heap behavior
        testEmptyHeap();

        // 2. Basic insert and findMin
        testBasicInsertAndFindMin();

        // 3. Multiple inserts, same keys, then deleteMin repeatedly
        testMultipleInsertsAndDeleteMin();

        // 4. DecreaseKey and cascading cut
        testDecreaseKeyCascadingCut();

        // 5. Delete (both min and non-min)
        testDelete();

        // 6. Meld two heaps
        testMeld();

        // 7. Check link/cut counters
        testCounters();
        */
        //part2.do_first_test();
        part2.do_second_test();

        /*System.out.println("======== All Tests Completed ========");
        for (int i=0 ; i < 2; i++){
            testBasicInsertAndFindMin();
            testDelete();
        }*/

    }
    public static void print_roots(FibonacciHeap my_heap){
        FibonacciHeap.HeapNode current = my_heap.min;
        do {
            System.out.println(current.key);
            current = current.next;
        }
        while(current != my_heap.min);
    }
    /**
     * Test 1: Verify behavior of an empty heap.
     */
    private static void testEmptyHeap() {
        System.out.println("--- Test #1: Empty Heap ---");
        FibonacciHeap emptyHeap = new FibonacciHeap();
        System.out.println("Heap created. size() = " + emptyHeap.size());
        System.out.println("findMin() = " + (emptyHeap.findMin() == null ? "null" : "ERROR!"));

        // Try deleteMin on empty
        emptyHeap.deleteMin(); // Should do nothing, no crash
        System.out.println("After deleteMin() on empty, size() = " + emptyHeap.size());

        // Try numTrees() on empty
        System.out.println("numTrees() on empty = " + emptyHeap.numTrees());
        System.out.println();
    }

    /**
     * Test 2: Insert a few elements and check the min pointer and size.
     */
    private static void testBasicInsertAndFindMin() {
        System.out.println("--- Test #2: Basic Insert and FindMin ---");
        FibonacciHeap heap = new FibonacciHeap();
        heap.insert(10, "Ten");
        heap.insert(5, "Five");

        
        heap.insert(7, "Seven");

        System.out.println("Expected size = 3, got size() = " + heap.size());
        System.out.println("Expected min.key = 5, got min.key = "
                + (heap.findMin() != null ? heap.findMin().key : "null"));

        System.out.println("numTrees() = " + heap.numTrees()
                + " (3 separate trees if no consolidation has occurred).");
        System.out.println();
        print_roots(heap);
    }

    /**
     * Test 3: Insert multiple elements (including duplicates), then call deleteMin repeatedly.
     */
    private static void testMultipleInsertsAndDeleteMin() {
        System.out.println("--- Test #3: Multiple Inserts and Repeated DeleteMin ---");
        FibonacciHeap heap = new FibonacciHeap();

        // Insert multiple items
        int[] keys = {4,   10, 2, 15, 1};
        for (int k : keys) {
            heap.insert(k, "Value"+k);
        }
        System.out.println("Inserted keys: 4,10,2,15,1");
        System.out.println("size() = " + heap.size() + ", expected = 6");
        System.out.println("findMin() = " + (heap.findMin() != null ? heap.findMin().key : "null")
                + " (expected 1)");

        // Delete min until heap is empty
        while (!isHeapEmpty(heap)) {
            int minKey = heap.findMin().key;

            System.out.println("numb of trees: " + heap.numTrees());
            heap.deleteMin();
            //print_roots(heap);
            System.out.println("deleteMin() removed " + minKey
                    + ", new min = " + (heap.findMin() != null ? heap.findMin().key : "null")
                    + ", size() = " + heap.size());
        }
        System.out.println("Heap is empty now.\n");
    }

    /**
     * Test 4: DecreaseKey and cascading cut scenario.
     */
    private static void testDecreaseKeyCascadingCut() {
        System.out.println("--- Test #4: DecreaseKey & Cascading Cut ---");
        FibonacciHeap heap = new FibonacciHeap();

        // Insert multiple keys, building up a small tree structure
        FibonacciHeap.HeapNode node10 = heap.insert(10, "Ten");
        FibonacciHeap.HeapNode node20 = heap.insert(20, "Twenty");
        FibonacciHeap.HeapNode node30 = heap.insert(30, "Thirty");
        FibonacciHeap.HeapNode node40 = heap.insert(40, "Forty");
        FibonacciHeap.HeapNode node5  = heap.insert(5, "Five");

        System.out.println("Inserted keys: 10, 20, 30, 40, 5");
        System.out.println("Min should be 5, actual min: "
                + (heap.findMin() != null ? heap.findMin().key : "null"));

        // Let's remove the min to force consolidation => fewer roots
        heap.deleteMin();
        System.out.println("After deleteMin(5), new min: "
                + (heap.findMin() != null ? heap.findMin().key : "null"));
        System.out.println("size() = " + heap.size());

        // Now, let's do decreaseKey on node40. Decrease it to 2 to trigger a cut
        int diff = node40.key - 2;
        heap.decreaseKey(node40, diff);
        System.out.println("After decreaseKey(40->2), new min.key = "
                + (heap.findMin() != null ? heap.findMin().key : "null")
                + " (expected 2)");
        System.out.println("size() still = " + heap.size());

        // Decrease node30 below node10 => multiple cascading cuts might happen
        diff = node30.key - 1;
        heap.decreaseKey(node30, diff);
        System.out.println("After decreaseKey(30->1), min.key = " + heap.findMin().key
                + " (expected 1)");

        System.out.println();
    }

    /**
     * Test 5: Delete operation (both deleting the current min and a non-min node).
     */
    private static void testDelete() {
        System.out.println("--- Test #5: Delete (min and non-min) ---");
        FibonacciHeap heap = new FibonacciHeap();

        // Insert some elements
        FibonacciHeap.HeapNode n1 = heap.insert(1, "One");
        FibonacciHeap.HeapNode n5 = heap.insert(5, "Five");
        FibonacciHeap.HeapNode n10 = heap.insert(10, "Ten");
        FibonacciHeap.HeapNode n2 = heap.insert(2, "Two");

        System.out.println("Inserted keys: 1,5,10,2, size() = " + heap.size() + " => expected 4");
        System.out.println("Min = " + heap.findMin().key + " (expected 1)");

        // Delete the min (which is 1)
        heap.delete(n1);
        System.out.println("Deleted node with key=1, new min = "
                + (heap.findMin() != null ? heap.findMin().key : "null")
                + ", size() = " + heap.size() + " => expected 3");

        // Delete a non-min node (say key=10)
        System.out.println("Deleting node with key=10 (non-min)...");
        heap.delete(n10);
        System.out.println("After delete(10), size() = " + heap.size()
                + " => expected 2");
        System.out.println("Min pointer might not be updated if 10 was forced lower. " +
                "But let's check: findMin() = "
                + (heap.findMin() != null ? heap.findMin().key : "null"));

        System.out.println();
    }

    /**
     * Test 6: Meld two heaps.
     */
    private static void testMeld() {
        System.out.println("--- Test #6: Meld ---");

        // First heap
        FibonacciHeap heap1 = new FibonacciHeap();
        heap1.insert(1, "One");
        heap1.insert(3, "Three");

        // Second heap
        FibonacciHeap heap2 = new FibonacciHeap();
        heap2.insert(2, "Two");
        heap2.insert(100, "Hundred");

        System.out.println("Heap1 min = " + heap1.findMin().key + ", size = " + heap1.size());
        System.out.println("Heap2 min = " + heap2.findMin().key + ", size = " + heap2.size());

        // Meld them
        heap1.meld(heap2);
        System.out.println("After meld, new heap size = " + heap1.size()
                + " => expected 4");
        System.out.println("Heap2 size = " + heap2.size()
                + " => expected 0 (no longer usable)");

        System.out.println("New heap min = " + heap1.findMin().key + " => expected 1 or 2");
        System.out.println();
    }

    /**
     * Test 7: Check the totalLinks and totalCuts counters
     * after performing various operations.
     *
     * Note that these counters are static for all heaps,
     * so each operation across all tests contributed.
     */
    private static void testCounters() {
        System.out.println("--- Test #7: totalLinks and totalCuts ---");
        FibonacciHeap heap = new FibonacciHeap();
        System.out.println("totalLinks() so far = " + heap.totalLinks());
        System.out.println("totalCuts() so far = " + heap.totalCuts());
        System.out.println("These values reflect all the linking/cutting done in previous tests.");
        System.out.println();
    }

    /**
     * Utility: checks whether a FibonacciHeap is empty by verifying if findMin() is null.
     * (Alternatively, you can call heap.size() == 0.)
     */
    private static boolean isHeapEmpty(FibonacciHeap heap) {
        return heap.findMin() == null;
    }
}
