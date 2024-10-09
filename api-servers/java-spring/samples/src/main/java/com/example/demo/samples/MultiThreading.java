package com.example.demo.samples;

public class MultiThreading {

    public static void main(String[] args) {

        Thread1 thread = new Thread1();
        thread.start();

    }

    private static class Thread1 extends Thread {

        @Override
        public void run() {
            System.out.println(
                    "in thread : " +
                    currentThread().getName()
            );
        }

    }

}
