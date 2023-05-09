public class SyncSymbPrinter extends Thread {
    private final Object lock;
    private final char symbol;
    private final int elementCount;

    public SyncSymbPrinter(char symbol, int elementCount, Object lock){
        this.symbol = symbol;
        this.lock = lock;
        this.elementCount = elementCount;
    }

    @Override
    public void run() {
        synchronized (lock){
            for (int i = 0; i < elementCount; i++) {
                System.out.print(symbol);
                lock.notify();
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            lock.notify();
        }
    }
}
