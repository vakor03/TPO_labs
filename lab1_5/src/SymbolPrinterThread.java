public class SymbolPrinterThread extends Thread {
    private final char symbol;
    private final int times;

    public SymbolPrinterThread(char symbol, int times) {
        this.symbol = symbol;
        this.times = times;
    }

    @Override
    public void run() {
        for (int i = 0; i < times; i++) {
            System.out.print(symbol);
            
        }
    }
}
