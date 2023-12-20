package org.jess.basic;

import java.util.Properties;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Processor extends Thread{
    private static final Logger logger = LogManager.getLogger(Processor.class);
    // volatile can make sure that every therads have no cache of this variable
    private volatile boolean running = true;


    @Override
    public void run() {
        while (running) {
            System.out.println("Hello");
            try {
                Thread.sleep(100);
            }
            catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }
    }
    public void shutdown(){
        running=false;
    }
}
public class SynchronizationDemo {
    public static void main(String[] args){
        Processor proc1 = new Processor();
        proc1.start();
        System.out.println("Press return to stop...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        proc1.shutdown();

    }

}
