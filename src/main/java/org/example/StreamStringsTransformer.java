package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StreamStringsTransformer {
    private List<String> data;

    public StreamStringsTransformer(List<String> startingData) {
        this.data = new ArrayList<>(startingData);
    }

    public List<String> transform(List<StringFunction> functions) {
        for (StringFunction function : functions) {
            data = data.parallelStream()
              .map(function::transform)
              .collect(Collectors.toList());
        }

        return data;
    }

    public static interface StringFunction {
        String transform(String str);
    }


}

/**
 * 1. 簡化代碼和提高可讀性：使用 Stream API 可以減少代碼行數，使代碼更加清晰和易於理解。
 * 2. 自動化的並行處理：Stream API 能夠自動利用多核處理器的優勢進行並行處理，無需手動創建和管理線程。
 * 3. 減少錯誤和側效：由於 Stream API 是功能性的，它可以幫助減少共享變數和資料競爭的問題。
 *
 * 在這個版本中，我使用了 parallelStream() 方法來開啟並行處理，這樣每個轉換函數的應用都會在多個線程上執行。
 * 這種方法可以自動平衡負載並充分利用多核處理器，同時也避免了手動管理線程的複雜性。
 */