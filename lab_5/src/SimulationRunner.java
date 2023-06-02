import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SimulationRunner implements Callable<SimulationResult> {
    private final String name;
    private final long simulationDuration = 10000;
    private final int queueLength = 30;
    private final int consumersCount = 5;
    private final int producerMaxSleepingTime = 50;
    private final int consumerSleepingTime = 175;
    private final int loggerSleepingTime = 500;

    public SimulationRunner(String name) {
        this.name = name;
    }

    @Override
    public SimulationResult call() {
        long startTime = System.currentTimeMillis();

        Queue queue = new Queue(queueLength);
        var threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Callable<Object>> tasks = new ArrayList<>();
        tasks.add(Executors.callable(new Producer(queue, startTime, simulationDuration, producerMaxSleepingTime)));

        for (int i = 0; i < consumersCount; i++) {
            tasks.add(Executors.callable(new Consumer(queue, startTime, simulationDuration, consumerSleepingTime)));
        }

        Logger logger = new Logger(queue, startTime, name, simulationDuration, loggerSleepingTime);
        Thread loggerThread = new Thread(logger);

        try {
            loggerThread.start();

            threadPool.invokeAll(tasks);

            loggerThread.join();
        } catch (InterruptedException ignored) {
        }

        threadPool.shutdown();

        return logger.getSimulationResult();
    }
}
