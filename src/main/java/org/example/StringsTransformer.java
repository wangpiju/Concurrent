package org.example;

import java.util.ArrayList;
import java.util.List;

public class StringsTransformer {

    private List<String> data = new ArrayList<String>();

    public StringsTransformer(List<String> startingData) {
        this.data = startingData;
    }

    private void forEach(StringFunction function) {
        List<String> newData = new ArrayList<String>();
        for (String str : data) {
            newData.add(function.transform(str));
        }
        data = newData;
    }

    public List<String> transform(List<StringFunction> functions)
      throws InterruptedException
    {
        List<Thread> threads = new ArrayList<Thread>();
        for (final StringFunction f : functions) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    forEach(f);
                }
            }));
        }
        for (Thread t : threads) {
            t.join();
        }
        return data;
    }

    public interface StringFunction {

        String transform(String str);
    }
}

/**
 * Thread Safety:
 * The data field is accessed and modified by multiple threads,
 * potentially leading to race conditions and inconsistent states.
 * <p>
 * Efficiency:
 * Spawning a new thread for each transformation might be inefficient
 * due to context switching and overhead, especially if the number of transformations is large.
 * <p>
 * Two Alternatives:
 * <p>
 * Executor Service: Use an ExecutorService to manage a pool of threads.
 * This can improve efficiency by reusing threads and reducing overhead.
 * It also provides better control over concurrency and task scheduling.
 * <p>
 * Futures and CompletableFutures: Utilize Future or CompletableFuture for asynchronous
 * computation.
 * This allows transformations to be applied in a non-blocking manner and efficiently handles
 * multiple tasks.
 * It provides better error handling and makes the code more readable by avoiding deep nesting.
 */