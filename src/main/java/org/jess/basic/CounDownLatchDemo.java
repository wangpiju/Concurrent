package org.jess.basic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ProcessorLatch implements Runnable{
    private static final Logger logger = LogManager.getLogger(ProcessorLatch.class);

    private CountDownLatch countDownLatch;

    public ProcessorLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override public void run() {
        logger.info("Started!!!");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error("Interrupted!!!", e);
        }
        countDownLatch.countDown();
    }
}
public class CounDownLatchDemo {
    private static final Logger logger = LogManager.getLogger(CounDownLatchDemo.class);

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for(int i = 0; i < 6; i++){
            executorService.submit(new ProcessorLatch(countDownLatch));
        }
        countDownLatch.await();

        logger.info("Completed!!!!");

    }

}
