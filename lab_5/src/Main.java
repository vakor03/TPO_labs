import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ArrayList<Callable<SimulationResult>> tasks = new ArrayList<>();

        tasks.add(new SimulationRunner("Runner 1"));
        tasks.add(new SimulationRunner("Runner 2"));
        tasks.add(new SimulationRunner("Runner 3"));

        List<Future<SimulationResult>> results = new ArrayList<>();
        try {
            results.addAll(threadPool.invokeAll(tasks));
        } catch (InterruptedException ignored) {
        }

        int servedCount = 0;
        int rejectedCount = 0;
        double queueLength = 0;
        for (var task : results) {
            SimulationResult simulationResult = task.get();
            servedCount += simulationResult.getServedCount();
            rejectedCount += simulationResult.getRejectedCount();
            queueLength += simulationResult.getAverageQueueLength();
        }

        double rejectChance = (double) rejectedCount / (servedCount + rejectedCount);

        System.out.println("\nTotal results: " +
                "\nServed count: " + servedCount +
                "\nRejected count: " + rejectedCount +
                "\nReject chance: " + rejectChance +
                "\nAverage queue length: " + queueLength / results.size());

        threadPool.shutdown();
    }
}

