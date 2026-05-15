package pub.module.file.curd.entity;

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
                    /** 文件编码 */
                        @Schema(description = "文件编码")
                private String fileCode;

                    /** 文件名称 */
                        @Schema(description = "文件名称")
                private String fileName;

                    /** 文件大小（字节） */
                        @Schema(description = "文件大小")
                private Long fileSize;

                    /** 文件链接 */
                        @Schema(description = "文件链接")
                private String fileUrl;


        }
