package org.example.samples;

public class MultiThreading {

    /*

        Thread Dump:

        jstack pid > thread_dump.txt

     */

    public static void main(String[] args) {

        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += 1;
        }

        // fac(30); fac(100)

        // 1. connect to DB

        // 2. insert some data into DB

        // 2.1 insert 10 objs into DB

        // pow(10000, 7777777); pow(10000, 77777)

        Thread1 thread1 = new Thread1();
        thread1.start(); // * runs as Thread-1 (normal exec)
        // thread1.run(); - unnecessary as a call (.start), only as an abstract method - override


        MultiThreading _this = new MultiThreading(); // MultiThreading static
        Thread2 thread2 = _this.new Thread2(); // Thread2 not static (forced)
        // 'new' called by 'org.example.samples.MultiThreading.this' - cannot be referenced from a static context
        // hence, forced static (MultiThreading) _this.new for Thread2 (non-static class) instantiation
        thread2.start(); // * runs as Thread-0 (static exec)

    }

    private static class Thread1 extends Thread {

        @Override
        public void run() { // abstract method from Runnable interface (implemented by Thread)
            System.out.println(
                    "in thread : " +
                            currentThread().getName()
            );
        }

    }

    private class Thread2 extends Thread {

        @Override
        public void run() { // abstract method from Runnable interface (implemented by Thread)
            System.out.println(
                    "in thread : " +
                            currentThread().getName()
            );
        }

    }

}
