package info.kgeorgiy.ja.antonov.concurrent.mapper;

public class MySemaphore {

    private volatile int current;
    private final int border;


    public MySemaphore(int border) {
        this.border = border;
        this.current = 0;
    }

    public synchronized void inc() {
        current++;
        if (current >= border) {
            notifyAll();//TODO ?
        }
    }

    public synchronized void acquire() throws InterruptedException {
        while (current < border) {
            wait();
        }
    }


}
