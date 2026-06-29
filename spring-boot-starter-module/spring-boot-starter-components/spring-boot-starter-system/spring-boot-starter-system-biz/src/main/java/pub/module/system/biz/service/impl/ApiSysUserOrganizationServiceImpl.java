package pub.module.system.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.system.api.service.ApiSysUserOrganizationService;
import pub.module.system.api.service.dto.OrganizationDTO;
import pub.module.system.api.service.dto.UserOrgRoleDTO;
import pub.module.system.crud.entity.SysOrganization;
import pub.module.system.crud.entity.SysUser;
import pub.module.system.crud.entity.SysUserOrganization;
import pub.module.system.crud.service.SysOrganizationService;
import pub.module.system.crud.service.SysUserOrganizationService;
import pub.module.system.crud.service.SysUserService;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ApiSysUserOrganizationServiceImpl implements ApiSysUserOrganizationService {
    @Resource
    SysUserOrganizationService sysUserOrganizationService;
    @Resource
    SysOrganizationService sysOrganizationService;
    @Resource
    SysUserService sysUserService;

    @Override
    public List<String> getUserCodes(String orgCode) {
        QueryWrapper<SysUserOrganization> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserOrganization::getOrgCode, orgCode);
        List<SysUserOrganization> list = sysUserOrganizationService.list(queryWrapper);
        return new ArrayList<>(list.stream().map(SysUserOrganization::getUserCode).toList());
    }

    @Override
    public List<String> getOrgCodes(String userCode) {
        Set<String> orgCodes = new LinkedHashSet<>(getValidOrgCodes(userCode));
        SysUser sysUser = sysUserService.getByCode(userCode);
        if (sysUser != null && StrUtil.isNotBlank(sysUser.getUserOrgCode())) {
            orgCodes.add(sysUser.getUserOrgCode().trim());
        }
        return new ArrayList<>(orgCodes);
    }

    @Override
    public List<String> getSysOrganizationNameByUserCode(String userCode) {
        return new ArrayList<>(getSysOrganizationByUserCode(userCode).stream().map(SysOrganization::getOrgName).toList());
    }

    @Override
    public List<OrganizationDTO> listOrganizationsByUserCode(String userCode) {
        return BeanUtil.copyToList(getSysOrganizationByUserCode(userCode), OrganizationDTO.class);
    }

    public List<SysOrganization> getSysOrganizationByUserCode(String userCode) {
        Assert.notEmpty(userCode, "用户编码不能为空！");
        List<String> orgCodes = getOrgCodes(userCode);
        if (orgCodes.isEmpty()) {
            return List.of();
        }
        return sysOrganizationService.list(new QueryWrapper<SysOrganization>().lambda()
                .in(SysOrganization::getOrgCode, orgCodes)
                .orderByAsc(SysOrganization::getSeqNo)
                .orderByAsc(SysOrganization::getCreateTime));
    }

    private List<SysUserOrganization> listUserOrganizations(String userCode) {
        return sysUserOrganizationService.list(new QueryWrapper<SysUserOrganization>().lambda()
                .eq(SysUserOrganization::getUserCode, userCode)
                .orderByAsc(SysUserOrganization::getSeqNo)
                .orderByAsc(SysUserOrganization::getCreateTime));
    }

    @Override
    public List<String> getValidOrgCodes(String userCode) {
        Assert.notEmpty(userCode, "用户编码不能为空！");
        Set<String> orgCodes = new LinkedHashSet<>();
        for (SysUserOrganization relation : listUserOrganizations(userCode)) {
            if (StrUtil.isNotBlank(relation.getOrgCode())) {
                orgCodes.add(relation.getOrgCode().trim());
            }
        }
        return new ArrayList<>(orgCodes);
    }

    @Override
    public String getFirstOrgCode(String userCode) {
        List<String> orgCodes = getValidOrgCodes(userCode);
        return orgCodes.isEmpty() ? null : orgCodes.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String ensureUserOrgCode(String userCode) {
        Assert.notEmpty(userCode, "用户编码不能为空！");
        SysUser sysUser = sysUserService.getByCode(userCode);
        Assert.notNull(sysUser, "用户不存在");
        String currentOrgCode = sysUser.getUserOrgCode();
        if (StrUtil.isNotBlank(currentOrgCode)) {
            return currentOrgCode.trim();
        }
        List<String> switchableOrgCodes = getValidOrgCodes(userCode);
        if (switchableOrgCodes.isEmpty()) {
            return null;
        }
        String defaultOrgCode = switchableOrgCodes.get(0);
        sysUser.setUserOrgCode(defaultOrgCode);
        sysUserService.updateById(sysUser);
        return defaultOrgCode;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveUserOrganizations(String userCode, List<UserOrgRoleDTO> items) {
        Assert.notEmpty(userCode, "用户编码不能为空");
        sysUserOrganizationService.remove(new QueryWrapper<SysUserOrganization>().lambda().eq(SysUserOrganization::getUserCode, userCode));
        List<String> validOrgCodes = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        if (items != null) {
            for (UserOrgRoleDTO item : items) {
                if (item == null || StrUtil.isBlank(item.getOrgCode()) || StrUtil.isBlank(item.getRoleCode())) {
                    continue;
                }
                String orgCode = item.getOrgCode().trim();
                String roleCode = item.getRoleCode().trim();
                String uniqueKey = orgCode + "|" + roleCode;
                if (!seen.add(uniqueKey)) {
                    continue;
                }
                SysUserOrganization relation = new SysUserOrganization();
                relation.setUserCode(userCode);
                relation.setOrgCode(orgCode);
                relation.setRoleCode(roleCode);
                sysUserOrganizationService.save(relation);
                if (!validOrgCodes.contains(orgCode)) {
                    validOrgCodes.add(orgCode);
                }
            }
        }
        SysUser sysUser = sysUserService.getByCode(userCode);
        Assert.notNull(sysUser, "用户不存在");
        String pickedUserOrgCode = pickUserOrgCode(validOrgCodes);
        if (pickedUserOrgCode == null && StrUtil.isNotBlank(sysUser.getUserOrgCode())) {
            pickedUserOrgCode = sysUser.getUserOrgCode().trim();
        }
        sysUser.setUserOrgCode(pickedUserOrgCode);
        sysUserService.updateById(sysUser);
    }

    @Override
    public List<String> getRoleCodesByOrgCodeAndUserCode(String orgCode, String userCode) {
        if (StrUtil.isBlank(orgCode) || StrUtil.isBlank(userCode)) {
            return List.of();
        }
        List<SysUserOrganization> list = sysUserOrganizationService.list(new QueryWrapper<SysUserOrganization>().lambda()
                .eq(SysUserOrganization::getUserCode, userCode.trim())
                .eq(SysUserOrganization::getOrgCode, orgCode.trim()));
        return list.stream()
                .map(SysUserOrganization::getRoleCode)
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .toList();
    }

    /**
     * 写入 user_org_code：优先选子级部门，否则取所选第一个。
     */
    private String pickUserOrgCode(List<String> orgCodes) {
        if (orgCodes == null || orgCodes.isEmpty()) {
            return null;
        }
        for (String orgCode : orgCodes) {
            SysOrganization organization = sysOrganizationService.getByCode(orgCode);
            if (organization != null && StrUtil.isNotBlank(organization.getOrgParentCode())) {
                return orgCode;
            }
        }
        return orgCodes.get(0);
    }
}
