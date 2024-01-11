package org.jess.basic;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ProducerConsumerDemo {
    private static final Logger logger = LogManager.getLogger(ProducerConsumerDemo.class);
    private BlockingQueue<Integer> queue = new ArrayBlockingQueue(10);

    public static void main(String[] args) throws InterruptedException {

    }

    private void producer() throws InterruptedException {
        Random random = new Random();
        while(true){
            queue.put(random.nextInt(100));
        }
    }

    private void consumer() throws InterruptedException {
        Random random = new Random();
        while(true){
            Thread.sleep(100);

            if(random.nextInt(10) == 0){
                Integer value = queue.take();
                logger.info("Taken value: " + value + " ; Queue size is: " + queue.size());
            }
        }
    }

}
