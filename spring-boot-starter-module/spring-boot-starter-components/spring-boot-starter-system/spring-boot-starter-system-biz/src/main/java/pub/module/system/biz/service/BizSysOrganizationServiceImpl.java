package pub.module.system.biz.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.system.api.constants.SysOrgCategoryCodeEnum;
import pub.module.system.api.service.BizSysOrganizationService;
import pub.module.system.api.service.dto.OrganizationDTO;
import pub.module.system.curd.entity.SysOrganization;
import pub.module.system.curd.service.SysOrganizationService;

import java.util.List;

/**
 * 组织机构业务 Service 实现
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Service
public class BizSysOrganizationServiceImpl implements BizSysOrganizationService {

    @Resource
    SysOrganizationService sysOrganizationService;

    @Override
    public void setTree(OrganizationDTO organizationDTO) {
        QueryWrapper<SysOrganization> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysOrganization::getOrgParentCode, organizationDTO.getOrgCode());
        List<SysOrganization> list = sysOrganizationService.list(queryWrapper);
        List<OrganizationDTO> organizationDTOList = BeanUtil.copyToList(list, OrganizationDTO.class);
        organizationDTO.setChildren(organizationDTOList);
        for (OrganizationDTO item : organizationDTOList) {
            this.setTree(item);
        }
    }
    @Override
    public OrganizationDTO getByCode(String sysOrgCode) {
        SysOrganization sysOrganization = sysOrganizationService.getOne(new QueryWrapper<SysOrganization>().lambda().eq(SysOrganization::getOrgCode, sysOrgCode));
        OrganizationDTO result = BeanUtil.copyProperties(sysOrganization, OrganizationDTO.class);
        this.setTree(result);
        return result;
    }

    /**
     * Lists root companies and builds their organizational trees
     */
    @Override
    public List<OrganizationDTO> listRootCompany() {
        List<SysOrganization> list = sysOrganizationService.list(new QueryWrapper<SysOrganization>().lambda().isNull(SysOrganization::getOrgParentCode).eq(SysOrganization::getOrgCategoryCode, SysOrgCategoryCodeEnum.COM.getCode()));
        List<OrganizationDTO> result = BeanUtil.copyToList(list, OrganizationDTO.class);
        for (OrganizationDTO item : result) {
            this.setTree(item);
        }
        return result;
    }
}
