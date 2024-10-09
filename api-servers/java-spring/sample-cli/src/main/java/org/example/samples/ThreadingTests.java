package org.example.samples;

class Animal {
    private String name;
    private String type;

    public Animal(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String toString() {
        return "Animal : "
                + this.name
                + ", Type : "
                + this.type;
    }
}

class TThread extends Animal implements Runnable {

    public TThread(String name, String type) { // required for parent Animal - super(instantiation)
        super(name, type);
    }

    @Override
    public void run() {

    }

}

public class ThreadingTests {

    public static void main(String[] args) {

        Animal a1 = new Animal("tom", "cat");
        Animal a2 = new Animal("wolf", "dog");

        Thread1 thread1 = new Thread1(a1);
        Thread1 thread2 = new Thread1(a1);

        Thread1 thread3 = new Thread1(a2);
        Thread1 thread4 = new Thread1(a2);

        thread1.start(); thread2.start();
        thread3.start(); thread4.start();

    }

    private static class Thread1 extends Thread { // private internal class (doesn't conflict with 'Thread1' in other files)

        private final Animal a;

        public Thread1(Animal a) {
            this.a = a;
        }

        @Override
        public void run() {

            // sync based on thread-locking on this.a object
            synchronized (this.a) { // only 1 thread can exec this scope at a time
                // since all instances of this thread depend on this.a (preventing race conditions - (not deadlocks))

                System.out
                        .println(
                                "current thread : "
                                        + currentThread().getName()
                                        + ", animal : "
                                        + this.a.toString()
                        );
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }

}
