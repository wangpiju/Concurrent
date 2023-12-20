package org.jess.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class RunnableInstance implements Runnable {
    private static final Logger logger = LogManager.getLogger(RunnableInstance.class);

    @Override
    public void run() {
        for(int i=0; i<10; i++){
            System.out.println("Hello " + i);

            try {
                Thread.sleep(100);
            }
            catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }
    }
}

public class RunnableDemo {
    public static void main(String[] args){
        Thread t1 = new Thread(new RunnableInstance());
        Thread t2 = new Thread(new RunnableInstance());

        t1.start();
        t2.start();

    }
}
