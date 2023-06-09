public class SymbolPrinter implements Runnable {
    private final char symbol;
    private final Synchronizer synchronizer;
    private final int iterationCount;

    public char getSymbol() {
        return symbol;
    }

    public SymbolPrinter(char symbol, Synchronizer synchronizer, int iterationCount) {
        this.symbol = symbol;
        this.synchronizer = synchronizer;
        this.iterationCount = iterationCount;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterationCount; i++) {
            synchronized (synchronizer) {
                while (synchronizer.getActiveSymbol() != symbol) {
                    try {
                        synchronizer.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                System.out.print(symbol);
                synchronizer.updateActiveIndex();
            }
        }
    }
}