import static java.lang.Thread.sleep;

public class Consumer implements Runnable {
    private final Queue queue;
    private final long startTime;
    private final long simulationDuration;
    private final int sleepingTime;

    public Consumer(Queue queue, long startTime, long simulationDuration, int sleepingTime) {
        this.queue = queue;
        this.startTime = startTime;
        this.sleepingTime = sleepingTime;
        this.simulationDuration = simulationDuration;
    }

    @Override
    public void run() {
        while (System.currentTimeMillis() - startTime <= simulationDuration) {
            queue.serve();

            try {
                sleep(sleepingTime);
                queue.incServedCount();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
