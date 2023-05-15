import java.util.ArrayList;

public class ProducerConsumer {
    public static void main(String[] args) {
        SharedResource sharedResource = new SharedResource(1000);
        ArrayList<Thread> threads = new ArrayList<>();

        threads.add(new Thread(new Producer(sharedResource, 1)));
        threads.add(new Thread(new Consumer(sharedResource, 1000)));

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException ignored) {
        }

    }
}
