public class IncrementThread extends Thread{
    private final Counter counter;
    private final int times;
    public IncrementThread(Counter counter, int times) {
        this.counter = counter;
        this.times = times;
    }

    @Override
    public void run() {
        for (int i = 0; i < times; i++) {
//            counter.incrementNonSync(); // non-synchronized increment
            counter.incrementSyncMethod(); // synchronized method increment
//            counter.incrementSyncBlock(); // synchronized block increment
//            counter.incrementLock(); // lock increment
        }
    }
}

