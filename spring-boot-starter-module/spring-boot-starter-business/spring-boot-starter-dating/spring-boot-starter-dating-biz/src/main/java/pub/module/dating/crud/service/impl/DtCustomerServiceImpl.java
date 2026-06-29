package pub.module.dating.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.model.po.BaseEntity;
import pub.module.dating.api.service.dto.CusCityResidenceOptionDTO;
import pub.module.dating.crud.entity.DtContact;
import pub.module.dating.crud.entity.DtContactApply;
import pub.module.dating.crud.entity.DtCusMatchmakerRel;
import pub.module.dating.crud.entity.DtCustomer;
import pub.module.dating.crud.entity.DtCustomerContactRecord;
import pub.module.dating.crud.entity.DtPreference;
import pub.module.dating.crud.entity.DtRecommended;
import pub.module.dating.crud.mapper.DtCustomerMapper;
import pub.module.dating.crud.service.DtContactApplyService;
import pub.module.dating.crud.service.DtContactService;
import pub.module.dating.crud.service.DtCusMatchmakerRelService;
import pub.module.dating.crud.service.DtCustomerContactRecordService;
import pub.module.dating.crud.service.DtCustomerService;
import pub.module.dating.crud.service.DtPreferenceService;
import pub.module.dating.crud.service.DtRecommendedService;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * 客户 Service
 */
@Slf4j
@Service
public class DtCustomerServiceImpl extends ServiceImpl<DtCustomerMapper, DtCustomer> implements DtCustomerService {

    @Resource
    private DtCustomerContactRecordService customerContactRecordService;

    @Resource
    private DtRecommendedService recommendedService;

    @Resource
    private DtPreferenceService preferenceService;

    @Resource
    private DtCusMatchmakerRelService cusMatchmakerRelService;

    @Resource
    private DtContactService contactService;

    @Resource
    private DtContactApplyService contactApplyService;

    private final String bizCode = "cusCode";

    public void setDefaultValue(DtCustomer entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField, "CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null
                || StrUtil.isEmpty(ReflectUtil.getFieldValue(entity, declaredField).toString())) {
            ReflectUtil.setFieldValue(entity, declaredField, "CUS" + IdUtil.getSnowflakeNextIdStr());
        }
        if (entity.getCusPoolStatusCode() == null) {
            entity.setCusPoolStatusCode(StatusCodeEnum.NO);
        }
        if (StrUtil.isNotEmpty(entity.getCusPhone())) {
            entity.setCusPhone(entity.getCusPhone().trim());
            Assert.isTrue(PhoneUtil.isMobile(entity.getCusPhone()), "手机号格式错误！");
        }
    }

    @Override
    public DtCustomer getByCode(String code) {
        return this.getBaseMapper().selectOne(
                new QueryWrapper<DtCustomer>().eq(StrUtil.toUnderlineCase(bizCode), code), false);
    }

    @Override
    public List<CusCityResidenceOptionDTO> listDistinctResidenceCities() {
        return getBaseMapper().listDistinctResidenceCities();
    }

    @Override
    @Transactional
    public boolean save(DtCustomer entity) {
        Object code = ReflectUtil.getFieldValue(entity, bizCode);
        if (code != null && StrUtil.isNotEmpty(code.toString())) {
            Assert.isNull(this.getByCode(code.toString()), "编码已存在");
        }
        long exPhone = this.count(new QueryWrapper<DtCustomer>().lambda().eq(DtCustomer::getCusPhone, entity.getCusPhone()));
        Assert.isTrue(exPhone == 0, "手机号已存在！");
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<DtCustomer> entityList) {
        for (DtCustomer entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        DtCustomer entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtCustomer 不存在");
        this.getBaseMapper().deleteById(id);
        this.removeRelatedData(entity);
        return true;
    }

    /**
     * 删除客户时清理其关联数据：联系记录、推荐、喜欢（偏好）、关注（客户红娘关系）、好友（联系人及申请）。
     */
    private void removeRelatedData(DtCustomer entity) {
        String cusCode = entity.getCusCode();
        String cusUserCode = entity.getCusUserCode();
        if (StrUtil.isEmpty(cusCode)) {
            return;
        }

        // 联系记录
        customerContactRecordService.remove(new QueryWrapper<DtCustomerContactRecord>().lambda()
                .eq(DtCustomerContactRecord::getCusCode, cusCode));

        // 推荐：客户作为被推荐对象（cusCode），或客户作为浏览者（userCode）
        LambdaQueryWrapper<DtRecommended> recommendedWrapper = Wrappers.lambdaQuery(DtRecommended.class)
                .eq(DtRecommended::getCusCode, cusCode);
        if (StrUtil.isNotEmpty(cusUserCode)) {
            recommendedWrapper.or().eq(DtRecommended::getUserCode, cusUserCode);
        }
        recommendedService.remove(recommendedWrapper);

        // 喜欢（偏好）：客户作为发起方或目标方
        preferenceService.remove(Wrappers.lambdaQuery(DtPreference.class)
                .eq(DtPreference::getPreferenceCusCode, cusCode)
                .or().eq(DtPreference::getPreferenceTargetCusCode, cusCode));

        // 关注（客户红娘关系）
        cusMatchmakerRelService.remove(Wrappers.lambdaQuery(DtCusMatchmakerRel.class)
                .eq(DtCusMatchmakerRel::getCusCode, cusCode));

        // 好友（联系人）：客户作为拥有者（userCode）或作为对方联系人（cusCode / cusUserCode）
        LambdaQueryWrapper<DtContact> contactWrapper = Wrappers.lambdaQuery(DtContact.class)
                .eq(DtContact::getCusCode, cusCode);
        if (StrUtil.isNotEmpty(cusUserCode)) {
            contactWrapper.or().eq(DtContact::getUserCode, cusUserCode)
                    .or().eq(DtContact::getCusUserCode, cusUserCode);
        }
        contactService.remove(contactWrapper);

        // 好友申请：客户作为被申请人（userCode / cusCode / cusUserCode）或申请人（appCusCode）
        LambdaQueryWrapper<DtContactApply> contactApplyWrapper = Wrappers.lambdaQuery(DtContactApply.class)
                .eq(DtContactApply::getCusCode, cusCode)
                .or().eq(DtContactApply::getAppCusCode, cusCode);
        if (StrUtil.isNotEmpty(cusUserCode)) {
            contactApplyWrapper.or().eq(DtContactApply::getUserCode, cusUserCode)
                    .or().eq(DtContactApply::getCusUserCode, cusUserCode);
        }
        contactApplyService.remove(contactApplyWrapper);
    }

    @Transactional
    @Override
    public boolean removeByIds(Collection<?> ids) {
        Assert.notEmpty(ids, "主键集合不能为空");
        ids.forEach(entity -> this.removeById((Serializable) entity));
        return true;
    }

    @Override
    @Transactional
    public boolean updateById(DtCustomer entity) {
        BaseEntity target = this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString());
        Assert.isFalse(target != null && !target.getId().equals(entity.getId()), "编码已存在");
        this.getBaseMapper().updateById(entity);
        return true;
    }

    @Override
    public DtCustomer getById(Serializable id) {
        DtCustomer entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtCustomer 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public DtCustomer getOne(Wrapper<DtCustomer> queryWrapper, boolean throwEx) {
        DtCustomer entity = null;
        List<DtCustomer> list = this.list(queryWrapper);
        if (!list.isEmpty()) {
            entity = list.get(0);
            this.setDefaultValue(entity);
        }
        if (throwEx && list.size() > 1) {
            throw new IllegalArgumentException("查询条件有误，查询到多条数据");
        }
        return entity;
    }
}
