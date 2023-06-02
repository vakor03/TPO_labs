public class SimulationResult {
    private final int rejectedCount;
    private final int servedCount;
    private final double chanceOfReject;
    private final double averageQueueLength;

    SimulationResult(int rejectedCount, int servedCount, double chanceOfReject, double averageQueueLength) {
        this.rejectedCount = rejectedCount;
        this.servedCount = servedCount;
        this.chanceOfReject = chanceOfReject;
        this.averageQueueLength = averageQueueLength;
    }

    public int getRejectedCount() {
        return rejectedCount;
    }

    public int getServedCount() {
        return servedCount;
    }

    public double getChanceOfRejection() {
        return chanceOfReject;
    }

    public double getAverageQueueLength() {
        return averageQueueLength;
    }

}
