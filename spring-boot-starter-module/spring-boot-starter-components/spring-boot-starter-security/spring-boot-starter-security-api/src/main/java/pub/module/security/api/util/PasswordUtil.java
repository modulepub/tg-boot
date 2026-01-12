package pub.module.security.api.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.SecureUtil;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.KeyPair;

/**
 * 密码工具类
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
public class PasswordUtil {

    public static String hashPassword(String password,String salt) {
        return BCrypt.hashpw(password, salt);
    }

    public static String genSalt() {
        return BCrypt.gensalt();
    }

    public static boolean matches(String password1,String password2) {
        Assert.notNull(password1, "password1 cannot be null");
        Assert.notNull(password2, "password2 cannot be null");
        return password1.equals(password2);
    }



    public static String decryptPassword(String encryptPassword){
        String privateStr = "308193020100301306072a8648ce3d020106082a811ccf5501822d047930770201010420ce3d71a9afdabd0551aaf6a8dc0b4d0a4142a72b3b4d06c9b0ba00b84f1c7b56a00a06082a811ccf5501822da14403420004d3341fcb5689cddc6068605bdf943e03a0678a3ab38fb8491922e60de46364ebd282c9bbc3a9d938c8914ed56c1db2b4de84cb34d7f57ed527e80f0fd7e577f2";
        String publicStr = "3059301306072a8648ce3d020106082a811ccf5501822d03420004d3341fcb5689cddc6068605bdf943e03a0678a3ab38fb8491922e60de46364ebd282c9bbc3a9d938c8914ed56c1db2b4de84cb34d7f57ed527e80f0fd7e577f2";
        SM2 sm = new SM2(privateStr, publicStr);
        return sm.decryptStr(encryptPassword, KeyType.PrivateKey);
    }
    public static void main(String[] generate){
        KeyPair keyPairObj = SecureUtil.generateKeyPair("SM2");
        String privateKeyHex2 = HexUtil.encodeHexStr(keyPairObj.getPrivate().getEncoded());
        String publicKeyHex2 = HexUtil.encodeHexStr(keyPairObj.getPublic().getEncoded());
        System.out.println(privateKeyHex2);
        System.out.println(publicKeyHex2);
    }
}
