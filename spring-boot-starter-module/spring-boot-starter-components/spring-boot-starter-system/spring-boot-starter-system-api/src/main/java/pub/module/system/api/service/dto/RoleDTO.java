package pub.module.system.api.service.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
  * 角色表
  * @author tg
  * @since 2025-06-11
  * @version V1.0
  */
@Data
@TableName("sys_role")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RoleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 角色名称*/
    private String roleName;
    /** 角色编码*/
    private String roleCode;
	/**角色编码*/
    private String sysRoleCode;
	/**描述*/
    private String description;
	/**创建人*/
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
	/**更新人*/
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;
	/**租户ID*/
    private Integer tenantId;

    /**
     * 区分不同终端的字段
     */
    private String terminal;
}
