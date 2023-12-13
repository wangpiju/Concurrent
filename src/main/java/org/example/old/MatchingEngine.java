package org.example.old;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class OrderEvent {
    private UUID id;
    private double price;
    private long quantity;
    private boolean isBuyOrder;

    public OrderEvent() {
        this.id = UUID.randomUUID();
    }

    void set(double price, long quantity, boolean isBuyOrder) {
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

class OrderEventHandler {
    private final ConcurrentSkipListSet<OrderEvent> buyOrders = new ConcurrentSkipListSet<>(Comparator.comparing(OrderEvent::getPrice).reversed());
    private final ConcurrentSkipListSet<OrderEvent> sellOrders = new ConcurrentSkipListSet<>(Comparator.comparing(OrderEvent::getPrice));

    private static final Logger logger = LogManager.getLogger(OrderEventHandler.class);

    public void handleEvent(OrderEvent event) {
        logger.info("Processing order: Price={}, Quantity={}, BuyOrder={}", event.getPrice(), event.getQuantity(), event.isBuyOrder());
        if (event.isBuyOrder()) {
            matchOrder(event, sellOrders, buyOrders);
        } else {
            matchOrder(event, buyOrders, sellOrders);
        }
    }

    private void matchOrder(OrderEvent event, ConcurrentSkipListSet<OrderEvent> targetOrders, ConcurrentSkipListSet<OrderEvent> ownOrders) {
        for (OrderEvent targetOrder : targetOrders) {
            if ((event.isBuyOrder() && event.getPrice() >= targetOrder.getPrice()) ||
              (!event.isBuyOrder() && event.getPrice() <= targetOrder.getPrice())) {
                logger.info("Matched: " + event.getId() + " with " + targetOrder.getId());
                targetOrders.remove(targetOrder);
                break;
            }
        }
        if (!targetOrders.contains(event)) {
            ownOrders.add(event);
        }
    }
}

public class MatchingEngine {
    private final ConcurrentLinkedQueue<OrderEvent> orderQueue = new ConcurrentLinkedQueue<>();
    private final OrderEventHandler handler = new OrderEventHandler();
    private final ExecutorService executor;
    private static final Logger logger = LogManager.getLogger(MatchingEngine.class);
    private volatile boolean isShutdownInitiated = false;

    public MatchingEngine() {
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public synchronized void sendOrder(double price, long quantity, boolean isBuyOrder) {
        if (!isShutdownInitiated) {
            OrderEvent event = new OrderEvent();
            event.set(price, quantity, isBuyOrder);
            orderQueue.add(event);
            executor.submit(() -> processOrders());
        } else {
            logger.warn("Attempted to send order after shutdown initiation");
        }
    }

    private void processOrders() {
        while (!orderQueue.isEmpty()) {
            OrderEvent event = orderQueue.poll();
            if (event != null) {
                handler.handleEvent(event);
            }
        }
    }

    public synchronized void shutdown() {
        isShutdownInitiated = true;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    public static class OrderProducer implements Runnable {
        private final MatchingEngine engine;
        private static final Logger logger = LogManager.getLogger(OrderProducer.class);

        public OrderProducer(MatchingEngine engine) {
            this.engine = engine;
        }

        @Override
        public void run() {
            double price = Math.random() * 100;
            long quantity = (long) (Math.random() * 10);
            boolean isBuyOrder = Math.random() > 0.5;

            engine.sendOrder(price, quantity, isBuyOrder);
            logger.info("Order sent by thread: " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        MatchingEngine engine = new MatchingEngine();
        int numberOfProducers = 1000;

        ExecutorService producerExecutor = Executors.newFixedThreadPool(numberOfProducers);

        for (int i = 0; i < numberOfProducers; i++) {
            producerExecutor.submit(new OrderProducer(engine));
        }

        try {
            producerExecutor.awaitTermination(1, TimeUnit.SECONDS);
            producerExecutor.shutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        engine.shutdown();
    }
}

/**
 * 效能和低延遲：
 *
 * 1.基礎版本使用標準的隊列和多線程，可能會在高負載下表現不佳，特別是在低延遲需求的場景中。
 * 相比之下，優化版本利用 Disruptor 的高效設計，特別適合於需要快速響應和低延遲的交易系統。
 *
 * 2.資源利用：
 * Disruptor 提供的無鎖環狀緩衝區極大地減少了垃圾回收的壓力，從而提高了整體性能。
 *
 * 3. 擴展性和靈活性：
 * 基礎版本的擴展性較低，主要受限於其線程和隊列管理。而優化版本則提供了更多的配置選項，
 * 使其能夠更好地適應各種不同的場景和需求。
 *
 * 4. 並行數據處理：
 * Disruptor 是專門為高效的並行處理而設計的，提供了比標準隊列和線程池更高效的並行處理機制。
 *
 * 5.錯誤處理和穩定性：
 * 優化版本在錯誤處理和系統穩定性方面提供了更好的支持，這對於交易系統來說至關重要。
 */
