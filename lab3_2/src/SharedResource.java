public class SharedResource {
    private boolean empty = true;
    private boolean full = false;
    private int[] buffer;
    private int count = 0;
    public SharedResource(int size){
        buffer = new int[size];
    }

    public synchronized int take() {
        while (empty) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }

        int value = buffer[count--];
        this.empty = count == 0;
        this.full = false;

        notifyAll();
        return value;
    }

    public synchronized void put(int value) {
        while (full) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }

        empty = false;
        buffer[++count] = value;
        full = count == buffer.length - 1;
        notifyAll();
    }
}


