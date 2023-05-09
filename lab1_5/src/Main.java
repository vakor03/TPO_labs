// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        SymbolPrinterThread thread1 = new SymbolPrinterThread('-', 100);
        SymbolPrinterThread thread2 = new SymbolPrinterThread('|', 100);
        thread1.start();
        thread2.start();
    }
}

