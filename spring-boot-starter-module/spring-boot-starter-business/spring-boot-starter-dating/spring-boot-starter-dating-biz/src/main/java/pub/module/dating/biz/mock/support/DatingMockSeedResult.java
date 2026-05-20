package pub.module.dating.biz.mock.support;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DatingMockSeedResult {
    private String companyCode;
    private String companyName;
    private int matchmakerCount;
    private int maleCustomerCount;
    private int femaleCustomerCount;
    private int relationCount;
    private List<String> matchmakerPhones = new ArrayList<>();
    private List<String> customerPhones = new ArrayList<>();
    private String message;
}
