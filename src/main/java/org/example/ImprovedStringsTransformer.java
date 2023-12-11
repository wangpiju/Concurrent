package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImprovedStringsTransformer {

    private List<String> data;
    private final ExecutorService executorService;

    public ImprovedStringsTransformer(List<String> startingData) {
        this.data = startingData;
        this.executorService =
          Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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

    public List<String> transform(List<StringFunction> functions)
      throws InterruptedException, ExecutionException
    {
        List<Future<List<String>>> futures = new ArrayList<>();
        for (StringFunction f : functions) {
            futures.add(executorService.submit(createTask(f)));
        }

        for (Future<List<String>> future : futures) {
            data = future.get();
        }

        executorService.shutdown();
        return data;
    }

    public interface StringFunction {

        String transform(String str);
    }
}

