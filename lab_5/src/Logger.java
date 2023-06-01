import static java.lang.Thread.sleep;

public class Logger implements Runnable {
    private final String runnerName;
    private final Queue queue;
    private int counter;
    private final long simulationDuration;
    private final long startTime;
    private final int loggerSleepingTime;

    public Logger(Queue queue, long startTime, String runnerName, long simulationDuration, int loggerSleepingTime) {
        this.queue = queue;
        this.startTime = startTime;
        this.runnerName = runnerName;
        this.simulationDuration = simulationDuration;
        this.loggerSleepingTime = loggerSleepingTime;
    }

    @Override
    public void run() {
        while (System.currentTimeMillis() - startTime <= simulationDuration) {
            try {
                sleep(loggerSleepingTime);
                int queueSize = queue.getSize();
                counter += queueSize;

                int servedCount = queue.getServedCount();
                int rejectedCount = queue.getRejectedCount();
                double chanceOfReject = calculateChanceOfReject(servedCount, rejectedCount);
                double averageQueueLength = calculateAverageQueueLength();

                System.out.println("\nRunner: " + runnerName +
                        "\nServed count: " + servedCount +
                        "\nRejected count: " + rejectedCount +
                        "\nReject chance: " + chanceOfReject +
                        "\nAverage queue length: " + averageQueueLength);

            } catch (InterruptedException ignored) {
            }
        }
    }

    SimulationResult getSimulationResult() {
        int servedItems = queue.getServedCount();
        int rejectedItems = queue.getRejectedCount();

        return new SimulationResult(rejectedItems, servedItems,
                calculateChanceOfReject(servedItems, rejectedItems),
                calculateAverageQueueLength());
    }

    private double calculateChanceOfReject(int servedItems, int rejectedItems) {
        return (double) rejectedItems / (servedItems + rejectedItems);
    }

    private double calculateAverageQueueLength() {
        return counter / ((double) simulationDuration / loggerSleepingTime);
    }
}