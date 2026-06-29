package pub.module.trade.crud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import pub.module.trade.crud.entity.TdGoodsBenefit;

/**
 * 商品权益 Mapper
 */
public interface TdGoodsBenefitMapper extends BaseMapper<TdGoodsBenefit> {

    /**
     * 按商品编码物理删除权益行（替换权益列表时使用，避免逻辑删除与 uk_td_gd_bnf_code 冲突）。
     */
    @Delete("DELETE FROM td_goods_benefit WHERE td_gd_code = #{tdGdCode}")
    int deletePhysicalByTdGdCode(@Param("tdGdCode") String tdGdCode);
}
