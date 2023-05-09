// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        NonSyncSymbPrinter nonSyncSymbPrinter = new NonSyncSymbPrinter('-', 100);
        NonSyncSymbPrinter nonSyncSymbPrinter1 = new NonSyncSymbPrinter('|', 100);
        nonSyncSymbPrinter.start();
        nonSyncSymbPrinter1.start();

        try {
            nonSyncSymbPrinter.join();
            nonSyncSymbPrinter1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println();
        Object lock = new Object();
        SyncSymbPrinter syncSymbPrinter = new SyncSymbPrinter('-', 100, lock);
        SyncSymbPrinter syncSymbPrinter1 = new SyncSymbPrinter('|', 100, lock);
        syncSymbPrinter.start();
        syncSymbPrinter1.start();

    }
}

