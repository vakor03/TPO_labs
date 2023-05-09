public class SymbolPrinterSynchronizer {
    private SymbolPrinterThread thread1;
    private SymbolPrinterThread thread2;

    public SymbolPrinterSynchronizer(SymbolPrinterThread thread1, SymbolPrinterThread thread2) {
        this.thread1 = thread1;
        this.thread2 = thread2;
    }
}
