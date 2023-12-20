package org.jess.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultipleLocksDemo {

    private static final Random random = new Random();
    private static final List<Integer> list1 = new ArrayList<>();
    private static final List<Integer> list2 = new ArrayList<>();

    //Add two object locks to make sure that thread safe and performance
    private static Object lock1 = new Object();
    private static Object lock2 = new Object();


    public static  void stageOne() throws InterruptedException {
        synchronized (lock1) {
            Thread.sleep(1);

            list1.add(random.nextInt());
        }

    }

    public static  void stageTwo() throws InterruptedException {
        synchronized (lock2) {
            Thread.sleep(1);
            list2.add(random.nextInt());
        }
    }

    public static void process() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            stageOne();
            stageTwo();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start.....");
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    process();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    process();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        long end = System.currentTimeMillis();
        System.out.println("Time take: " + (end - start));
        System.out.println("List1: " + list1.size() + ", List2: " + list2.size());
    }
}
