package test.extest;

public class Main {
    public static void main(String[] args) {
        Test1 test1 = new Test1();
        new Thread(test1).start();
        new Thread(new Test2(test1)).start();
    }
}
