package pub.module.im.biz.ry.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import io.rong.models.response.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.im.api.constants.ImGroupTypeCodeEnum;
import pub.module.im.api.service.BizCsrService;
import pub.module.im.api.service.BizImGroupService;
import pub.module.im.api.service.BizImSysNoticeService;
import pub.module.im.biz.ry.service.BizRyService;
import pub.module.im.curd.entity.ImGroup;
import pub.module.system.api.service.dto.UserDTO;

import jakarta.annotation.Resource;

@Slf4j
@Service
public class BizCsrServiceImpl implements BizCsrService {
    @Resource
    BizRyService bizRyService;
    @Resource
    BizImGroupService bizImGroupService;
    @Resource
    BizImSysNoticeService bizImSysNoticeService;


    @SneakyThrows
    @Override
    public void initSysUser(UserDTO sysUser) {
        JSONObject robotConfig  = new JSONObject();
        JSONObject noticeConfig  = new JSONObject();
        String nickName = "用户${suffix}".replace("${suffix}", RandomUtil.randomNumbers(4));
        String avatar = "http://files.yingxingshuzi.top/hhsc/temp/20251014101210005/AgAABlwsypdA2_o_06BN64k4hJn1_ywj.png";
        if(StrUtil.isNotEmpty(sysUser.getUserAvatar())){
            avatar = sysUser.getUserAvatar();
        }
        TokenResult tokenResult = bizRyService.getRyToken(sysUser.getUserCode(), nickName, avatar);
        String userCode = sysUser.getUserCode();
        ImGroup sr1Group = bizImGroupService.initCsrGroup(userCode, ImGroupTypeCodeEnum.SR1);
        ImGroup sr3Group = bizImGroupService.initCsrGroup(userCode, ImGroupTypeCodeEnum.SR3);
        ImGroup sr2Group = bizImGroupService.initCsrGroup(userCode, ImGroupTypeCodeEnum.SR2);
        ImGroup sr4Group = bizImGroupService.initCsrGroup(userCode, ImGroupTypeCodeEnum.SR4);
        ImGroup sr5Group = bizImGroupService.initCsrGroup(userCode, ImGroupTypeCodeEnum.SR5);
        bizImGroupService.addGroupMember(sr1Group, robotConfig.getStr("userCode"), "Hi~,我是{name}，很高兴为您服务！".replace("{name}", robotConfig.getStr("sysUserNickName")));
        bizImGroupService.addGroupMember(sr2Group, robotConfig.getStr("userCode"), "Hi~,我是{name}，很高兴为您服务！".replace("{name}", robotConfig.getStr("sysUserNickName")));
        bizImGroupService.addGroupMember(sr3Group, robotConfig.getStr("userCode"), "Hi~,我是{name}，很高兴为您服务！".replace("{name}", robotConfig.getStr("sysUserNickName")));
        bizImGroupService.addGroupMember(sr4Group, robotConfig.getStr("userCode"), "Hi~,我是{name}，很高兴为您服务！".replace("{name}", robotConfig.getStr("sysUserNickName")));
        bizImGroupService.addGroupMember(sr5Group, robotConfig.getStr("userCode"), "Hi~,我是{name}，很高兴为您服务！".replace("{name}", robotConfig.getStr("sysUserNickName")));
        bizImSysNoticeService.sendNotice(noticeConfig.getStr("userCode"), new String[]{userCode},"系统通知", "", "系统通知", "", "");
    }
}
