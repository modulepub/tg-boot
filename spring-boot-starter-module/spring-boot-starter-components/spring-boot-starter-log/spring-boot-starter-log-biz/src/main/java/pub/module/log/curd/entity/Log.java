package pub.module.log.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 日志表 对象
 * @author tg
 * 2026-01-12 01:41:07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "日志表")
public class Log extends BaseEntity {
                    /** 日志编码 */
                        @Schema(description = "日志编码")
                private String logCode;

                    /** 日志名称 */
                        @Schema(description = "日志名称")
                private String logName;

                    /** 方法名 */
                        @Schema(description = "方法名")
                private String logMethodName;

                    /** 日志内容 */
                        @Schema(description = "日志内容")
                private String logContent;

                    /** 日志描述 */
                        @Schema(description = "日志描述")
                private String logDescription;

                    /** 事务编码 */
                        @Schema(description = "事务编码")
                private String logTransactionCode;

                    /** 用户名 */
                        @Schema(description = "用户名")
                private String logUserName;


        }
