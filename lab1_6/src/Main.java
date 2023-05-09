import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        Counter counter = new Counter(0);
        HashSet<Thread> threads = new HashSet<>();

        threads.add(new IncrementThread(counter, 1_000_000));
        threads.add(new IncrementThread(counter, 1_000_000));
        threads.add(new DecrementThread(counter, 1_000_000));

        ReentrantLock lock = new ReentrantLock();
        lock.lock();

        for (Thread thread : threads) {
            thread.start();
        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(counter.getValue());
    }
}

