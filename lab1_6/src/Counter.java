import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int value;
    private final ReentrantLock lock;

    public Counter(int value) {
        this.value = value;
        lock = new ReentrantLock();
    }

    public int getValue() {
        return value;
    }

    public void incrementNonSync() {
        value++;
    }

    public void decrementNonSync() {
        value--;
    }
    public synchronized void incrementSyncMethod() {
        value++;
    }

    public synchronized void decrementSyncMethod() {
        value--;
    }

    public void incrementSyncBlock() {
        synchronized (this) {
            value++;
        }
    }

    public void decrementSyncBlock() {
        synchronized (this) {
            value--;
        }
    }

    public void incrementLock(){
        lock.lock();
        value++;
        lock.unlock();
    }

    public void decrementLock(){
        lock.lock();
        value--;
        lock.unlock();
    }
}
