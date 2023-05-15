public class Consumer implements Runnable {
    private final SharedResource sharedResource;
    private final long sleepTime;

    public Consumer(SharedResource sharedResource, long sleepTime) {
        this.sharedResource = sharedResource;
        this.sleepTime = sleepTime;
    }

    public void run() {
        for (int i = 0; i < 10_000; i++) {
            int value = sharedResource.take();
            System.out.println("Consumed: " + value);
            try {
                Thread.sleep(this.sleepTime);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
