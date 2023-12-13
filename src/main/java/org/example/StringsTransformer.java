package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Jimmy was tasked with writing a class that takes a base list of strings
 * and a series of transformations and applies them, returning the end result.
 * To better utilize all available resources, the solution was done in a multi-threaded fashion.
 * Explain the problems with this solution, and offer 2 alternatives.
 * Discuss the advantages of each approach.
 */

  import java.util.*;
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
      throws InterruptedException {
        List<Thread> threads = new ArrayList<Thread>();

        for (final StringFunction f : functions) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    forEach(f);
                }}));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        return data;
    }
    public static interface StringFunction {
        public String transform(String str);
    }
}


/**
 *
 * 1. syntax error: forEach(StringFunction function 少 )
 *
 * 2. 多線程資料競爭（Race Condition）：
 * 您的 forEach 方法在每個線程中都會修改共享的 data 成員。
 * 這會導致資料競爭問題，因為多個線程可能同時嘗試讀取和修改這個共享資源。
 *
 * 3. 不同步的資料更新：
 * 在多個線程中同時修改 data 變數可能會導致某些更改被覆蓋，因此某些轉換可能不會在最終結果中反映。
 * 線程管理不當：
 *
 * 4. Thread management：
 * transform 方法中創建了線程，但沒有啟動它們（缺少 start() 調用）。
 * 此外，即使它們被啟動，也沒有確保它們在返回結果之前全部完成（join() 方法在沒有啟動的線程上沒有效果）。
 */

/**
 * Thread Safety:
 * The data field is accessed and modified by multiple threads,
 * potentially leading to race conditions and inconsistent states.
 *
 * Efficiency:
 * Spawning a new thread for each transformation might be inefficient
 * due to context switching and overhead, especially if the number of transformations is large.
 *
 * Two Alternatives:
 *
 * Executor Service: Use an ExecutorService to manage a pool of threads.
 * This can improve efficiency by reusing threads and reducing overhead.
 * It also provides better control over concurrency and task scheduling.
 *
 * Futures and CompletableFutures: Utilize Future or CompletableFuture for asynchronous
 * computation.
 * This allows transformations to be applied in a non-blocking manner and efficiently handles
 * multiple tasks.
 * It provides better error handling and makes the code more readable by avoiding deep nesting.
 */

/**
 * 在Jimmy的案例中，他負責撰寫一個類別，用於對字符串列表執行一系列變換，並以多線程方式實現，但這種方法存在一些問題：
 *
 * 線程安全性：多線程訪問和修改共享數據（如data列表）可能導致競爭條件和數據不一致。
 *
 * 效率問題：為每個轉換任務創建一個新線程可能會導致過度的線程創建和切換開銷，特別是當轉換任務數量很多時。
 *
 * 兩種替代方案：
 *
 * 使用執行器服務（Executor Service）：通過管理一個線程池，來重用線程並降低開銷。這可以提高效率，並提供更好的並發控制和任務調度。
 *
 * 利用Futures和CompletableFutures：使用Future或CompletableFuture進行異步計算。這允許以非阻塞方式應用轉換，並有效處理多個任務。它提供了更好的錯誤處理，並使代碼更易於閱讀。
 */




