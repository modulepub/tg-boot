package pub.module.dating.api.service;


import pub.module.dating.api.constants.CusMemberBenefitTypeEnum;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.dto.DtGuestPreviewRecommendDTO;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.api.service.dto.MemberBenefitConsumeResultDTO;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.system.api.service.dto.UserDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Api 客户 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
public interface ApiDtCustomerService  {

    void importData(Map<String,Object> data);

    /**
     * 按登录用户确保存在客户档案：若无则创建最小客户记录（幂等）。
     */
    void initCustomerByUser(UserDTO user);

    /**
     * 将系统用户昵称同步到绑定客户的 {@code cusNickName}（客户不存在时先幂等初始化）。
     */
    void syncCusNickNameFromUser(UserDTO user);

    /**
     * 将客户姓名 {@code cusName} 同步到系统用户 {@code userRealName}。
     */
    void syncUserRealNameFromCustomer(String userCode, String cusName);

    /**
     * 将客户昵称 {@code cusNickName} 同步到系统用户 {@code userNickName}。
     */
    void syncUserNickNameFromCustomer(String userCode, String cusNickName);

    /**
     * 将客户实名认证状态同步到系统用户 {@code userIdentityAuthenticatedStatusCode}。
     */
    void syncUserIdentityAuthenticatedFromCustomer(String userCode, StatusCodeEnum status);

    DtCustomerDTO getCusByUserCode(String userCode);
    List<DtCustomerDTO> listAll(List<String> notIn);

    DtCustomerDTO getCusByCusCode(String cusCode);

    List<DtCustomerDTO> findCustomer(List<String> notIn,DtCustomerDTO customerDto);

    /**
     * 免登录推荐预览（兼容旧接口）：仅按意向性别查询，按创建时间取前 {@code limit} 条。
     *
     * @param intentionSexCode 意向性别编码（1 男 / 2 女）
     * @param limit            最大条数
     */
    List<DtCustomerDTO> listGuestPreviewBySexCode(String intentionSexCode, int limit);

    /**
     * 免登录推荐预览：按访客意向匹配度排序，取前 {@code limit} 条。
     *
     * @param intention 访客填写的推荐意向（至少含意向性别）
     * @param limit     最大条数
     */
    List<DtGuestPreviewRecommendDTO> listGuestPreviewByIntention(DtIntentionDTO intention, int limit);
    /**
     * 按客户昵称 {@code cusNickName} 精确查询；未找到返回 {@code null}。
     */
    DtCustomerDTO getCusByNickNameExact(String cusNickName);

    /**
     * 按客户昵称 {@code cusNickName} 精确查询全部匹配记录（昵称可重复时返回多条）。
     */
    List<DtCustomerDTO> listCusByNickNameExact(String cusNickName);

    /** 批量按客户编码查询（联系人列表等回填展示字段） */
    List<DtCustomerDTO> listByCusCodes(Collection<String> cusCodes);

    /** 批量按系统用户编码查询（联系人申请列表回填申请人信息） */
    List<DtCustomerDTO> listByUserCodes(Collection<String> userCodes);

    /**
     * 用户端：按当前用户绑定客户更新字段；{@code patch} 中非空属性才会写入（前端传什么更新什么）。
     * 若传入 {@code cusCode} 则须与当前客户一致。
     */
    DtCustomerDTO updateCurrCustomerPartial(String userCode, DtCustomerDTO patch);

    /**
     * mock 测试数据导入：写入客户资料并同步 sys_user / IM，但不发布 {@code dating.profile.updated}（避免触发推荐与订阅通知）。
     */
    DtCustomerDTO updateMockCustomerPartial(String userCode, DtCustomerDTO patch);

    /**
     * 客户资料已写入库后，发布 {@code dating.profile.updated}，供 dating 等域同步冗余快照。
     * 管理端或其它非 {@link #updateCurrCustomerPartial} 写路径在更新展示字段后应调用。
     */
    void notifyCustomerProfileUpdated(String userCode);

