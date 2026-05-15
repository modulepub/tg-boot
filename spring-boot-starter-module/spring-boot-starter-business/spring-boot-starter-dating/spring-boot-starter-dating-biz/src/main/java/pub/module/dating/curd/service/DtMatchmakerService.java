package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtMatchmaker;
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

    /**
     * 用户端筛选用：按红娘记录聚合去重后的所在城市名称（非空），字典序升序。
     */
    List<String> listDistinctMkCityNames();
}
