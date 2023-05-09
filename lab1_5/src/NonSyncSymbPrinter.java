public class NonSyncSymbPrinter extends Thread {
    private final char symbol;
    private final int elementsCount;

    public NonSyncSymbPrinter(char symbol, int elementsCount) {
        this.symbol = symbol;
        this.elementsCount = elementsCount;
    }

    @Override
    public void run() {
        for (int i = 0; i < elementsCount; i++) {
            System.out.print(symbol);
            
        }
    }
}

