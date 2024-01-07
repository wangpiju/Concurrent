package org.jess.basic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Worker implements Runnable{
    private static final Logger logger = LogManager.getLogger(Worker.class);

    private int id;

    public Worker(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        logger.info("Starting worker:  " + id);

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        logger.info("Completd worker:  " + id);
    }
}
public class ThreadPool {
    private static final Logger logger = LogManager.getLogger(ThreadPool.class);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        for(int i = 0; i < 100; i++) {
            executor.submit(new Worker(i));
        }

        executor.shutdown();
        logger.info("All tasks submitted.");

        executor.awaitTermination(1, TimeUnit.DAYS);

        logger.info("All tasks completed.");

    }

}
