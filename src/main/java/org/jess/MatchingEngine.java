package org.jess;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.Comparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class OrderEvent {
    private UUID id;
    private double price;
    private long quantity;
    private boolean isBuyOrder;

    void set(double price, long quantity, boolean isBuyOrder) {
        this.id = UUID.randomUUID();
        this.price = price;
        this.quantity = quantity;
        this.isBuyOrder = isBuyOrder;
    }

    // Getter方法
    public UUID getId() { return id; }
    public double getPrice() { return price; }
    public long getQuantity() { return quantity; }
    public boolean isBuyOrder() { return isBuyOrder; }
}

class OrderEventFactory implements EventFactory<OrderEvent> {
    @Override
    public OrderEvent newInstance() {
        return new OrderEvent();
    }
}

class OrderEventHandler implements EventHandler<OrderEvent> {
    private final ConcurrentSkipListSet<OrderEvent> buyOrders = new ConcurrentSkipListSet<>(Comparator.comparing(OrderEvent::getPrice).reversed());
    private final ConcurrentSkipListSet<OrderEvent> sellOrders = new ConcurrentSkipListSet<>(Comparator.comparing(OrderEvent::getPrice));

    private static final Logger logger = LogManager.getLogger(OrderEventHandler.class);


    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        // 添加日誌輸出
        logger.info("處理訂單: Price={}, Quantity={}, BuyOrder={}", event.getPrice(), event.getQuantity(), event.isBuyOrder());
        // 根據訂單類型，選擇相對的訂單集合進行撮合
        if (event.isBuyOrder()) {
            matchOrder(event, sellOrders, buyOrders);
        } else {
            matchOrder(event, buyOrders, sellOrders);
        }
    }

    private void matchOrder(OrderEvent event, ConcurrentSkipListSet<OrderEvent> targetOrders, ConcurrentSkipListSet<OrderEvent> ownOrders) {
        for (OrderEvent targetOrder : targetOrders) {
            // 買單價格高於賣單，或賣單價格低於買單，則進行撮合
            if ((event.isBuyOrder() && event.getPrice() >= targetOrder.getPrice()) ||
              (!event.isBuyOrder() && event.getPrice() <= targetOrder.getPrice())) {

                // 在此處理撮合邏輯，例如更新訂單狀態、發送撮合成功事件等
                logger.info("Matched: " + event.getId() + " with " + targetOrder.getId());
                logger.info("Matched: " + event.getPrice() + " with " + targetOrder.getPrice());

                // 從對應集合中移除撮合成功的訂單
                targetOrders.remove(targetOrder);
                break; // 若一次只與一個訂單撮合，則跳出循環
            }
        }
        // 如果無法撮合，則將訂單添加到相應的集合中
        if (!targetOrders.contains(event)) {
            ownOrders.add(event);
        }
    }
}

public class MatchingEngine {
    private Disruptor<OrderEvent> disruptor;
    private RingBuffer<OrderEvent> ringBuffer;
    private static final Logger logger = LogManager.getLogger(MatchingEngine.class);


    public MatchingEngine() {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        Executor executor = Executors.newFixedThreadPool(numProcessors);
        logger.error("CPU processor: " + numProcessors);
       // Executor executor = Executors.newCachedThreadPool();
        OrderEventFactory factory = new OrderEventFactory();
        int bufferSize = 4096;

        disruptor = new Disruptor<>(factory, bufferSize, executor, ProducerType.MULTI, new YieldingWaitStrategy());
        disruptor.handleEventsWith(new OrderEventHandler());
        ringBuffer = disruptor.start();
    }

    public void sendOrder(double price, long quantity, boolean isBuyOrder) {
        long sequence = ringBuffer.next();
        try {
            OrderEvent event = ringBuffer.get(sequence);
            event.set(price, quantity, isBuyOrder);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    public void shutdown() {
        disruptor.shutdown();
    }

    public static class OrderProducer implements Runnable {
        private final MatchingEngine engine;
        private final int producerId;
        private static final Logger logger = LogManager.getLogger(OrderProducer.class);

        public OrderProducer(MatchingEngine engine, int producerId) {
            this.engine = engine;
            this.producerId = producerId;
        }

        @Override
        public void run() {
            // 在這裡生成隨機訂單數據，並發送到撮合引擎
            double price = Math.random() * 100;
            long quantity = (long) (Math.random() * 10);
            boolean isBuyOrder = Math.random() > 0.5;

            engine.sendOrder(price, quantity, isBuyOrder);

            logger.info("Producer " + producerId + " sent order on thread " + Thread.currentThread().getName());

//            try {
//                Thread.sleep(1000); // 模擬延遲
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        }
    }

    public static void  main(String[] args) {
        MatchingEngine engine = new MatchingEngine();

        // 創建並啟動多個生產者線程
        int numProducers = 1000; // 假設有5個交易員
        for (int i = 0; i < numProducers; i++) {
            new Thread(new OrderProducer(engine, i)).start();
        }

        // 讓主線程等待一段時間
        try {
            Thread.sleep(1000); // 等待10秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 停止撮合引擎
        engine.shutdown();
        System.exit(0);
    }
}
