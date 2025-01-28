package fibonachiheap.fibonachiheap;

import java.util.Random;

public class FibonacciHeapTest {

    private static FibonacciHeap heap;
    private static int expectedNumTrees; // משתנים גלובליים
    private static int expectedSize;

    public static void main(String[] args) {
        setUp();
        performTests();
    }

    // אתחול ההיפ והוספת 500 איברים אקראיים
    private static void setUp() {
        heap = new FibonacciHeap();
        Random rand = new Random();
        for (int i = 0; i < 500; i++) {
            heap.insert(rand.nextInt(1000), "Node " + i);
        }
        expectedNumTrees = 500;  // אתחול הערכים הגלובליים
        expectedSize = 500;
    }

    // פונקציה שמבצעת את כל הבדיקות
    private static void performTests() {
        // ביצוע הבדיקות תוך כדי עדכון משתנים
        testNumTreesInitiallyZero();
        testNumTreesAfterInsert();
        testNumTreesAfterDeleteMin();
        testDecreaseKey();
        testDecreaseKeyWithParentSmaller();
        testCut();
        testCutWithRankAdjustment();
        testRankAdjustmentAfterCut();
        testRankAdjustmentMultipleChildren();
        testDeleteMinOnEmptyHeap();
        testMeld();
        testSize();

        // ביצוע `deleteMin`
        heap.deleteMin();
        expectedNumTrees--;  // עדכון אחרי פעולת deleteMin
        expectedSize--;

        // בדיקות אחרי `deleteMin`
        testNumTreesInitiallyZero();
        testNumTreesAfterInsert();
        testNumTreesAfterDeleteMin();
        testDecreaseKey();
        testDecreaseKeyWithParentSmaller();
        testCut();
        testCutWithRankAdjustment();
        testRankAdjustmentAfterCut();
        testRankAdjustmentMultipleChildren();
        testDeleteMinOnEmptyHeap();
        testMeld();
        testSize();
    }

    // בדיקה אם numTrees נכון אחרי יצירת ההיפ
    public static void testNumTreesInitiallyZero() {
        if (heap.numTrees() == expectedNumTrees) {
            System.out.println("testNumTreesInitiallyZero: Passed");
        } else {
            System.out.println("testNumTreesInitiallyZero: Failed");
            System.out.println("Expected: " + expectedNumTrees + ", Found: " + heap.numTrees());
        }
    }

    // בדיקה אם numTrees נכון אחרי הוספת איבר
    public static void testNumTreesAfterInsert() {
        heap.insert(10, "New Node");
        // עדכון הציפיות לאחר הבדיקה
        if (heap.numTrees() == expectedNumTrees + 1 && heap.size() == expectedSize + 1) {
            System.out.println("testNumTreesAfterInsert: Passed");
            expectedNumTrees++;  // עדכון ציפיות
            expectedSize++;  // עדכון ציפיות
        } else {
            System.out.println("testNumTreesAfterInsert: Failed");
            System.out.println("Expected numTrees: " + (expectedNumTrees + 1) + ", Found: " + heap.numTrees());
            System.out.println("Expected size: " + (expectedSize + 1) + ", Found: " + heap.size());
        }
    }

    // בדיקה אם numTrees נכון אחרי מחיקת המינימום
    public static void testNumTreesAfterDeleteMin() {
        heap.deleteMin();
        // עדכון הציפיות אחרי מחיקת מינימום
        if (heap.numTrees() == expectedNumTrees - 1 && heap.size() == expectedSize - 1) {
            System.out.println("testNumTreesAfterDeleteMin: Passed");
            expectedNumTrees--;  // עדכון ציפיות
            expectedSize--;  // עדכון ציפיות
        } else {
            System.out.println("testNumTreesAfterDeleteMin: Failed");
            System.out.println("Expected numTrees: " + (expectedNumTrees - 1) + ", Found: " + heap.numTrees());
            System.out.println("Expected size: " + (expectedSize - 1) + ", Found: " + heap.size());
        }
    }

    // בדיקה אם decreaseKey עובד כראוי
    public static void testDecreaseKey() {
        FibonacciHeap.HeapNode node = heap.insert(20, "Node 2");
        heap.decreaseKey(node, 5);
        int expectedKey = 15;
        if (node.key == expectedKey && heap.findMin().key == expectedKey) {
            System.out.println("testDecreaseKey: Passed");
        } else {
            System.out.println("testDecreaseKey: Failed");
            System.out.println("Expected key: " + expectedKey + ", Found key: " + node.key);
            System.out.println("Expected min key: " + expectedKey + ", Found min key: " + heap.findMin().key);
        }
    }

