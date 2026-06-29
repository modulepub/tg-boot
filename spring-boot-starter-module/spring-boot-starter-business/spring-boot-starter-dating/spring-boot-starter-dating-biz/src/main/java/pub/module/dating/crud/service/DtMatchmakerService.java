package pub.module.dating.crud.service;

import pub.module.dating.crud.entity.DtMatchmaker;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 红娘信息 Service
 *
 * @author tg
 * 2026-03-22 13:32:44
 */
public interface DtMatchmakerService extends IService<DtMatchmaker> {
    DtMatchmaker getByCode(String code);

    /** 按用户编码查询红娘（至多一条） */
    DtMatchmaker getByUserCode(String userCode);

    /**
     * 用户端筛选用：按红娘记录聚合去重后的所在城市名称（非空），字典序升序。
     */
    List<String> listDistinctMkCityNames();
}
