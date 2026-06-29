package pub.module.affines.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.affines.api.service.ApiAfChildProfileService;
import pub.module.affines.api.service.ApiAfChildProfileViewService;
import pub.module.affines.api.service.dto.AfChildProfileDTO;
import pub.module.affines.api.service.dto.AfChildProfileViewDTO;
import pub.module.affines.crud.entity.AfChildProfile;
import pub.module.affines.crud.entity.AfChildProfileView;
import pub.module.affines.crud.service.AfChildProfileService;
import pub.module.affines.crud.service.AfChildProfileViewService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiAfChildProfileViewServiceImpl implements ApiAfChildProfileViewService {

    @Resource
    private AfChildProfileViewService afChildProfileViewService;
    @Resource
    private AfChildProfileService afChildProfileService;
    @Resource
    private ApiAfChildProfileService apiAfChildProfileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordView(String viewerUserCode, String afChildProfileCode) {
        Assert.notBlank(viewerUserCode, "浏览者用户编码不能为空");
        Assert.notBlank(afChildProfileCode, "资料卡编码不能为空");
        AfChildProfile profile = afChildProfileService.getByCode(afChildProfileCode.trim());
        Assert.notNull(profile, "资料卡不存在");
        AfChildProfileView view = new AfChildProfileView();
        view.setAfChildProfileCode(afChildProfileCode.trim());
        view.setAfViewerUserCode(viewerUserCode.trim());
        afChildProfileViewService.save(view);
    }

    @Override
    public List<AfChildProfileViewDTO> listMyViews(String viewerUserCode) {
        Assert.notBlank(viewerUserCode, "浏览者用户编码不能为空");
        List<AfChildProfileView> views = afChildProfileViewService.lambdaQuery()
                .eq(AfChildProfileView::getAfViewerUserCode, viewerUserCode.trim())
                .orderByDesc(AfChildProfileView::getCreateTime)
                .list();
        return views.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AfChildProfileViewDTO> listViewsByProfileCode(String afChildProfileCode) {
        Assert.notBlank(afChildProfileCode, "资料卡编码不能为空");
        List<AfChildProfileView> views = afChildProfileViewService.lambdaQuery()
                .eq(AfChildProfileView::getAfChildProfileCode, afChildProfileCode.trim())
                .orderByDesc(AfChildProfileView::getCreateTime)
                .list();
        return views.stream().map(this::toDto).collect(Collectors.toList());
    }

    private AfChildProfileViewDTO toDto(AfChildProfileView view) {
        AfChildProfileViewDTO dto = BeanUtil.copyProperties(view, AfChildProfileViewDTO.class);
        if (StrUtil.isNotBlank(view.getAfChildProfileCode())) {
            AfChildProfileDTO profile = apiAfChildProfileService.getDetailByCode(view.getAfChildProfileCode());
            dto.setProfile(profile);
        }
        return dto;
    }
}
