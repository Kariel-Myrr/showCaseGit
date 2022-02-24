package info.kgeorgiy.ja.antonov.hello.client;

class HelloChannelInfo {
    private int count;
    private final int number;
    private long writeTime;
    private final long timeOut;

    public HelloChannelInfo(int number, long timeOut) {
        this.number = number;
        this.count = 0;
        this.timeOut = timeOut;
    }

    public void inc() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public int getNumber() {
        return number;
    }

    public boolean isWaiting() {
        return System.currentTimeMillis() - writeTime < timeOut;
    }

    public void setWaiting() {
        this.writeTime = System.currentTimeMillis();
    }


    public void notWaiting() {
        this.writeTime = 0;
    }
}
