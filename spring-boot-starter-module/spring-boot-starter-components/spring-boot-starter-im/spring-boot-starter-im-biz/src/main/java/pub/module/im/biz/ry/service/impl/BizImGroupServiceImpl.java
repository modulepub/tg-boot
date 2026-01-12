package pub.module.im.biz.ry.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.rong.RongCloud;
import io.rong.messages.TxtMessage;
import io.rong.models.group.GroupMember;
import io.rong.models.group.GroupModel;
import io.rong.models.message.GroupMessage;
import io.rong.models.response.MessageResult;
import io.rong.models.response.OperationGroupResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.im.api.constants.ImGroupTypeCodeEnum;
import pub.module.im.api.service.BizImGroupService;
import pub.module.im.biz.ry.service.BizRyService;
import pub.module.im.curd.entity.ImGroup;
import pub.module.im.curd.entity.ImGroupMember;
import pub.module.im.curd.service.IImGroupMemberService;
import pub.module.im.curd.service.IImGroupService;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class BizImGroupServiceImpl implements BizImGroupService {
    @Resource
    BizRyService bizRyService;
    @Resource
    IImGroupService imGroupService;
    @Resource
    IImGroupMemberService imGroupMemberService;


    /**
     * 初始化对客户【服务中心】群组
     *
     * @param userCode         用户编码
     * @param imGroupTypeCodeEnum 客服位
     */
    @Override
    public ImGroup initCsrGroup(String userCode, ImGroupTypeCodeEnum imGroupTypeCodeEnum) throws Exception {
        ImGroup imGroup = imGroupService.getOne(new QueryWrapper<ImGroup>().lambda()
                        .eq(ImGroup::getImGroupBelongSysUserCode, userCode)
                        .eq(ImGroup::getImGroupTypeCode, imGroupTypeCodeEnum.getCode())
                , false);

        if (imGroup == null) {
            imGroup = new ImGroup();
            imGroup.setImGroupBelongSysUserCode(userCode);
            imGroup.setImGroupTypeCode(imGroupTypeCodeEnum.getCode());
            imGroup.setImGroupName(imGroupTypeCodeEnum.getName());
            imGroup.setImGroupHeadImg("http://files.yingxingshuzi.top/hhsc/static/kf.png");
            imGroupService.save(imGroup);
            this.initRyGroup(imGroup);
        }
        return imGroup;
    }

    public void initRyGroup(ImGroup imGroup) throws Exception {
        RongCloud rongCloud = bizRyService.getRongCloud();
        GroupModel groupModel = new GroupModel();
        groupModel.setId(imGroup.getImGroupCode());
        groupModel.setName(imGroup.getImGroupName());
        groupModel.setFromUserId(imGroup.getImGroupBelongSysUserCode());
        GroupMember groupMember = new GroupMember();
        groupMember.setId(imGroup.getImGroupBelongSysUserCode());
        groupMember.setGroupId(imGroup.getImGroupCode());
        ImGroupMember imGroupMember = new ImGroupMember();
        imGroupMember.setImGroupCode(imGroup.getImGroupCode());
        imGroupMember.setUserCode(imGroup.getImGroupBelongSysUserCode());
        imGroupMemberService.save(imGroupMember);
        groupModel.setMembers(List.of(groupMember).toArray(new GroupMember[0]));
        OperationGroupResult groupResult = rongCloud.group.create(groupModel);
        log.info("创建群组接口返回：{}", groupResult);
        Assert.notNull(groupResult, "groupResult is null");
        Assert.isTrue(groupResult.getCode() == 200, groupResult.getErrorMessage());

    }


    public void addGroupMember(ImGroup imGroup, String userCode, String message) {
        Assert.notEmpty(userCode, "addGroupMember userCode is null");
        Assert.notNull(imGroup, "addGroupMember imGroup is null");
        Assert.notEmpty(imGroup.getId(), "addGroupMember imGroup.id is null");
        RongCloud rongCloud = bizRyService.getRongCloud();
        GroupModel groupModel = new GroupModel();
        groupModel.setId(imGroup.getImGroupCode());
        long exCount = imGroupMemberService.count(new QueryWrapper<ImGroupMember>().lambda().eq(ImGroupMember::getUserCode, userCode).eq(ImGroupMember::getImGroupCode, imGroup.getImGroupCode()));
        if (exCount == 0) {
            try {
                GroupMember groupMember = new GroupMember();
                groupMember.setId(userCode);
                groupMember.setGroupId(imGroup.getImGroupCode());
                groupModel.setMembers(ArrayUtil.toArray(List.of(groupMember), GroupMember.class));
                groupModel.setName(imGroup.getImGroupName());
                OperationGroupResult operationGroupResult = rongCloud.group.join(groupModel);
                log.info("添加群组用户返回:{}", operationGroupResult);
                Assert.notNull(operationGroupResult, "operationGroupResult is null");
                Assert.isTrue(operationGroupResult.getCode() == 200, "operationGroupResult.code is not 200");
                ImGroupMember imGroupMember = new ImGroupMember();
                imGroupMember.setImGroupCode(imGroup.getImGroupCode());
                imGroupMember.setUserCode(userCode);
                imGroupMemberService.save(imGroupMember);
                if (StrUtil.isNotEmpty(message)) {
                    GroupMessage groupMessage = new GroupMessage();
                    groupMessage.setTargetId(Collections.singletonList(imGroup.getImGroupCode()).toArray(new String[0]));
                    groupMessage.setSenderId(userCode);
                    TxtMessage txtMessage = new TxtMessage(message, "");
                    groupMessage.setObjectName(txtMessage.getType());
                    groupMessage.setContent(txtMessage);
                    MessageResult messageResult = rongCloud.message.group.send(groupMessage);
                    log.info("发送群消息返回:{}", messageResult);
                }

            } catch (Exception e) {
                log.error("addGroupUser：{}", e.getMessage());
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 获取空闲客服
     * @param userCode 用户编码
     * @return 客服群组
     */
    @Override
    public ImGroup getKxGroup(String userCode) {
        QueryWrapper<ImGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ImGroup::getImGroupBelongSysUserCode, userCode);
        queryWrapper.lambda().eq(ImGroup::getImGroupTypeCode,ImGroupTypeCodeEnum.SR1.getCode());
        ImGroup result = imGroupService.getOne(queryWrapper,false);
        log.info("获取到空闲的群组{}",result);
        return result;
    }

}
