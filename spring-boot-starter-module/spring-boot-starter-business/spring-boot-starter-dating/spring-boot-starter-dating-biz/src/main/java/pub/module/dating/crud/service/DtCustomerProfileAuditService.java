package pub.module.dating.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.dating.crud.entity.DtCustomerProfileAudit;

import java.util.List;

public interface DtCustomerProfileAuditService extends IService<DtCustomerProfileAudit> {

    DtCustomerProfileAudit getByCode(String cusProfileAuditCode);

    /**
     * 查询某用户当前全部资料审核明细（字段名、子项序号升序）。
     */
    List<DtCustomerProfileAudit> listByCusUserCode(String cusUserCode);

    DtCustomerProfileAudit getByCmRecordCode(String cmRecordCode);

    /**
     * 查询引用同一内容审核记录的全部明细（同一内容被复用到多个字段/子项时可能多于一条）。
     */
    List<DtCustomerProfileAudit> listByCmRecordCode(String cmRecordCode);

    /**
     * 作废某用户指定字段的全部审核明细（逻辑删除），用于该字段被重新提交时替换旧审核。
     */
    void removeByCusUserCodeAndField(String cusUserCode, String fieldName);
}
