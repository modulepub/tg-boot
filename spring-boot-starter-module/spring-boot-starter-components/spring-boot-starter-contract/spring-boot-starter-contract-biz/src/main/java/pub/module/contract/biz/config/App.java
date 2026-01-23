package pub.module.contract.biz.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Data
public class App {
    private String appId;
    private List<String> urlList;
    private String privateKey;
}
