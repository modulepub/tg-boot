package pub.module.dating.crud.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Select;

import pub.module.dating.api.service.dto.CusCityResidenceOptionDTO;
import pub.module.dating.crud.entity.DtCustomer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 客户 Mapper
 * @author tg
 *  2026-02-01 10:25:44
 */
public interface DtCustomerMapper extends BaseMapper<DtCustomer> {

    /**
     * 按生活城市编码分组，返回编码 + 名称（供推荐意向等：存编码、显名称）
     */
    @Select("""
            SELECT cus_city_residence_code AS cusCityResidenceCode,
                   MAX(TRIM(cus_city_residence_name)) AS cusCityResidenceName
            FROM dt_customer
            WHERE (deleted IS NULL OR deleted = '' OR deleted = '0')
              AND cus_city_residence_code IS NOT NULL
              AND CHAR_LENGTH(TRIM(cus_city_residence_code)) > 0
            GROUP BY cus_city_residence_code
            ORDER BY MAX(TRIM(cus_city_residence_name))
            """)
    List<CusCityResidenceOptionDTO> listDistinctResidenceCities();
}
