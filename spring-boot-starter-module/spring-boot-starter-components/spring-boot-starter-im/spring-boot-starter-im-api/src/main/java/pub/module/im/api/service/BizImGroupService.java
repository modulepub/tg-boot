package pub.module.im.api.service;

import pub.module.im.api.constants.ImGroupTypeCodeEnum;
import pub.module.im.curd.entity.ImGroup;

/**
 * 即时通讯群组
 * @author tg
 * @since 2025-10-05
 * @version V1.0
 */
public interface BizImGroupService  {
    /**
     * 初始化对客户【服务中心】群组
     * @param userCode 用户编码
     * @param imGroupTypeCodeEnum 客服位
     */
    ImGroup initCsrGroup(String userCode, ImGroupTypeCodeEnum imGroupTypeCodeEnum) throws Exception;
    /**
     * 添加群组成员
     * @param imGroup 群组
     * @param userCode 成员
     */
    void addGroupMember(ImGroup imGroup, String userCode,String message);

    /**
     * 获取空闲客服
     * @param userCode 用户编码
     * @return 客服群组
     */
    ImGroup getKxGroup(String userCode);
}
