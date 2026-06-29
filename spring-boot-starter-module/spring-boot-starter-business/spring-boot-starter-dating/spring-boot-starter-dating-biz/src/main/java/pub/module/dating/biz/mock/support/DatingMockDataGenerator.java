package pub.module.dating.biz.mock.support;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import pub.module.system.api.constants.UserSexCodeEnum;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 基于姓名种子生成稳定的模拟业务字段。
 */
public final class DatingMockDataGenerator {

    private static final List<CityPick> CITIES = List.of(
            new CityPick("CN-371100", "中国/山东省/日照市"),
            new CityPick("CN-370200", "中国/山东省/青岛市")
    );

    private static final List<EduPick> EDUCATIONS = List.of(
            new EduPick("bachelor", "本科"),
            new EduPick("master", "硕士"),
            new EduPick("college", "大专"),
            new EduPick("phd", "博士")
    );

    private static final List<String> FEMALE_NICKNAMES = loadLines("mock/female-nicknames.txt");
    private static final List<String> MALE_NICKNAMES = loadLines("mock/male-nicknames.txt");
    private static final List<String> FEMALE_MOMENTS = loadLines("mock/female-moments.txt");
    private static final List<String> MALE_MOMENTS = loadLines("mock/male-moments.txt");
    private static final List<String> MATCHMAKER_MOMENTS = loadLines("mock/matchmaker-moments.txt");

    private static final String[] MK_TAGS = {"资深红娘", "真诚服务", "本地资源", "高效匹配"};

    private DatingMockDataGenerator() {
    }

    public static Random seeded(String seed) {
        return new Random(seed.hashCode() & 0xfffffffL);
    }

    public static int age(Random r, int min, int max) {
        return min + r.nextInt(max - min + 1);
    }

    public static CityPick city(Random r) {
        return CITIES.get(r.nextInt(CITIES.size()));
    }

    public static EduPick education(Random r) {
        return EDUCATIONS.get(r.nextInt(EDUCATIONS.size()));
    }

    /** 按性别从预置昵称库 deterministic 选取（同一 seedKey 稳定）。 */
    public static String nickname(String seedKey, UserSexCodeEnum sex) {
        List<String> pool = sex == UserSexCodeEnum.MAN ? MALE_NICKNAMES : FEMALE_NICKNAMES;
        if (pool.isEmpty()) {
            return sex == UserSexCodeEnum.MAN ? "阳光少年" : "温柔少女";
        }
        String key = StrUtil.blankToDefault(seedKey, "mock");
        int idx = Math.floorMod(key.hashCode(), pool.size());
        return pool.get(idx);
    }

    /** 按性别从预置说说库随机选取。 */
    public static String moment(Random r, UserSexCodeEnum sex) {
        List<String> pool = sex == UserSexCodeEnum.MAN ? MALE_MOMENTS : FEMALE_MOMENTS;
        if (pool.isEmpty()) {
            return sex == UserSexCodeEnum.MAN
                    ? "踏实过日子，想认真找个对象。"
                    : "认真找对象，慢热但专一。";
        }
        return pool.get(r.nextInt(pool.size()));
    }

    /** 红娘展业说说，与客户个人简介区分。 */
    public static String matchmakerMoment(Random r) {
        if (MATCHMAKER_MOMENTS.isEmpty()) {
            return "深耕本地婚恋服务，一对一牵线搭桥，用专业和真诚帮大家遇见对的人。";
        }
        return MATCHMAKER_MOMENTS.get(r.nextInt(MATCHMAKER_MOMENTS.size()));
    }

    /** 红娘评分：4.5 ~ 5.0，保留一位小数。 */
    public static BigDecimal matchmakerScore(Random r) {
        double raw = 4.5 + r.nextDouble() * 0.5;
        return BigDecimal.valueOf(raw).setScale(1, RoundingMode.HALF_UP);
    }

    public static String matchmakerTags(Random r) {
        int n = 2 + r.nextInt(2);
        return String.join(",", RandomUtil.randomEleSet(List.of(MK_TAGS), n));
    }

    public static long heightCm(Random r, UserSexCodeEnum sex) {
        if (sex == UserSexCodeEnum.MAN) {
            return 168L + r.nextInt(18);
        }
        return 155L + r.nextInt(15);
    }

    public static long weightKg(Random r, UserSexCodeEnum sex) {
        if (sex == UserSexCodeEnum.MAN) {
            return 60L + r.nextInt(25);
        }
        return 45L + r.nextInt(20);
    }

    public static String mockIdNo(Random r, UserSexCodeEnum sex) {
        String prefix = sex == UserSexCodeEnum.MAN ? "1101011990" : "1101011992";
        return prefix + String.format("%04d", 1000 + r.nextInt(8999)) + String.format("%01d", r.nextInt(10));
    }

    private static List<String> loadLines(String classpath) {
        try (InputStream in = DatingMockDataGenerator.class.getClassLoader().getResourceAsStream(classpath)) {
            if (in == null) {
                throw new IllegalStateException("mock 资源缺失: " + classpath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                Set<String> unique = new LinkedHashSet<>();
                reader.lines()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .filter(s -> !s.matches(".*\\d+$"))
                        .forEach(unique::add);
                return List.copyOf(unique);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("加载 mock 资源失败: " + classpath, ex);
        }
    }

    public record CityPick(String code, String fullName) {
    }

    public record EduPick(String code, String name) {
    }
}
