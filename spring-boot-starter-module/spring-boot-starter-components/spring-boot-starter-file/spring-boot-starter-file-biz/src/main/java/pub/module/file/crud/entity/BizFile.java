package pub.module.file.crud.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;

/**
 * 文件 对象
 * @author tg
 * 2026-03-09 07:28:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "文件")
public class BizFile extends BaseEntity {
                    @Schema(description = "文件编码")
                private String fileCode;

                    @Schema(description = "文件名称")
                private String fileName;

                    @Schema(description = "文件大小")
                private Long fileSize;

                    @Schema(description = "文件链接")
                private String fileUrl;


        }
