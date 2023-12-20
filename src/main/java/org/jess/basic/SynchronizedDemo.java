package org.jess.basic;

public class SynchronizedDemo {
    private int count = 0;

    public synchronized void incrementCount(){
        count++;
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedDemo synchronizedDemo = new SynchronizedDemo();
        synchronizedDemo.doWork();

    }

    public void doWork() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override public void run() {
                for (int i = 0; i <10000;i++) {
                    //count++;
                    //count  = count + 1; This is three steps
                    incrementCount();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override public void run() {
                for (int i = 0; i <10000;i++) {
                   // count++;
                    incrementCount();
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Count is: " + count);
    }
}