    /**
     * 客户表资料字段更新后的同进程副作用（sys_user、IM）及 MQ 广播。
     */
    void afterCustomerProfileFieldsUpdated(String userCode);

    /**
     * 为用户累加每日权益次数上限（加好友 / 推荐 / 牵线）。用于支付成功回调、运营发放等可信链路；
     * 用户端 {@link #updateCurrCustomerPartial} 不允许直接改写这些字段。
     *
     * @param tdOdGdCode            订单商品明细编码；非空时写入充值记录表并按此幂等，已存在相同明细编码则不再充值
     * @param tdOdCode              订单编码，可空，仅写入记录便于查询
     * @param userCode              系统用户编码
     * @param addFriendRightDelta   添加好友权益增量，{@code null} 表示不调整该字段
     * @param recommendRightDelta   推荐权益增量，{@code null} 表示不调整该字段
     * @param matchRightDelta       牵线权益增量，{@code null} 表示不调整该字段
     */
    void rechargeMemberBenefits(String tdOdGdCode, String tdOdCode, String userCode, Long addFriendRightDelta, Long recommendRightDelta, Long matchRightDelta);

    /**
     * 会员套餐（vip 类目）支付成功后开通会员：写入会员类型/到期日，并按商品权益配置累加三类每日次数上限。
     * 到期日 = 当前时间 + 商品服务期（天），不叠加原到期日。
     * 权益增量按商品权益子表配置（key：addFriendNum / recNum / matchNum），未配置时默认 10。
     *
     * @param tdOdGdCode       订单商品明细编码（幂等键）
     * @param tdOdCode         订单编码
     * @param userCode         系统用户编码
     * @param memberTypeCode   会员类型编码（商品编码 tdGdCode）
     * @param memberTypeName   会员类型名称（商品名称 tdGdName）
     * @param serviceDayPeriod 商品服务期（天）；null 或 &lt;=0 时按 365 天
     */
    void activateMemberSubscription(String tdOdGdCode, String tdOdCode, String userCode,
                                    String memberTypeCode, String memberTypeName, Integer serviceDayPeriod);

    /**
     * 原子扣减添加好友权益；余额不足时返回 {@code false}。
     * @deprecated 请使用 {@link #tryConsumeMemberBenefit(String, CusMemberBenefitTypeEnum, long, String)}
     */
    @Deprecated
    boolean tryDeductAddFriendRight(String userCode, long amount);

    /**
     * 校验当日权益是否超限并写入消费记录（不扣减客户字段，仅统计 consume 表）。
     *
     * @param userCode  系统用户编码
     * @param type      权益类型
     * @param amount    消费次数，须为正数
     * @param bizRef    业务关联编码，可空
     */
    MemberBenefitConsumeResultDTO tryConsumeMemberBenefit(String userCode, CusMemberBenefitTypeEnum type,
                                                          long amount, String bizRef);

    /**
     * 回填客户 DTO 中当日权益已用次数（添加好友、推荐、牵线申请），供会员页展示。
     */
    void enrichMemberBenefitDayUsage(DtCustomerDTO customer, String userCode);

    /**
     * 回填资料完整度（0-100），供用户端「我的」页展示。
     */
    void enrichProfileCompletenessRate(DtCustomerDTO customer);

    /**
     * 手机号二要素核验通过（阿里云 BizCode=1）后，更新绑定客户的实名状态与姓名。
     *
     * @param userCode 系统用户编码
     * @param phone    核验手机号
     * @param realName 核验姓名
     * @return 是否已更新客户表
     */
    boolean applyIdentityAfterPhoneTwoFactorVerify(String userCode, String phone, String realName);

    /**
     * 资产认证审核通过后，回写客户爱与诚相关认证字段，并点亮 {@code cusLsStatusCode}；
     * 随后发布 {@code dating.profile.updated}，供婚恋等域同步冗余快照（含爱与诚灯牌）。
     */
    void applyAssetCertApproved(String cusCode, String vehicleLicensePhoto, String realEstateCertificatePhoto);

}
