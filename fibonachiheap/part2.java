package fibonachiheap.fibonachiheap;
import java.util.Random;
import java.util.logging.Handler;

//

public class part2 {
    public static int[] randomPermutation(int n) {
        int[] perm = new int[n];
        // Initialize array with values 1..n
        for (int i = 0; i < n; i++) {
            perm[i] = i + 1;
        }

        // Create a Random object
        Random random = new Random();

        // Shuffle using Fisher-Yates
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap perm[i] and perm[j]
            int temp = perm[i];
            perm[i] = perm[j];
            perm[j] = temp;
        }

        return perm;
    }
    public static void do_first_test(){
        for(int i = 0; i < 5; i++){
            int n = (int)Math.pow(3,i+8) - 1;
            FibonacciHeap heap = new FibonacciHeap();

            long start = System.currentTimeMillis();

            int[] per = randomPermutation(n);
            for(int j = 0; j < n; j++){
                heap.insert(per[j], "Node " + j);
            }
            heap.deleteMin();

            long end = System.currentTimeMillis();
            long duration = end - start;

            System.out.println(heap.numTrees());
            System.out.println(heap.totalCuts());
            System.out.println(heap.totalLinks());
            System.out.println(heap.size());
            System.out.println("Time for n = " + n + " is: " + duration);
        }
    }
    public static void do_second_test(){
        for(int i = 0; i < 5; i++){
            int n = (int)Math.pow(3,i+8) - 1;
            n = 10;
            FibonacciHeap heap = new FibonacciHeap();

            long start = System.currentTimeMillis();

            int[] per = randomPermutation(n);
            FibonacciHeap.HeapNode heapNode;
            for(int j = 0; j < n; j++){
                FibonacciHeap.HeapNode x = heap.insert(per[j], "Node " + j);
                if (x.key == 4){
                    heapNode = x;
                }
            }
            for(int j = 0; j < n/2; j++) {
                System.out.println(heap.size());
                System.out.println("min"+heap.findMin().key);
                System.out.println("trees:"+heap.numTrees());
                heap.deleteMin();
            }
            long end = System.currentTimeMillis();
            long duration = end - start;

            System.out.println(heap.numTrees());
            System.out.println(heap.totalCuts());
            System.out.println(heap.totalLinks());
            System.out.println(heap.size());
            System.out.println("Time for n = " + n + " is: " + duration);
        }
    }
}









