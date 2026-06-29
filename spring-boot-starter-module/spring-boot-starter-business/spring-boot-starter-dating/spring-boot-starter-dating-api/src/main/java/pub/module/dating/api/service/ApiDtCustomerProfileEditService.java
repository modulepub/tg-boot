package pub.module.dating.api.service;

import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.dto.DtCustomerProfileEditDTO;

/**
 * 客户资料编辑（先入编辑记录表，审核通过后同步 customer）
 */
public interface ApiDtCustomerProfileEditService {

    /**
     * 查询当前用户最新资料编辑记录；无记录时从 customer 同步。
     */
    DtCustomerProfileEditDTO getCurrProfileEdit(String userCode);

    /**
     * 保存资料编辑（始终新增记录），走内容审核后仅同步通过字段到 customer。
     */
    DtCustomerProfileEditDTO saveCurrProfileEdit(String userCode, DtCustomerDTO patch);
}
