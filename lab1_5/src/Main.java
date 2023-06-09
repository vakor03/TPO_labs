import jdk.jshell.spi.ExecutionControl;

import java.util.List;
public class Main {
    public static void main(String[] args) {
        NonSyncSymbPrinter nonSyncSymbPrinter = new NonSyncSymbPrinter('-', 500);
        NonSyncSymbPrinter nonSyncSymbPrinter1 = new NonSyncSymbPrinter('|', 500);
        nonSyncSymbPrinter.start();
        nonSyncSymbPrinter1.start();

        try {
            nonSyncSymbPrinter.join();
            nonSyncSymbPrinter1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println();
//        char[] symbols = {'-', '|'};
//        Synchronizer synchronizer = new Synchronizer(symbols);
//        Thread[] threads = new Thread[symbols.length];
//        for (int i = 0; i < symbols.length; i++) {
//            threads[i] = new Thread(new SymbolPrinter(symbols[i], synchronizer, 5000));
//        }
//        for (Thread value : threads) {
//            value.start();
//        }
//
//        try {
//            for (Thread thread : threads) {
//                thread.join();
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

    }
}
