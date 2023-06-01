import java.util.Random;

import static java.lang.Thread.sleep;

public class Producer implements Runnable {
    private final Queue queue;
    private final long startTime;
    private final long simulationDuration;
    private final int maxSleepingTime;

    public Producer(Queue queue, long startTime, long simulationDuration, int maxSleepingTime) {
        this.queue = queue;
        this.startTime = startTime;
        this.simulationDuration = simulationDuration;
        this.maxSleepingTime = maxSleepingTime;
    }

    @Override
    public void run() {
        var random = new Random();

        while ((System.currentTimeMillis() - startTime) <= simulationDuration) {
            try {
                sleep(random.nextInt(maxSleepingTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            queue.put(random.nextInt(100));
        }
    }
}
