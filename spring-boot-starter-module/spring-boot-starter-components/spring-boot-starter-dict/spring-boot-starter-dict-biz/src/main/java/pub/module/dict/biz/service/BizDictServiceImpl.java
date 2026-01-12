package pub.module.dict.biz.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pub.module.dict.api.constants.DictCacheKey;
import pub.module.dict.api.service.BizDictService;
import pub.module.dict.curd.entity.Dict;
import pub.module.dict.curd.entity.DictItem;
import pub.module.dict.curd.service.IDictItemService;
import pub.module.dict.curd.service.IDictService;

import java.util.List;

@Service
public class BizDictServiceImpl implements BizDictService {
    @Resource
    IDictService dictService;
    @Resource
    IDictItemService dictItemService;

    @Cacheable(value = DictCacheKey.DICT_CACHE_KEY, key = "#dictCode")
    @Override
    public List<DictDTO> listByCode(String dictCode) {

        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        if (!"all".equals(dictCode)) {
            List<String> dictCodeList = StrUtil.split(dictCode, ",");
            queryWrapper.lambda().in(Dict::getDictCode, dictCodeList);
        }

        List<Dict> list = dictService.list(queryWrapper);
        List<DictDTO> dictDTOList = BeanUtil.copyToList(list, DictDTO.class);
        for (DictDTO item : dictDTOList) {
            List<DictItem> dictItemList = dictItemService.list(new QueryWrapper<DictItem>().lambda().eq(DictItem::getDictCode, item.getDictCode()));
            item.setDictItemList(BeanUtil.copyToList(dictItemList, DictDTO.DictItemDTO.class));
        }
        return dictDTOList;
    }
}
