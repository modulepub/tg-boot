package pub.module.contract.biz.service;

import cn.hutool.core.lang.Assert;
import com.ancun.netsign.client.NetSignClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NetSignClientService {
    Map<String, NetSignClient> cache = new HashMap<>();

    /**
     * Retrieves or initializes NetSignClient for given appId; ensures nonempty appId
     */
    public NetSignClient getNetSignClient(String appId) {
        Assert.notEmpty(appId, "appId is not null");
        NetSignClient result = cache.get(appId);
        if (result == null) {
            final String url = "https://oapi.asign.cn/";
            String privateKey = "xx+9Lsvq6z3VWxT0xHwmw0LsVO4iqy+CTM6HYyjLVhqgO6RbQUBtBFJ1UQufqDchAx/KkL2W8ebU8jqNYYzhZvZ+Wsk5tKHyGRys5V3Tu6cMx6Uk8TKD3G3rgFEf2cuN7o64VCDxUAhXHA1QmlbFFFTJv2YzZr1BhzDji9i5KCse28V3/uL5kEYMtsHj1+ntK0GXnyaysNHpCViKyEXVcvBSWIgVHlnYoORoDOwMcGZhmGvYJPjktXdNa+fx3+fJm6pS9Ne+ZLn3tMl/ZRLFCsrUhQoa4E5pCUPmMsuLyTQcMcqkV77DAgMBAAECggEBAIbkGIxfKA5shG8N8mvwoIuxRf7CM+O5Lin67caPvJRP4b4tRP6qPZxtumePX9KQG1c2WbIWm+6FGbjCMxST2fnZEz0i28DWhd/vHjEX3LSKNeiZhI63mF31Qf6B4dlIQdTSSinkTlUmRVmywctFHQp3cX/+PCajGx0UV5pyQjkhzRvAvFTsMjmtFiEKMk3wdW8iDo86QwLj6TdmPQyyy+BuODE19EbW5GozSlc199VTUzbNP2Q0wnb/VK6pcyySfw+e/+n7Nj6U6ydZ2kHlATZwSPZ3TBDCyna5HW3SOFCjNq3ns9yeIUluKQxXIYUtg8Z6uodCHGD0RVNIRjpz+EECgYEAyee9k0gnoMkxv1+MhiOSWQBtTv7L4Wj9eBYpuvmLD+O+dwbkDzGmrNgSKWYxbS2DDdE/Y2vKBATzjBbQWIs9h08ZdzJWTuZruksmzqsZ4iI8VZl8Q/ItQKww+qR5Vz3YR0UhWvpgyBY6QOdN0gQYHSDyqTPO3K3Uo2UHmd/w4g8CgYEArSBD5740flnmIJKXhBMbxbcHOn2P6GTbys7v5WjI8dnF8bGoKZ1NcRTgtTSvffGR8XoRjGmY6ZvauyJkRps4zxzRCWf28/jVuRpw+Zc0Qwf+t5TfRtdvO05ejf9V/lLz200TB4IA+odGxTotIwlXk+1HJ+U8mYrrBOcQyKeTfA0CgYBEMKZQNdBcd1b74VNegmkM8h34zgkhqgnhIusZIOqchThwjr07fHNkcN1BaihLTVGzuvYDqAXkcZ+nspTPMsenCUn6fd+0ahH8KyGCLztgqltiNaJIeGRcMUuheycYFeUTvP9U8Lb7HLaz8OrRgVx7igMqrjqxQWxNevOkPoPmdQKBgFGqrb2s8NBFkhP9crHf3bL3stY5kt/6a+AQnAOo2VmU9BYeSwhOZtCYG6P/FWWgxcqWXZ7R1UsltRmfkJPp9vNkUYtRWqId2Ju3yGFEnWedZt+sv2KIj7T3NA1hzGvrL5NcmURqZBHKLG0vE0m6x3BFdtJwBZUcfd7TLQyqWB0lAoGBAKocx9m38sN90EK/3xNIREvxblUIYdbQLY9QySQzeBpn0q+At3tL0zcQgG1CwI6BZSl+zU+HFVJcMh9RmKKVbAJK3rQnD+Xrt0xt96YvAde2pWEBx634Pkhjf8rJoG3g6FZsBF0dPRD81CxACISWKOQAp5/4sH4TxAoCaOwYmX37";
            if ("730801641".equals(appId)) {
                privateKey = "xxxxdS6ja/G8//s398eSCRuM/3lSnerBYjXwDPN4fDqRmbqOT4gOBSIuReujnnZw0D8/38WKEmAE59Y3BgnN0KzwgLA/80EkDj9uQ/oRNpejpsK62xW5NFHwHgWO8pIm7Hy2bBbdfs87nDG0eOqtR+wjVaMx1CtBJ0BvCUbgb9papu45EkfiHYiGK4EGkeqeS3hSX/HAgMBAAECggEALgsP4L3rx5kDZ7w8k9rE01fr6FG3SsvgYv/aqVx4KN4ZRWnJ5LI4e/8dw+uWa6vYkXYmfV9YZqGDOo3wADGHCuXbRAH+A6EMKS3Frrcqu8H62GQeFuakqjc66Tpg6cGTLHxPBgtnX2w8Edwt7nbZDpm5AIRZF+B4ofmLd1U74G4aB/LsDoIHcayC7v3M4STXsOn4RWvRyF7ZImtlU1tWNUu7WZq1GBzi+dCakz4HSIfHdFm39qQOMpC5B8pM0uqa6FfPO4t1RTiUn0A5JMEs4pGOq0sV/mkVvhCuwpRrj8isMSDOjXI0XRmNZwOL/ChsrqhPKXBfLlPOizLdBVKnAQKBgQD1vXp0ne3lwbvaXeq4x2QxIV6ycVoWXdIr+eG+JZigtuR7vaE0sqHAIYQyAAiULUArWkYnyN7O8o5mxwqf2xVSYiN5qoHWDsCUBmSfkvU8NvhqTe0G1bXGK1MKSVO1BWeGVstgVlGvi5MxBvl1EpEJ23tkLMFI3BF0GCJaUSFglwKBgQDzAWSv0vPoppa4Hxqxe0w/Yo55sVVdDovUQuXy9ITrBmj+5Ax8UzrqffNBY0lzg3XbDYgK3dtEJ0mdsbr8CJW1hjISrfkdgPQhXTEMQo7FcZ9Da5EkGBMCSKLmAGn0Z3fvMHHrzcF/jM6GxinZPPTfgG4A+9d2R8wYJJjXM2OQUQKBgQDnvS68yK4vDDrF2+ldJZODjW+LIcAN9Z4coIJU2YmMjcz8MEDUvFwzHqhWqpdsfRvVhkVAgXo0eKL3o16Oj4EU9r1zn+re3XyYW0sK/QBNL52mSiRrXKAV6Idf3PSWcilFk5YpHF3eCObexi/7Sm4CeLttJZOEzRuceqHywC2UswKBgArQ5A9jX1HaoFi/oauGpmUn2NmXyAnlTNn8xEllEsl3NpMpyVh7R+MKChq99ZX7ovJGDukU6N6GtSMctwriwu8acv4b2juy8qrNgBJgoAQvw3v3vLUTkT0HyKBeIlkrhc4SpO9u8A7iUIr1JQWzTd7o03yduQBdv7rPXjIt6l+RAoGBAN/+g1z39QozxXd72JwBQ2uf4voqYml1Iz23pHUmaNGAEUYG8y70L3TgkRKwOBpNhzjAtSmTlY6Uubc5mwpX1Eo91VEZbENB9wfglnL/R6OwgAoIPeNLgZvOYvMMkw3fC/rwpkzVnkQbhI9TiHeQ/SJ/Mq1T5z/V6/Cy4Wth/gTN";
            }
            result = new NetSignClient(url, appId, privateKey);
        }
        return result;
    }
    public void cleanCache(){
        cache.clear();
    }
}