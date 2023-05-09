public class DecrementThread extends Thread {
    private final Counter counter;
    private final int times;

    public DecrementThread(Counter counter, int times) {
        this.counter = counter;
        this.times = times;
    }

    @Override
    public void run() {
        for (int i = 0; i < times; i++) {
//            counter.decrementNonSync(); // non-synchronized decrement
//            counter.decrementSyncMethod(); // synchronized method decrement
//            counter.decrementSyncBlock(); // synchronized block decrement
            counter.decrementLock(); // lock decrement
        }

    }
}
