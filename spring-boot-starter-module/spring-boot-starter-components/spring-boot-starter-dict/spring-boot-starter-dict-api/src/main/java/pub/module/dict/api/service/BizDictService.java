package pub.module.dict.api.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * sys_dict
 * @author tg
 * @since 2025-09-29
 * @version V1.0
 */
public interface BizDictService {
    List<DictDTO> listByCode(String dictCode);
    @Data
    class DictDTO implements Serializable {

        @Schema(description="字典名称")
        private String dictName;
        @Schema(description="字典编码")
        private String dictCode;
        List<DictItemDTO> dictItemList;
        @Data
        public static class DictItemDTO implements Serializable{

            @Schema(description="字典编码")
            private String dictCode;

            @Schema(description="字典项文本")
            private String dictItemText;

            @Schema(description="字典项值")
            private String dictItemValue;
            @Schema(description="颜色")
            private String dictItemColor;
        }
    }


}
