import fibonachiheap.fibonachiheap.FibonacciHeap;

public static int get_min(FibonacciHeap.HeapNode now, int depth, int max_depth)
{;
    if (depth > max_depth)
        return -3;
    if (now.child == null)
        return now.key;

    int mini = now.key;
    int counter = 0;
    FibonacciHeap.HeapNode node_now = now.child;
    do {
        counter++;
        mini = Math.min(mini, get_min(node_now, depth + 1, max_depth));
        if (node_now.next.prev != node_now)
            return -1;
        if ((node_now.parent != now && depth > 1) || (node_now.parent != null && depth == 1)) {
            return 0;
        }
        node_now = node_now.next;
    } while(node_now != now.child && counter <= now.rank);
    if (counter != now.rank) {
        return -2;
    }

    return mini;
}

public static boolean good_FibonacciHeap(FibonacciHeap heap_now)
{
    if (heap_now.min == null)
        return heap_now.numTrees() == 0;
    FibonacciHeap.HeapNode fake_node = new FibonacciHeap.HeapNode(1000000,".");
    fake_node.rank = heap_now.numTrees();
    fake_node.child = heap_now.min;
    int max_rank = 2 * (int)(Math.log(heap_now.size()) / Math.log(2)) + 2;
    return get_min(fake_node, 1, max_rank) == heap_now.min.key && get_min(fake_node, 1, max_rank) != 0;
}

public static void main(String[] args) {
    int by = 30;
    Random random = new Random();
    for (int i = 0; i < 150; i++)
    {
        //System.out.println("Testing " + i + "th FibonacciHeap");
        FibonacciHeap heap = new FibonacciHeap();
        int size_now = 50000;
        List<AbstractMap.SimpleEntry<Integer, FibonacciHeap.HeapNode>> list = new ArrayList<>();

        for (int j = 0; j < size_now; j++)
        {
            int value = random.nextInt(size_now) + 1;
            //System.out.println("Adding " + value);
            FibonacciHeap.HeapNode new_node = heap.insert(value, "");
            list.add(new AbstractMap.SimpleEntry<>(value, new_node));
            if (!good_FibonacciHeap(heap))
                System.out.println("Error1!");
        }
        list.sort(Comparator.comparingInt(AbstractMap.SimpleEntry::getKey));

        for (int j = 0; j < size_now; j++)
        {
            if (random.nextInt(15) == 1)
            {
                for (int k = 0; k < by; k++)
                {
                    int index_change = random.nextInt(size_now - j) + j;
                    if (list.get(index_change).getValue().key > by)
                    {
                        //System.out.println("Decrease " + list.get(index_change).getValue().key + " by " + by);
                        heap.decreaseKey(list.get(index_change).getValue(), by);
                        if (!good_FibonacciHeap(heap))
                        {
                            System.out.println("Error2!");
                            return;
                        }
                        AbstractMap.SimpleEntry<Integer, FibonacciHeap.HeapNode> out_node = list.get(index_change);
                        list.set(index_change, new AbstractMap.SimpleEntry<>(out_node.getKey() - by, out_node.getValue()));
                    }
                }
                List<AbstractMap.SimpleEntry<Integer, FibonacciHeap.HeapNode>> subList = list.subList(j, list.size());
                subList.sort(Comparator.comparingInt(AbstractMap.SimpleEntry::getKey));
            }
            if (heap.findMin().key != list.get(j).getKey())
            {
                System.out.println("Error3!");
                return;
            }
            //System.out.println("Delete value maybe same as min " + heap.findMin().key);
            //System.out.println(list.get(j).getValue().key);
            heap.delete(list.get(j).getValue());
            if (!good_FibonacciHeap(heap))
            {
                System.out.println("Error4!");
                return;
            }
        }
    }
    System.out.println("After!!!!!");
}