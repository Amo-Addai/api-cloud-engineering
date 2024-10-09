package org.example.samples;

class Thread1 implements Runnable {

    @Override
    public void run() {
        // Thread1 doesn't extend Thread, so required for .currentThread
        System.out.println("in thread : " + Thread.currentThread().getName());
    }

}

public class CustomThread extends Thread1 { // { //

    public static void main(String[] args) {

        CustomThread _this = new CustomThread();
        Thread thread = new Thread(_this.new Thread2());
        thread.start(); // * runs as Thread-1; Thread-1 when CustomThread extends Thread1

        Thread3 thread3 = new Thread3();
        thread3.start(); // * runs as Thread-2; Thread-2 when CustomThread extends Thread1

        Thread4 thread4 = new Thread4();
        // cannot call .start (doesn't extend Thread; only implements Runnable)
        thread4.run(); // * runs as main-thread; main-thread when CustomThread extends Thread1

        Thread5 thread5 = new Thread5();
        // both extends Thread & implements Runnable; can call .start
        thread5.start(); // * runs as Thread-3; Thread-3 when CustomThread extends Thread1
        thread5.setDaemon(true); // run as daemon / background-thread

        Thread6 thread6 = new Thread6();
        try {
            thread6.start();
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(
                    "thread6 after interrupt : "
                    + thread6.getName()
                    + " "
                    + thread6.isAlive()
            );
            System.out.println(
                    "main thread after interrupt : "
                            + Thread.currentThread().getName()
                            + " "
                            + Thread.currentThread().isAlive()
            );
        }

    }

    private class Thread2 extends Thread {

        @Override
        public void run() {
            System.out.println("in thread : " + currentThread().getName());
        }

    }

    private static class Thread3 extends Thread {

        @Override
        public void run() {
            System.out.println("in thread : " + currentThread().getName());
        }

    }

    private static class Thread4 implements Runnable {

        @Override
        public void run() {
            System.out.println("in thread : " + Thread.currentThread().getName());
        }

    }

    private static class Thread5 extends Thread implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("in thread before : " + Thread.currentThread().getName());
                Thread.sleep(5000); // throws InterruptedException
                System.out.println("in thread after sleep : " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static class Thread6 extends Thread implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println(
                        "in thread before : "
                        + Thread.currentThread().getName()
                        + " " + currentThread().isAlive()
                );
                currentThread().setDaemon(true); // run as daemon / background-task
                Thread.sleep(5000); // throws InterruptedException
                System.out.println(
                        "in thread after sleep : "
                        + Thread.currentThread().getName()
                        + " " + currentThread().isAlive()
                );
            } catch (InterruptedException e) {
                System.out.println(e.toString());
                e.printStackTrace();
                currentThread().interrupt(); // recall InterruptedException for main-exec scope
            }
        }

    }

}

class Thread5 extends CustomThread implements Runnable {

    @Override
    public void run() {
        System.out.println("in thread : " + Thread.currentThread().getName());
    }

}