package pub.module.dating.biz.mock.support;

import cn.hutool.core.util.RandomUtil;
import pub.module.system.api.constants.UserSexCodeEnum;

import java.util.List;
import java.util.Random;

/**
 * 基于姓名种子生成稳定的模拟业务字段。
 */
public final class DatingMockDataGenerator {

    private static final List<CityPick> CITIES = List.of(
            new CityPick("CN-510100", "中国/四川省/成都市"),
            new CityPick("CN-110000", "中国/北京市"),
            new CityPick("CN-310100", "中国/上海市"),
            new CityPick("CN-440100", "中国/广东省/广州市"),
            new CityPick("CN-440300", "中国/广东省/深圳市"),
            new CityPick("CN-330100", "中国/浙江省/杭州市")
    );

    private static final List<EduPick> EDUCATIONS = List.of(
            new EduPick("bachelor", "本科"),
            new EduPick("master", "硕士"),
            new EduPick("college", "大专"),
            new EduPick("phd", "博士")
    );

    private static final String[] MOMENTS = {
            "认真找对象，希望遇见三观相合的人。",
            "性格温和，喜欢旅行和美食，期待真诚相遇。",
            "工作稳定，家庭和睦，愿以结婚为目的交往。",
            "热爱生活，相信缘分，期待一起经营小日子。"
    };

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

    public static String moment(Random r) {
        return MOMENTS[r.nextInt(MOMENTS.length)];
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

    public record CityPick(String code, String fullName) {
    }

    public record EduPick(String code, String name) {
    }
}
