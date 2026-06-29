package pub.module.system.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.system.crud.entity.SysUserTag;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户标签 Service
 */
public interface SysUserTagService extends IService<SysUserTag> {

    /**
     * 按业务编码查询
     */
    SysUserTag getByCode(String userTagCode);

    /**
     * 按用户编码查询标签列表
     */
    List<SysUserTag> listByUserCode(String userCode);

    /**
     * 批量按用户编码查询标签名称，返回 userCode -> 标签名称列表
     */
    Map<String, List<String>> mapTagNamesByUserCodes(Collection<String> userCodes);

    /**
     * 为用户新增一个标签（按 userCode + tagCode 去重，已存在则直接返回）
     */
    SysUserTag addTag(String userCode, String tagCode, String tagName);

    /**
     * 判断用户是否拥有给定标签编码中的任意一个
     */
    boolean hasAnyTagCode(String userCode, Collection<String> tagCodes);
}
