package pub.module.generator.biz.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * CxSelect 树结构实体类
 * 
 */
@Setter
@Getter
public class CxSelect implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据值字段名称
     */
    private String v;

    /**
     * 数据标题字段名称
     */
    private String n;

    /**
     * 子集数据字段名称
     */
    private List<CxSelect> s;


    public CxSelect(String v, String n)
    {
        this.v = v;
        this.n = n;
    }

}
