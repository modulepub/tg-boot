package pub.module.wx.crud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import pub.module.wx.crud.entity.WxMaSubscribeSendLog;
import pub.module.wx.crud.model.WxMaSubscribeTemplateGroupRow;

import java.util.List;

public interface WxMaSubscribeSendLogMapper extends BaseMapper<WxMaSubscribeSendLog> {

    @Select("""
            SELECT template_id AS templateId,
                   COUNT(*) AS sendCount,
                   MAX(idempotent_key) AS sampleIdempotentKey
            FROM wx_ma_subscribe_send_log
            WHERE deleted = 0
              AND template_id IS NOT NULL
              AND TRIM(template_id) <> ''
            GROUP BY template_id
            ORDER BY sendCount DESC, template_id ASC
            """)
    List<WxMaSubscribeTemplateGroupRow> listTemplateGroupStats();
}
