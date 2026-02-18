package pub.module.system.biz.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.system.api.service.ApiSysUserOrganizationService;
import pub.module.system.curd.entity.SysOrganization;
import pub.module.system.curd.entity.SysUserOrganization;
import pub.module.system.curd.service.SysOrganizationService;
import pub.module.system.curd.service.SysUserOrganizationService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApiSysUserOrganizationServiceImpl implements ApiSysUserOrganizationService {
    @Resource
    SysUserOrganizationService sysUserOrganizationService;
    @Resource
    SysOrganizationService sysOrganizationService;

    @Override
    public List<String> getUserCodes(String orgCode) {
        QueryWrapper<SysUserOrganization> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserOrganization::getOrgCode, orgCode);
        List<SysUserOrganization> list = sysUserOrganizationService.list(queryWrapper);
        return new ArrayList<>(list.stream().map(SysUserOrganization::getUserCode).toList());
    }

    @Override
    public List<String> getOrgCodes(String userCode) {
        return new ArrayList<>(getSysOrganizationByUserCode(userCode).stream().map(SysOrganization::getOrgCode).toList());
    }

    @Override
    public List<String> getSysOrganizationNameByUserCode(String userCode) {
        return new ArrayList<>(getSysOrganizationByUserCode(userCode).stream().map(SysOrganization::getOrgName).toList());
    }

    public List<SysOrganization> getSysOrganizationByUserCode(String userCode) {
        Assert.notEmpty(userCode, "用户编码不能为空！");
        String inSql = "select org_code from sys_user_organization where deleted=0 and user_code = '${userCode}'";
        inSql = inSql.replace("${userCode}", userCode);
        return sysOrganizationService.list(new QueryWrapper<SysOrganization>().lambda().inSql(SysOrganization::getOrgCode, inSql));
    }

    @Transactional
    @Override
    public void saveOrgCodes(List<String> orgCodes, String userCode) {
        sysUserOrganizationService.remove(new QueryWrapper<SysUserOrganization>().lambda().eq(SysUserOrganization::getUserCode, userCode));
        for (String orgCode : orgCodes) {
            SysUserOrganization sysUserOrganization = new SysUserOrganization();
            sysUserOrganization.setUserCode(userCode);
            sysUserOrganization.setOrgCode(orgCode);
            sysUserOrganizationService.save(sysUserOrganization);
        }
    }
}
