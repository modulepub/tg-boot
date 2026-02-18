package pub.module.generator.biz.service.impl;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.generator.biz.entity.GenTableColumn;
import pub.module.generator.biz.mapper.GenTableColumnMapper;
import pub.module.generator.biz.service.IGenTableColumnService;

/**
 * 业务字段 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class GenTableColumnServiceImpl implements IGenTableColumnService
{
    @Resource
    private GenTableColumnMapper genTableColumnMapper;

    /**
     * 查询业务字段列表
     * 
     * @param genTableColumn 业务字段信息
     * @return 业务字段集合
     */
    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(GenTableColumn genTableColumn)
    {
        QueryWrapper<GenTableColumn> queryWrapper = new QueryWrapper<>();
        if(StrUtil.isNotEmpty(genTableColumn.getColumnName())){
            queryWrapper.lambda().like(GenTableColumn::getColumnName, genTableColumn.getColumnName());
        }
        if(genTableColumn.getTableId()!=null){
            queryWrapper.lambda().eq(GenTableColumn::getTableId, genTableColumn.getTableId());
        }
        return genTableColumnMapper.selectList(queryWrapper);
    }
}