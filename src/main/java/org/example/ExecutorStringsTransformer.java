package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorStringsTransformer {
    private List<String> data = new ArrayList<String>();

    public ExecutorStringsTransformer(List<String> startingData) {
        this.data = startingData;
    }

    private synchronized void forEach(StringFunction function) {
        List<String> newData = new ArrayList<String>();
        for (String str : data) {
            newData.add(function.transform(str));
        }
        data = newData;
    }

    public List<String> transform(List<StringFunction> functions)
      throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(functions.size());
        List<Future<?>> futures = new ArrayList<>();

        for (final StringFunction f : functions) {
            futures.add(executor.submit(() -> forEach(f)));
        }

        for (Future<?> future : futures) {
            future.get(); // 等待每個任務完成
        }

        executor.shutdown();
        return data;
    }

    public static interface StringFunction {
        String transform(String str);
    }
}
/**
 * Java 的 ExecutorService 提供了一種更優雅的方式來處理多線程。
 * 您可以創建一個固定大小的線程池並提交任務給它。這樣做可以更好地管理線程生命週期，
 * 並且可以使用如 Future 和 Callable 等工具來更輕鬆地獲取結果。
 * 這種方法提供了更好的效能和更低的資源消耗。
 *
 * 在這個修改後的實現中，我使用了一個固定大小的線程池來避免不必要的線程創建與銷毀。
 * 此外，我使用了 synchronized 關鍵字來避免多線程同時修改 data 變數。
 * 這樣可以保證線程安全性並提高效能。
 */
