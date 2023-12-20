package org.jess.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Runner extends Thread{
    private static final Logger logger = LogManager.getLogger(Runner.class);

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
public class ThreadDemo {
    public static void main(String[] args){
        Runner r1 = new Runner();
        Runner r2 = new Runner();

        r1.start();
        r2.start();
    }

}
