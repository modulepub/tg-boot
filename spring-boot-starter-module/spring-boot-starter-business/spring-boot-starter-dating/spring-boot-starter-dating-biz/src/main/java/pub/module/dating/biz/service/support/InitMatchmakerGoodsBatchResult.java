package pub.module.dating.biz.service.support;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InitMatchmakerGoodsBatchResult {
    private int total;
    private int successCount;
    private int failedCount;
    private List<String> failedLabels = new ArrayList<>();
    private String message;
}
