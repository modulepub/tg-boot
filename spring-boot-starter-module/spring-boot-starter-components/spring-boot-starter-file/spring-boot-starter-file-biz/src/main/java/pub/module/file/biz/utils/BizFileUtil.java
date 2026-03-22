package pub.module.file.biz.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

import java.util.Date;

public class BizFileUtil {
    /**
     * 获取文件路径
     * @param fileMd5 文件HASH
     * @param fileName 文件名称
     * @param biz 业务路径
     * @return 封装的文件路径
     */
    public static String getPath(String fileMd5, String fileName, String biz) {
        String filePath;
        if (StrUtil.isEmpty(biz)) {
            biz = "temp";
        }
        Assert.isFalse(biz.contains("/"),"biz存储目录非法，不应该传入目录路径");
        if (StrUtil.isEmpty(fileMd5)) {
            fileMd5 = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
        }
        filePath = "/" + biz + "/" + fileMd5 + "/" + fileName;
        return filePath;
    }

}
