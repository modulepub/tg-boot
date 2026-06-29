package pub.module.common.security.session;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录会话快照：权限以登录时写入为准，鉴权 Filter 只读此对象，不查库。
 */
@Data
public class AuthSessionSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userCode;

    private List<String> authorities = new ArrayList<>();

    private long expireAtEpochMillis;
}
