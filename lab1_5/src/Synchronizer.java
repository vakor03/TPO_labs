public class Synchronizer {
    private final char[] symbols;
    private int activeIndex;
    private int counter;

    public Synchronizer(char[] symbols) {
        this.symbols = symbols;
    }

    public char getActiveSymbol() {
        return symbols[activeIndex];
    }

    public void updateActiveIndex() {
        activeIndex++;
        activeIndex %= symbols.length;
        counter++;

        if (counter % 100 == 0 && counter != 0) {
            System.out.println();
        }

        notifyAll();
    }
}
