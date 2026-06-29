package pub.module.dating.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.CusMemberTierConstants;
import pub.module.dating.api.constants.DatingTradeGoodsCategoryEnum;
import pub.module.dating.biz.service.support.InitMatchmakerGoodsBatchResult;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.trade.api.dto.TdGoodsDTO;
import pub.module.trade.api.dto.TdGoodsBenefitDTO;
import pub.module.trade.api.constants.TdGoodsBenefitKeyConstants;
import pub.module.trade.api.service.ApiTdGoodsService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class InitGoodsService {

    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.7500");
    private static final BigDecimal DEFAULT_MEMBER_COMMISSION_RATE = new BigDecimal("0.0500");
    private static final BigDecimal PLATFORM_INVENTORY = new BigDecimal("99999");
    private static final String MEMBER_TIER_CGY_CODE = "vip";
    private static final String MEMBER_TIER_CGY_NAME = "会员套餐";

    @Resource
    private ApiTdGoodsService apiTdGoodsService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Lazy
    @Resource
    private InitGoodsService self;

    @Transactional
    public List<TdGoodsDTO> initByMk(DtMatchmaker dtMatchmaker) {
        List<TdGoodsDTO> goodsDTOList = new ArrayList<>();
        goodsDTOList.add(upsertMatchmakerGoods(dtMatchmaker, buildTopTemplate(dtMatchmaker)));
        goodsDTOList.add(upsertMatchmakerGoods(dtMatchmaker, buildMatchSuccessTemplate(dtMatchmaker)));
        goodsDTOList.add(upsertMatchmakerGoods(dtMatchmaker, buildMarrySuccessTemplate(dtMatchmaker)));
        return goodsDTOList;
    }

    /**
     * 为全部已认证红娘初始化/更新默认服务商品（逐人调用 {@link #initByMk}，单人失败不影响其余）。
     */
    public InitMatchmakerGoodsBatchResult initAllCertifiedMatchmakers() {
        QueryWrapper<DtMatchmaker> wrapper = new QueryWrapper<>();
        wrapper.eq("mk_identity_status_code", StatusCodeEnum.YES.getCode());
        List<DtMatchmaker> matchmakers = dtMatchmakerService.list(wrapper);

        InitMatchmakerGoodsBatchResult result = new InitMatchmakerGoodsBatchResult();
        result.setTotal(matchmakers.size());
        for (DtMatchmaker matchmaker : matchmakers) {
            try {
                self.initByMk(matchmaker);
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                result.setFailedCount(result.getFailedCount() + 1);
                String label = matchmaker.getMkName() != null ? matchmaker.getMkName() : matchmaker.getMkCode();
                result.getFailedLabels().add(label);
                log.warn("init matchmaker goods failed, mkCode={}, mkUserCode={}",
                        matchmaker.getMkCode(), matchmaker.getMkUserCode(), e);
            }
        }
        result.setMessage(String.format("共 %d 位已认证红娘，成功 %d 位，失败 %d 位",
                result.getTotal(), result.getSuccessCount(), result.getFailedCount()));
        return result;
    }

    private TdGoodsDTO upsertMatchmakerGoods(DtMatchmaker dtMatchmaker, TdGoodsDTO template) {
        TdGoodsDTO existing = apiTdGoodsService.getByTdGdSysUserCodeAndCgyCode(
                dtMatchmaker.getMkUserCode(), template.getTdGdCgyCode());
        if (existing == null) {
            template.setTdGdSysUserCode(dtMatchmaker.getMkUserCode());
            template.setTdGdSysUserRealName(dtMatchmaker.getMkName());
            template.setTdGdSysUserPhone(dtMatchmaker.getMkPhone());
            String code = apiTdGoodsService.addGoods(template);
            template.setTdGdCode(code);
            return template;
        }
        existing.setSeqNo(template.getSeqNo());
        existing.setTdGdCgyName(template.getTdGdCgyName());
        existing.setTdGdName(template.getTdGdName());
        existing.setTdGdDescription(template.getTdGdDescription());
        existing.setTdGdCommissionRate(template.getTdGdCommissionRate());
        existing.setTdGdInventoryNum(template.getTdGdInventoryNum());
        existing.setTdGdSysUserRealName(dtMatchmaker.getMkName());
        existing.setTdGdSysUserPhone(dtMatchmaker.getMkPhone());
        apiTdGoodsService.updateGoods(existing);
        return existing;
    }

    /**
     * 初始化平台会员套餐商品（钻石 / 黑钻 / 金钻），商品编码使用道具 ID，已存在则跳过。
     */
    @Transactional
    public List<TdGoodsDTO> initMemberGoodsIfAbsent() {
        List<TdGoodsDTO> created = new ArrayList<>();
        collectIfCreated(created, ensureMemberGoods("standardMember", "钻石会员", new BigDecimal("368"), 1L, 20, null, false));
        collectIfCreated(created, ensureMemberGoods("premiumMember", "黑钻会员", new BigDecimal("698"), 2L, 40, null, false));
        collectIfCreated(created, ensureMemberGoods("diamondMember", "金钻会员", new BigDecimal("6998"), 3L, 60, null, false));
        // 赠送会员：后台代客赠送的免费会员商品（价格 0，跳过付费流程），归属 vip 类目复用原会员开通逻辑；
        // 标记为隐藏，移动端不会展示，避免用户免费下单。
        collectIfCreated(created, ensureMemberGoods(
                CusMemberTierConstants.FREE_VIP, CusMemberTierConstants.FREE_VIP_NAME, BigDecimal.ZERO, 4L,
                CusMemberTierConstants.FREE_VIP_DAILY_QUOTA, CusMemberTierConstants.FREE_VIP_SERVICE_DAYS, true));
        return created;
    }

    private static String buildMemberGoodsDescription(int dailyQuota) {
        return "1、每日推荐次数增加至" + dailyQuota + "次\n"
                + "2、每日添加好友次数增加至" + dailyQuota + "次\n"
                + "3、每日牵线次数增加至" + dailyQuota + "次";
    }

    private static void collectIfCreated(List<TdGoodsDTO> created, TdGoodsDTO goods) {
        if (goods != null) {
            created.add(goods);
        }
    }

    private TdGoodsDTO ensureMemberGoods(String tdGdCode, String tdGdName, BigDecimal tdGdPrice, long seqNo, int dailyQuota,
                                         Integer dayPeriod, boolean hidden) {
        if (apiTdGoodsService.existsByTdGdCode(tdGdCode)) {
            return null;
        }
        TdGoodsDTO tdGoodsDTO = new TdGoodsDTO();
        tdGoodsDTO.setTdGdCode(tdGdCode);
        tdGoodsDTO.setSeqNo(seqNo);
        tdGoodsDTO.setTdGdCgyCode(MEMBER_TIER_CGY_CODE);
        tdGoodsDTO.setTdGdCgyName(MEMBER_TIER_CGY_NAME);
        tdGoodsDTO.setTdGdName(tdGdName);
        tdGoodsDTO.setTdGdDescription(buildMemberGoodsDescription(dailyQuota));
        tdGoodsDTO.setTdGdEnabledCode("1");
        tdGoodsDTO.setTdGdHiddenStatusCode(hidden ? StatusCodeEnum.YES.getCode() : StatusCodeEnum.NO.getCode());
        tdGoodsDTO.setTdGdInventoryNum(PLATFORM_INVENTORY);
        tdGoodsDTO.setTdGdPrice(tdGdPrice);
        tdGoodsDTO.setTdGdDayPeriod(dayPeriod);
        tdGoodsDTO.setTdGdCommissionRate(DEFAULT_MEMBER_COMMISSION_RATE);
        tdGoodsDTO.setBenefitList(buildMemberBenefitList(dailyQuota));
        apiTdGoodsService.addGoods(tdGoodsDTO);
        return tdGoodsDTO;
    }

    private static List<TdGoodsBenefitDTO> buildMemberBenefitList(int dailyQuota) {
        List<TdGoodsBenefitDTO> list = new ArrayList<>();
        list.add(buildBenefit(TdGoodsBenefitKeyConstants.ADD_FRIEND_NUM, (long) dailyQuota, "每日添加好友次数"));
        list.add(buildBenefit(TdGoodsBenefitKeyConstants.REC_NUM, (long) dailyQuota, "每日推荐次数"));
        list.add(buildBenefit(TdGoodsBenefitKeyConstants.MATCH_NUM, (long) dailyQuota, "每日牵线次数"));
        return list;
    }

    private static TdGoodsBenefitDTO buildBenefit(String key, long value, String desc) {
        TdGoodsBenefitDTO dto = new TdGoodsBenefitDTO();
        dto.setTdGdBnfKey(key);
        dto.setTdGdBnfValue(value);
        dto.setTdGdBnfDesc(desc);
        return dto;
    }

    private TdGoodsDTO buildMatchSuccessTemplate(DtMatchmaker dtMatchmaker) {
        TdGoodsDTO tdGoodsDTO = new TdGoodsDTO();
        tdGoodsDTO.setSeqNo(1L);
        tdGoodsDTO.setTdGdCgyCode(DatingTradeGoodsCategoryEnum.CONTRACT_MATCH_SUCCESS.getCode());
        tdGoodsDTO.setTdGdCgyName(DatingTradeGoodsCategoryEnum.CONTRACT_MATCH_SUCCESS.getDesc());
        tdGoodsDTO.setTdGdName("2年内安排线下约见24次");
        tdGoodsDTO.setTdGdDescription("服务期两年，红娘完成安排线下约见总次数或者客户达成恋爱关系，20次是在客户空窗期的情况，本服务协议约束红娘不能让客户的空窗期超过1个月，甲方有意保持的情况除外，甲方应当遵循真诚原则，诚实告知关系推进情况，积极响应红娘制定的破冰计划，乙方应当积极参加平台服务技能培训、提升专业素养、积极主动为客户提供专业服务、完成任务。特别说明：在任务未达标或者提前完成甲乙双方可以向平台提前主张清算，平台应当在5个工作日内完结，有争议的情况应当在20个工作日完结。本服务包含爱之诚价值提升服务、200次添加好友、200次牵线服务、200次推荐服务，价值同会员权益中心");
        tdGoodsDTO.setTdGdEnabledCode("1");
        tdGoodsDTO.setTdGdInventoryNum(new BigDecimal("100"));
        tdGoodsDTO.setTdGdPrice(new BigDecimal("5699"));
        tdGoodsDTO.setTdGdSysUserCode(dtMatchmaker.getMkUserCode());
        tdGoodsDTO.setTdGdSysUserRealName(dtMatchmaker.getMkName());
        tdGoodsDTO.setTdGdSysUserPhone(dtMatchmaker.getMkPhone());
        tdGoodsDTO.setTdGdCommissionRate(DEFAULT_COMMISSION_RATE);
        return tdGoodsDTO;
    }

    private TdGoodsDTO buildMarrySuccessTemplate(DtMatchmaker dtMatchmaker) {
        TdGoodsDTO tdGoodsDTO = new TdGoodsDTO();
        tdGoodsDTO.setSeqNo(3L);
        tdGoodsDTO.setTdGdCgyCode(DatingTradeGoodsCategoryEnum.CONTRACT_MARRY_SUCCESS.getCode());
        tdGoodsDTO.setTdGdCgyName(DatingTradeGoodsCategoryEnum.CONTRACT_MARRY_SUCCESS.getDesc());
        tdGoodsDTO.setTdGdName("一年内包结婚");
        tdGoodsDTO.setTdGdDescription("服务期为年，包含【2年内安排线下约见24次】服务内容，「结婚」以办理结婚登记或协议约定为准，甲方应当诚实配合乙方执行真诚计划，乙方应当主动积极为甲方提供一切有利于结婚的服务。特别说明：在任务未达标或者提前完成甲乙双方可以向平台提前主张清算，平台应当在5个工作日内完结，有争议的情况应当在20个工作日完结。");
        tdGoodsDTO.setTdGdEnabledCode("1");
        tdGoodsDTO.setTdGdInventoryNum(new BigDecimal("100"));
        tdGoodsDTO.setTdGdPrice(new BigDecimal("28888"));
        tdGoodsDTO.setTdGdSysUserCode(dtMatchmaker.getMkUserCode());
        tdGoodsDTO.setTdGdSysUserRealName(dtMatchmaker.getMkName());
        tdGoodsDTO.setTdGdSysUserPhone(dtMatchmaker.getMkPhone());
        tdGoodsDTO.setTdGdCommissionRate(DEFAULT_COMMISSION_RATE);
        return tdGoodsDTO;
    }

    private TdGoodsDTO buildTopTemplate(DtMatchmaker dtMatchmaker) {
        TdGoodsDTO tdGoodsDTO = new TdGoodsDTO();
        tdGoodsDTO.setSeqNo(0L);
        tdGoodsDTO.setTdGdCgyCode(DatingTradeGoodsCategoryEnum.CUS_ACCELERATED_PLAN_30_DAY.getCode());
        tdGoodsDTO.setTdGdCgyName(DatingTradeGoodsCategoryEnum.CUS_ACCELERATED_PLAN_30_DAY.getDesc());
        tdGoodsDTO.setTdGdName("爱之诚价值提升");
        tdGoodsDTO.setTdGdDescription("无服务期，含协助客户完成爱之诚认证，形象改造服务及恋爱指导服务，每日免费推荐次数提升至10人。本项目除了价值提升也是诚意体现，信任是解除陌生人底层防备心理快速进入谈恋爱阶段(破冰阶段)，实现婚介比自我建立关系高效的密码。婚姻关系的建立本质是价值互换、需求满足。我们要知道对方的人品、实力、是否健康等信息、是否，负责任的红娘需要在其中撮合双方实现以上过程，需要主动并尊重参与者任何一方提出要求，比如了解双方的过往行为，判断三观是否匹配，了解双方的物质条件了解对方的健康状况等。在双方所需信息都满足的情况，督促双方快速进入感情磨合期。在经过大量实践经验，平台指导红娘建立30天破冰计划：1-5天完成印象确认->10-15天完成破冰阶段（需要双方高度配合）->15天-30天建立恋爱关系，如若不成，主动帮雇主进入下段感情关系，因为不应该让恋爱浪费我们太多时间和精力。特别说明：本项目是婚介诚意与高效计划的同意，因此平台不做居间量化及该款项的退款裁决。");
        tdGoodsDTO.setTdGdEnabledCode("1");
        tdGoodsDTO.setTdGdInventoryNum(new BigDecimal("100"));
        tdGoodsDTO.setTdGdPrice(new BigDecimal("298"));
        tdGoodsDTO.setTdGdSysUserCode(dtMatchmaker.getMkUserCode());
        tdGoodsDTO.setTdGdSysUserRealName(dtMatchmaker.getMkName());
        tdGoodsDTO.setTdGdSysUserPhone(dtMatchmaker.getMkPhone());
        tdGoodsDTO.setTdGdCommissionRate(DEFAULT_COMMISSION_RATE);
        return tdGoodsDTO;
    }
}
