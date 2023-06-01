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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SimulationResult) obj;
        return this.rejectedCount == that.rejectedCount &&
                this.servedCount == that.servedCount &&
                Double.doubleToLongBits(this.chanceOfReject) == Double.doubleToLongBits(that.chanceOfReject) &&
                Double.doubleToLongBits(this.averageQueueLength) == Double.doubleToLongBits(that.averageQueueLength);
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(rejected, served, chanceOfReject, averageQueueLength);
//    }

//    @Override
//    public String toString() {
//        return "SimulationResult[" +
//                "rejected=" + rejected + ", " +
//                "served=" + served + ", " +
//                "chanceOfReject=" + chanceOfReject + ", " +
//                "averageQueueLength=" + averageQueueLength + ']';
//    }

}
