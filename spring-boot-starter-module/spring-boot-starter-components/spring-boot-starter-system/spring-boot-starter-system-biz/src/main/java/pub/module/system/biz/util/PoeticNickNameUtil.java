package pub.module.system.biz.util;

import cn.hutool.core.util.RandomUtil;

/**
 * 注册时随机生成的诗意昵称（从词库中抽取，可组合扩展）。
 */
public final class PoeticNickNameUtil {

    private static final String[] NICKS = {
            "清月入怀", "星河醉客", "吹雪听风", "半卷诗笺", "墨染青山",
            "一池春水", "听雨小楼", "晚舟归渡", "云卷云舒", "琴声绕梁",
            "梦里江南", "竹影摇窗", "松风入梦", "浅笑安然", "疏影横斜",
            "暗香盈袖", "海棠依旧", "白露为霜", "秋水长天", "落霞孤鹜",
            "春江花月", "青山隐隐", "绿水悠悠", "烟波江上", "柳岸闻莺",
            "杏花微雨", "桃李春风", "月色如水", "星河欲渡", "白云深处",
            "古道西风", "长河落日", "大漠孤烟", "蒹葭苍苍", "月下独酌",
            "春风十里", "长安落花", "江南烟雨", "眉山远黛", "折柳送行",
            "桃花依旧", "小楼听风", "烟雨平生", "浮生若梦", "山河入画",
            "清风徐来", "明月几时", "把酒临风", "江上清风", "山间明月",
            "疏影暗香", "雪落无声", "梅边吹笛", "兰舟催发", "醉卧花阴"
    };

    private PoeticNickNameUtil() {
    }

    public static String randomNickName() {
        return RandomUtil.randomEle(NICKS);
    }
}
