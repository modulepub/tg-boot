package pub.module.dating.biz.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.curd.entity.DtMatchmaker;
import pub.module.trade.api.dto.TdGoodsDTO;
import pub.module.trade.api.service.ApiTdGoodsService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class InitGoodsService {
    @Resource
    private ApiTdGoodsService apiTdGoodsService;

    @Transactional
    public List<TdGoodsDTO> initByMk(DtMatchmaker dtMatchmaker) {
        List<TdGoodsDTO> goodsDTOList = apiTdGoodsService.listByTdGdSysUserCode(dtMatchmaker.getMkUserCode());
        if (goodsDTOList.isEmpty()) {
            goodsDTOList = new ArrayList<>();
            goodsDTOList.add(this.initTop(dtMatchmaker));
            goodsDTOList.add(this.init1(dtMatchmaker));
            goodsDTOList.add(this.init2(dtMatchmaker));
            goodsDTOList.add(this.init3(dtMatchmaker));
        }
        return goodsDTOList;
    }

    TdGoodsDTO init1(DtMatchmaker dtMatchmaker) {
        TdGoodsDTO tdGoodsDTO = new TdGoodsDTO();
        tdGoodsDTO.setSeqNo(1L);
        tdGoodsDTO.setTdGdCgyCode("contractMatchSuccess");
        tdGoodsDTO.setTdGdCgyName("签约-牵线成功");
        tdGoodsDTO.setTdGdName("牵线成功10次");
        tdGoodsDTO.setTdGdDescription("「牵线成功」指红娘为您完成一次有效牵线对接（以双方确认或平台记录为准）。本项为 10 次额度包，用完即止；细则以正式协议为准。");
        tdGoodsDTO.setTdGdEnabledCode("1");
        tdGoodsDTO.setTdGdInventoryNum(new BigDecimal("100"));
        tdGoodsDTO.setTdGdPrice(new BigDecimal("1299"));
        tdGoodsDTO.setTdGdSysUserCode(dtMatchmaker.getMkUserCode());
        tdGoodsDTO.setTdGdSysUserRealName(dtMatchmaker.getMkName());
        tdGoodsDTO.setTdGdSysUserPhone(dtMatchmaker.getMkPhone());
        tdGoodsDTO.setTdGdCode(apiTdGoodsService.addGoods(tdGoodsDTO));
        return tdGoodsDTO;
    }
    TdGoodsDTO init2(DtMatchmaker dtMatchmaker) {
        TdGoodsDTO tdGoodsDTO = new TdGoodsDTO();
        tdGoodsDTO.setSeqNo(2L);
        tdGoodsDTO.setTdGdCgyCode("contractQsSuccess");
        tdGoodsDTO.setTdGdCgyName("半年内牵手成功1次");
        tdGoodsDTO.setTdGdName("半年内牵手成功1次");
        tdGoodsDTO.setTdGdDescription("服务期为半年。「牵手成功」以双方确立恋爱关系并经确认为准。期内不限牵线次数。若期满未达成约定结果：扣除已实际消耗的牵线费用后，剩余金额按规则退还。");
        tdGoodsDTO.setTdGdEnabledCode("1");
        tdGoodsDTO.setTdGdInventoryNum(new BigDecimal("100"));
        tdGoodsDTO.setTdGdPrice(new BigDecimal("6888"));
        tdGoodsDTO.setTdGdSysUserCode(dtMatchmaker.getMkUserCode());
        tdGoodsDTO.setTdGdSysUserRealName(dtMatchmaker.getMkName());
        tdGoodsDTO.setTdGdSysUserPhone(dtMatchmaker.getMkPhone());
        tdGoodsDTO.setTdGdCode(apiTdGoodsService.addGoods(tdGoodsDTO));
        return tdGoodsDTO;
    }
    TdGoodsDTO init3(DtMatchmaker dtMatchmaker) {
        TdGoodsDTO tdGoodsDTO = new TdGoodsDTO();
        tdGoodsDTO.setSeqNo(3L);
        tdGoodsDTO.setTdGdCgyCode("contractMarrySuccess");
        tdGoodsDTO.setTdGdCgyName("一年内包结婚");
        tdGoodsDTO.setTdGdName("一年内包结婚");
        tdGoodsDTO.setTdGdDescription("服务期为一年。「结婚」以办理结婚登记或协议约定为准。期内不限牵手次数。若未达成：扣除已消耗牵线费用后，剩余退还。另含恋爱指导、形象改造等，以交付清单为准。");
        tdGoodsDTO.setTdGdEnabledCode("1");
        tdGoodsDTO.setTdGdInventoryNum(new BigDecimal("100"));
        tdGoodsDTO.setTdGdPrice(new BigDecimal("28888"));
        tdGoodsDTO.setTdGdSysUserCode(dtMatchmaker.getMkUserCode());
        tdGoodsDTO.setTdGdSysUserRealName(dtMatchmaker.getMkName());
        tdGoodsDTO.setTdGdSysUserPhone(dtMatchmaker.getMkPhone());
        tdGoodsDTO.setTdGdCode(apiTdGoodsService.addGoods(tdGoodsDTO));
        return tdGoodsDTO;
    }

    TdGoodsDTO initTop(DtMatchmaker dtMatchmaker) {
        TdGoodsDTO tdGoodsDTO = new TdGoodsDTO();
        tdGoodsDTO.setSeqNo(0L);
        tdGoodsDTO.setTdGdCgyCode("cusAcceleratedPlan30Day");
        tdGoodsDTO.setTdGdCgyName("30天关系速成计划");
        tdGoodsDTO.setTdGdName("试婚服务");
        tdGoodsDTO.setTdGdDescription("与该红娘签约须同意适婚计划，该计划 要求客户的每个关系 见面-谈恋爱-试婚-牵手成功（稳定的情侣关系） 30天内需要拿到结果，避免一段低质量关系浪费客户时间，红娘在其中起督促作用，同时也代表客户认真找对象的诚意。该服务包含1次情侣（2人）体检服务（三甲医院价值698的健康体检服务）及1次牵线成功服务（红娘达不成，平台不予红娘结算佣金）！");
        tdGoodsDTO.setTdGdEnabledCode("1");
        tdGoodsDTO.setTdGdInventoryNum(new BigDecimal("100"));
        tdGoodsDTO.setTdGdPrice(new BigDecimal("698"));
        tdGoodsDTO.setTdGdSysUserCode(dtMatchmaker.getMkUserCode());
        tdGoodsDTO.setTdGdSysUserRealName(dtMatchmaker.getMkName());
        tdGoodsDTO.setTdGdSysUserPhone(dtMatchmaker.getMkPhone());
        tdGoodsDTO.setTdGdCode(apiTdGoodsService.addGoods(tdGoodsDTO));
        return tdGoodsDTO;
    }
}
