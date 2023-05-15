public class Producer implements Runnable {
    private final SharedResource sharedResource;
    private final long sleepTime;

    public Producer(SharedResource sharedResource, long sleepTime) {
        this.sharedResource = sharedResource;
        this.sleepTime = sleepTime;
    }

    public void run() {
        for (int i = 0; i < 10_000; i++) {
            sharedResource.put(i);
            System.out.println("Produced: " + i);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {
            }
        }
    }
}


