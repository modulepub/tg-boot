package pub.module.dating.biz.mock.support;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * mock 素材目录（与当前类同级的 红娘/男生/女生/红娘婚介公司 文件夹）。
 */
public final class DatingMockPaths {

    public static final String DIR_COMPANY = "红娘婚介公司";
    public static final String DIR_MATCHMAKER = "红娘";
    public static final String DIR_MALE = "男生";
    public static final String DIR_FEMALE = "女生";

    public static final String UPLOAD_BIZ = "dating-mock";

    /** 红娘手机号段：199881xxxxx（11 位，须用 long） */
    public static final long PHONE_MK_BASE = 19988100001L;
    /** 男客户 */
    public static final long PHONE_MALE_BASE = 19988200001L;
    /** 女客户 */
    public static final long PHONE_FEMALE_BASE = 19988300001L;

    private DatingMockPaths() {
    }

    public static Path resolveRoot(String override) {
        if (override != null && !override.isBlank()) {
            Path p = Paths.get(override.trim());
            if (Files.isDirectory(p)) {
                return p.toAbsolutePath().normalize();
            }
            throw new IllegalStateException("mock 目录不存在: " + p);
        }
        URL marker = DatingMockPaths.class.getResource(DIR_MATCHMAKER + "/");
        if (marker != null && "file".equals(marker.getProtocol())) {
            try {
                return Paths.get(marker.toURI()).getParent();
            } catch (Exception ignored) {
                // fall through
            }
        }
        Path dev = Paths.get(System.getProperty("user.dir"))
                .resolve("spring-boot-starter-module/spring-boot-starter-business/spring-boot-starter-dating/spring-boot-starter-dating-biz/src/main/java/pub/module/dating/biz/mock");
        if (Files.isDirectory(dev)) {
            return dev.toAbsolutePath().normalize();
        }
        Path dev2 = Paths.get(System.getProperty("user.dir"))
                .resolve("tg-boot/spring-boot-starter-module/spring-boot-starter-business/spring-boot-starter-dating/spring-boot-starter-dating-biz/src/main/java/pub/module/dating/biz/mock");
        if (Files.isDirectory(dev2)) {
            return dev2.toAbsolutePath().normalize();
        }
        throw new IllegalStateException("未找到 mock 素材目录，请通过参数 mockRoot 指定绝对路径");
    }
}