    // בדיקה אם decreaseKey עובד כאשר ההורה קטן יותר
    public static void testDecreaseKeyWithParentSmaller() {
        FibonacciHeap.HeapNode parentNode = heap.insert(30, "Parent Node");
        FibonacciHeap.HeapNode childNode = heap.insert(40, "Child Node");
        childNode.parent = parentNode;
        heap.decreaseKey(childNode, 10);
        int expectedKey = 30;
        if (childNode.key == expectedKey) {
            System.out.println("testDecreaseKeyWithParentSmaller: Passed");
        } else {
            System.out.println("testDecreaseKeyWithParentSmaller: Failed");
            System.out.println("Expected key: " + expectedKey + ", Found key: " + childNode.key);
        }
    }

    // בדיקת פעולת cut
    public static void testCut() {
        FibonacciHeap.HeapNode node = heap.insert(10, "Node");
        heap.cut(node);
        if (node.parent == null) {
            System.out.println("testCut: Passed");
        } else {
            System.out.println("testCut: Failed");
            System.out.println("Expected parent: null, Found parent: " + node.parent);
        }
    }

    // בדיקת פעולת cut עם rank adjustment
    public static void testCutWithRankAdjustment() {
        FibonacciHeap.HeapNode parentNode = heap.insert(50, "Parent Node");
        FibonacciHeap.HeapNode childNode = heap.insert(60, "Child Node");
        childNode.parent = parentNode;
        parentNode.rank++;
        parentNode.numOfChild++;
        heap.cut(childNode);
        if (childNode.parent == null) {
            System.out.println("testCutWithRankAdjustment: Passed");
        } else {
            System.out.println("testCutWithRankAdjustment: Failed");
            System.out.println("Expected parent: null, Found parent: " + childNode.parent);
        }
    }

    // בדיקת rank adjustment אחרי cut
    public static void testRankAdjustmentAfterCut() {
        FibonacciHeap.HeapNode parentNode = heap.insert(100, "Parent");
        FibonacciHeap.HeapNode childNode = heap.insert(50, "Child");
        childNode.parent = parentNode;
        parentNode.child = childNode;
        parentNode.rank++;
        parentNode.numOfChild++;
        heap.cut(childNode);
        int expectedRank = 0;
        if (parentNode.rank == expectedRank) {
            System.out.println("testRankAdjustmentAfterCut: Passed");
        } else {
            System.out.println("testRankAdjustmentAfterCut: Failed");
            System.out.println("Expected rank: " + expectedRank + ", Found rank: " + parentNode.rank);
        }
    }

    // בדיקת rank adjustment כאשר יש כמה ילדים
    public static void testRankAdjustmentMultipleChildren() {
        FibonacciHeap.HeapNode parentNode = heap.insert(100, "Parent");
        FibonacciHeap.HeapNode childNode1 = heap.insert(50, "Child 1");
        FibonacciHeap.HeapNode childNode2 = heap.insert(60, "Child 2");
        childNode1.parent = parentNode;
        parentNode.child = childNode1;
        parentNode.rank++;
        parentNode.numOfChild++;
        childNode2.parent = parentNode;
        parentNode.rank++;
        parentNode.numOfChild++;
        heap.cut(childNode1);
        int expectedRank = 1;
        if (parentNode.rank == expectedRank) {
            System.out.println("testRankAdjustmentMultipleChildren: Passed");
        } else {
            System.out.println("testRankAdjustmentMultipleChildren: Failed");
            System.out.println("Expected rank: " + expectedRank + ", Found rank: " + parentNode.rank);
        }
    }

    // בדיקה אם מחיקת מינימום בהיפ ריק מתבצעת כראוי
    public static void testDeleteMinOnEmptyHeap() {
        heap.deleteMin();
        if (heap.numTrees() == 0 && heap.size() == 0) {
            System.out.println("testDeleteMinOnEmptyHeap: Passed");
        } else {
            System.out.println("testDeleteMinOnEmptyHeap: Failed");
        }
    }

    // בדיקה אם meld פועל כראוי
    public static void testMeld() {
        FibonacciHeap anotherHeap = new FibonacciHeap();
        anotherHeap.insert(10, "Node A");
        anotherHeap.insert(20, "Node B");
        heap.meld(anotherHeap);
        if (heap.numTrees() == expectedNumTrees + 2) {
            System.out.println("testMeld: Passed");
        } else {
            System.out.println("testMeld: Failed");
        }
    }

    // בדיקה אם גודל ההיפ נכון
    public static void testSize() {
        if (heap.size() == expectedSize) {
            System.out.println("testSize: Passed");
        } else {
            System.out.println("testSize: Failed");
            System.out.println("Expected size: " + expectedSize + ", Found size: " + heap.size());
        }
    }
}
