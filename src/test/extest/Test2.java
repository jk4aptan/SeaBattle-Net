package test.extest;

public class Test2 implements Runnable {
    private Test1 test1;

    public Test2(Test1 test1) {

        this.test1 = test1;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        synchronized (test1) {
//            test1.setTest1("wake up");
//            System.err.println("test2: send wake up");
//            test1.notifyAll();
//            System.err.println("test2: notify All");
//
//        }

        test1.wakeUp();
    }
}
