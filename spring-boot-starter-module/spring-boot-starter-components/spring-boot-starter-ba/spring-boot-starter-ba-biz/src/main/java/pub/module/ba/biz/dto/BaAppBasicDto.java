package pub.module.ba.biz.dto;

import lombok.Data;

/**
 * 行为明细
 *
 * @author tg
 * @version V1.0
 * @since 2025-10-10
 */
@Data
public class BaAppBasicDto {

    /**
     * 应用code
     */
    private String baAppCode;

    /**
     * 应用名称
     */
    private String baAppName;

    /**
     * 系统用户code
     */
    private String userCode;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 操作系统名称
     */
    private String osName;

    /**
     * 停留时间
     */
    private String dwellTime;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 页面code
     */
    private String pageCode;

    /**
     * 地区
     */
    private String area;

    /**
     * 来源名称
     */
    private String sourceName;

    /**
     * 来源code
     */
    private String sourceCode;

    //TODO 这两个参数可能会用于查询停留时间范围
//    /**
//     * 停留时间开始
//     */
//    private String dwellTimeStart;
//
//    /**
//     * 停留时间结束
//     */
//    private String dwellTimeEnd;

    /**
     * 是否是老用户
     */
    private Integer oldUser;

    /**
     * 创建时间
     */
    private String createTime;

//    /**
//     * 页数
//     */
//    private Integer pageNo;
//
//    /**
//     * 每页条数
//     */
//    private Integer pageSize;
}
