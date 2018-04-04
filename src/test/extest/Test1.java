package test.extest;

public class Test1 implements Runnable {

    private String test1;

    @Override
    public void run() {

        synchronized (this) {

            while (test1 == null) {
                System.err.println("test1 is empty....");
                System.err.println("test1: i am sleep...");
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.err.println("test1 set: " + test1);
        }
    }

    public void setTest1(String test1) {
        this.test1 = test1;
    }

    public void wakeUp() {
        test1 = "alskdfja slfdka";
        System.err.println("test2: alskdfja slfdka");
        synchronized (this) {
            this.notifyAll();
            System.err.println("test2: notify All");

        }
    }
}
