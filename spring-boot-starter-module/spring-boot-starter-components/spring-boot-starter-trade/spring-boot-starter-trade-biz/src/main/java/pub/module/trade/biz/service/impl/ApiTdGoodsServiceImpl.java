package pub.module.trade.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.trade.api.constants.TdGoodsBenefitKeyConstants;
import pub.module.trade.api.dto.TdGoodsBenefitDTO;
import pub.module.trade.api.dto.TdGoodsDTO;
import pub.module.trade.api.dto.TdGoodsMemberBenefitDeltaDTO;
import pub.module.trade.api.service.ApiTdGoodsService;
import pub.module.trade.crud.entity.TdGoods;
import pub.module.trade.crud.entity.TdGoodsBenefit;
import pub.module.trade.crud.service.ITdGoodsBenefitService;
import pub.module.trade.crud.service.ITdGoodsService;

import java.util.Collections;
import java.util.List;

/**
 * 商品业务实现
 *
 * @author tg
 * @since 2026-05-04
 */
@Service
public class ApiTdGoodsServiceImpl implements ApiTdGoodsService {

    @Resource
    private ITdGoodsService tdGoodsService;

    @Resource
    private ITdGoodsBenefitService tdGoodsBenefitService;

    private void saveBenefitListFromDto(String tdGdCode, List<TdGoodsBenefitDTO> benefitList) {
        if (benefitList == null) {
            return;
        }
        List<TdGoodsBenefit> entities = BeanUtil.copyToList(benefitList, TdGoodsBenefit.class);
        tdGoodsBenefitService.replaceByTdGdCode(tdGdCode, entities);
    }

    @Override
    public String addGoods(TdGoodsDTO dto) {
        Assert.notNull(dto, "TdGoodsDTO 不能为空");
        TdGoods entity = BeanUtil.copyProperties(dto, TdGoods.class);
        entity.setBenefitList(null);
        tdGoodsService.save(entity);
        saveBenefitListFromDto(entity.getTdGdCode(), dto.getBenefitList());
        return entity.getTdGdCode();
    }

    @Transactional
    @Override
    public List<TdGoodsDTO> listByTdGdSysUserCode(String tdGdSysUserCode) {
        Assert.isTrue(StrUtil.isNotBlank(tdGdSysUserCode), "tdGdSysUserCode 不能为空");
        List<TdGoods> list = tdGoodsService.lambdaQuery()
                .eq(TdGoods::getTdGdSysUserCode, tdGdSysUserCode)
                .orderByAsc(TdGoods::getSeqNo)
                .list();
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(list, TdGoodsDTO.class);
    }

    @Override
    public boolean existsByTdGdCode(String tdGdCode) {
        Assert.isTrue(StrUtil.isNotBlank(tdGdCode), "tdGdCode 不能为空");
        return tdGoodsService.lambdaQuery()
                .eq(TdGoods::getTdGdCode, tdGdCode.trim())
                .count() > 0;
    }

    @Override
    public TdGoodsDTO getByTdGdSysUserCodeAndCgyCode(String tdGdSysUserCode, String tdGdCgyCode) {
        Assert.isTrue(StrUtil.isNotBlank(tdGdSysUserCode), "tdGdSysUserCode 不能为空");
        Assert.isTrue(StrUtil.isNotBlank(tdGdCgyCode), "tdGdCgyCode 不能为空");
        TdGoods entity = tdGoodsService.lambdaQuery()
                .eq(TdGoods::getTdGdSysUserCode, tdGdSysUserCode.trim())
                .eq(TdGoods::getTdGdCgyCode, tdGdCgyCode.trim())
                .one();
        if (entity == null) {
            return null;
        }
        return BeanUtil.copyProperties(entity, TdGoodsDTO.class);
    }

    @Override
    public void updateGoods(TdGoodsDTO dto) {
        Assert.notNull(dto, "TdGoodsDTO 不能为空");
        Assert.isTrue(StrUtil.isNotBlank(dto.getId()), "商品 id 不能为空");
        TdGoods entity = BeanUtil.copyProperties(dto, TdGoods.class);
        entity.setBenefitList(null);
        tdGoodsService.updateById(entity);
        if (dto.getBenefitList() != null) {
            saveBenefitListFromDto(entity.getTdGdCode(), dto.getBenefitList());
        }
    }

    @Override
    public List<TdGoodsBenefitDTO> listBenefitsByTdGdCode(String tdGdCode) {
        Assert.isTrue(StrUtil.isNotBlank(tdGdCode), "tdGdCode 不能为空");
        List<TdGoodsBenefit> list = tdGoodsBenefitService.listByTdGdCode(tdGdCode.trim());
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(list, TdGoodsBenefitDTO.class);
    }

    @Override
    public TdGoodsMemberBenefitDeltaDTO resolveMemberBenefitDelta(String tdGdCode) {
        return TdGoodsBenefitKeyConstants.resolveMemberBenefitDelta(listBenefitsByTdGdCode(tdGdCode));
    }

    @Override
    public int removeTestGoods() {
        List<TdGoods> list = tdGoodsService.lambdaQuery()
                .eq(TdGoods::getTdGdTestStatusCode, StatusCodeEnum.YES.getCode())
                .list();
        if (list.isEmpty()) {
            return 0;
        }
        tdGoodsService.removeByIds(list.stream().map(TdGoods::getId).toList());
        return list.size();
    }
}
