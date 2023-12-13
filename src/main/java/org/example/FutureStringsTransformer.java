package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FutureStringsTransformer {
    private List<String> data;

    public FutureStringsTransformer(List<String> startingData) {
        this.data = new ArrayList<>(startingData);
    }

    public List<String> transform(List<StringFunction> functions) throws ExecutionException, InterruptedException {
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();

        for (StringFunction function : functions) {
            CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() ->
              data.stream()
                .map(function::transform)
                .collect(Collectors.toList())
            );
            futures.add(future);
        }

        CompletableFuture<Void> allDone = CompletableFuture.allOf(
          futures.toArray(new CompletableFuture[0])
        );
        allDone.get(); // 等待所有轉換完成

        // 處理每個階段的結果，這裡僅使用最後一個轉換的結果
        return futures.get(futures.size() - 1).get();
    }

    public static interface StringFunction {
        String transform(String str);
    }
}

/**
 * 使用 Futures 和 CompletableFuture 是處理並行任務的另一種有效方式。
 * 這些工具來自 Java 的 java.util.concurrent 套件，提供了豐富的API來處理異步計算。
 *
 * 在您的 StringsTransformer 類別中，我們可以使用 CompletableFuture 來獨立執行每個 StringFunction 轉換，
 * 並在所有轉換完成後收集最終結果。
 *
 * 在這個實現中，我們對於每個 StringFunction 都創建了一個 CompletableFuture。
 * 這些 CompletableFuture 對象會異步地執行轉換，並且可以利用 allOf 方法來等待所有轉換完成。
 * 最後，我們從最後一個 CompletableFuture 中獲取最終結果。
 *
 * 請注意，在這個實現中，我們只是簡單地使用了每個轉換的最後結果。
 * 如果您希望以不同的方式組合這些結果，您可能需要進行相應的調整。
 *
 * 此外，由於 CompletableFuture.supplyAsync 方法在沒有指定的情況下會使用 ForkJoinPool.commonPool()，
 * 您可能需要根據您的應用程序的需求考慮自定義執行器（Executor）。
 */