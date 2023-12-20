package org.jess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 準備初始數據
        List<String> data = Arrays.asList("Hello", "World", "Java", "Concurrency");
        StreamStringsTransformer transformer = new StreamStringsTransformer(data);

        // 定義轉換函數
        List<StreamStringsTransformer.StringFunction> functions = new ArrayList<>();
        functions.add(str -> str.toUpperCase()); // 將字符串轉為大寫
        functions.add(str -> str + "!");
        functions.add(str -> str + "!");
        functions.add(str -> str + "##"); // 在字符串後添加驚嘆號

        // 執行轉換
        try {
            List<String> result = transformer.transform(functions);
            System.out.println(result);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}

/**
 * 詳細解釋：
 * 1.使用 synchronized 或鎖定機制：
 * 適用於線程安全是首要考慮的場景，但可能會影響性能，因為線程可能需要等待鎖的釋放。
 *
 * 2. 使用 ExecutorService：
 *提供了更好的線程管理和性能優化，適合需要高度控制並行處理的複雜任務。
 *
 * 3. 使用 Java 8 的 Stream API：
 * 適用於輕量級並行任務和數據流處理，代碼簡潔易讀，但在錯誤處理和複雜的並行流程控制方面有限。
 *
 * 4. 使用 Futures 和 CompletableFuture：
 * 適合處理異步任務和複雜的工作流程，提供了豐富的API來處理異步結果和錯誤，
 * 但可能需要更多的代碼來管理和協調這些異步任務。
 */