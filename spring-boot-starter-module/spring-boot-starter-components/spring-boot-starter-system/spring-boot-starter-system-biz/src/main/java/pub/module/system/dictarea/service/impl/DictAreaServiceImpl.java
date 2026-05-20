package pub.module.system.dictarea.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.system.dictarea.entity.DictArea;
import pub.module.system.dictarea.mapper.DictAreaMapper;
import pub.module.system.dictarea.service.DictAreaService;

@Service
public class DictAreaServiceImpl extends ServiceImpl<DictAreaMapper, DictArea> implements DictAreaService {

    @Override
    public IPage<DictArea> searchArea(String keyword, String parentCode, Integer pageNo, Integer pageSize) {
        Page<DictArea> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<DictArea> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            String k = keyword.trim();
            q.and(w -> w.like(DictArea::getDictAreaName, k)
                    .or().like(DictArea::getDictAreaNameEn, k)
                    .or().like(DictArea::getDictAreaFullName, k));
        }
        else if (StrUtil.isNotBlank(parentCode)) {
            q.eq(DictArea::getDictAreaParentCode, parentCode.trim());
        }
        else {
            q.isNull(DictArea::getDictAreaParentCode);
        }
        q.orderByAsc(DictArea::getDictAreaLevel)
                .orderByAsc(DictArea::getSeqNo)
                .orderByAsc(DictArea::getDictAreaCode);
        return page(page, q);
    }
}
