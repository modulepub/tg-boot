package pub.module.cms.crud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import pub.module.cms.crud.entity.CmsNode;
import pub.module.cms.crud.entity.CmsNodeReadRecord;

public interface CmsNodeMapper extends BaseMapper<CmsNode> {

    @Update("UPDATE cms_node SET node_view_count = IFNULL(node_view_count, 0) + 1 "
            + "WHERE id = #{id} AND deleted = '0'")
    int incrementViewCount(@Param("id") String id);
}
