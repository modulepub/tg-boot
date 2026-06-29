package pub.module.system.crud.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.system.crud.entity.SysUserTag;
import pub.module.system.crud.mapper.SysUserTagMapper;
import pub.module.system.crud.service.SysUserTagService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysUserTagServiceImpl extends ServiceImpl<SysUserTagMapper, SysUserTag>
        implements SysUserTagService {

    @Override
    public boolean save(SysUserTag entity) {
        if (StrUtil.isBlank(entity.getUserTagCode())) {
            entity.setUserTagCode(IdUtil.getSnowflakeNextIdStr());
        }
        return super.save(entity);
    }

    @Override
    public SysUserTag getByCode(String userTagCode) {
        if (StrUtil.isBlank(userTagCode)) {
            return null;
        }
        return getOne(new QueryWrapper<SysUserTag>().lambda()
                .eq(SysUserTag::getUserTagCode, userTagCode.trim()), false);
    }

    @Override
    public List<SysUserTag> listByUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return Collections.emptyList();
        }
        return list(new QueryWrapper<SysUserTag>().lambda()
                .eq(SysUserTag::getUserCode, userCode.trim())
                .orderByAsc(SysUserTag::getCreateTime));
    }

    @Override
    public Map<String, List<String>> mapTagNamesByUserCodes(Collection<String> userCodes) {
        if (userCodes == null || userCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SysUserTag> tags = list(new QueryWrapper<SysUserTag>().lambda()
                .in(SysUserTag::getUserCode, userCodes)
                .orderByAsc(SysUserTag::getCreateTime));
        return tags.stream()
                .filter(tag -> StrUtil.isNotBlank(tag.getUserCode()) && StrUtil.isNotBlank(tag.getTagName()))
                .collect(Collectors.groupingBy(
                        SysUserTag::getUserCode,
                        Collectors.mapping(SysUserTag::getTagName, Collectors.toList())));
    }

    @Override
    public SysUserTag addTag(String userCode, String tagCode, String tagName) {
        if (StrUtil.hasBlank(userCode, tagCode, tagName)) {
            throw new IllegalArgumentException("用户编码、标签编码与标签名称不能为空");
        }
        String trimmedUserCode = userCode.trim();
        String trimmedTagCode = tagCode.trim();
        String trimmedTagName = tagName.trim();
        SysUserTag existing = getOne(new QueryWrapper<SysUserTag>().lambda()
                .eq(SysUserTag::getUserCode, trimmedUserCode)
                .eq(SysUserTag::getTagCode, trimmedTagCode), false);
        if (existing != null) {
            return existing;
        }
        SysUserTag entity = new SysUserTag();
        entity.setUserCode(trimmedUserCode);
        entity.setTagCode(trimmedTagCode);
        entity.setTagName(trimmedTagName);
        save(entity);
        return entity;
    }

    @Override
    public boolean hasAnyTagCode(String userCode, Collection<String> tagCodes) {
        if (StrUtil.isBlank(userCode) || tagCodes == null || tagCodes.isEmpty()) {
            return false;
        }
        List<String> normalizedCodes = tagCodes.stream()
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toList());
        if (normalizedCodes.isEmpty()) {
            return false;
        }
        return count(new QueryWrapper<SysUserTag>().lambda()
                .eq(SysUserTag::getUserCode, userCode.trim())
                .in(SysUserTag::getTagCode, normalizedCodes)) > 0;
    }
}
