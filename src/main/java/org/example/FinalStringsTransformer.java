package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FinalStringsTransformer {

    private List<String> data;
    private final ExecutorService executorService;
    private int poolSize = Runtime.getRuntime().availableProcessors(); // 可配置的線程池大小

    public FinalStringsTransformer(List<String> startingData, int poolSize) {
        this.data = startingData;
        this.poolSize = poolSize;
        this.executorService = Executors.newFixedThreadPool(this.poolSize);
    }

    private Callable<List<String>> createTask(final StringFunction function) {
        return () -> {
            List<String> newData = new ArrayList<>();
            for (String str : data) {
                newData.add(function.transform(str));
            }
            return newData;
        };
    }

    public List<String> transform(List<StringFunction> functions) throws InterruptedException {
        List<Future<List<String>>> futures = new ArrayList<>();
        for (StringFunction f : functions) {
            futures.add(executorService.submit(createTask(f)));
        }

        for (Future<List<String>> future : futures) {
            try {
                data = future.get();
            } catch (ExecutionException e) {
                e.printStackTrace(); // 錯誤處理
            }
        }

        // 優雅地關閉線程池
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        return data;
    }

    public interface StringFunction {
        String transform(String str);
    }
}

