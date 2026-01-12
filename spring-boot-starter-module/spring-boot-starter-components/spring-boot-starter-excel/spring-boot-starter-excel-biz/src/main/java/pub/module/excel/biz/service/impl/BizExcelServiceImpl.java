package pub.module.excel.biz.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.stereotype.Service;
import pub.module.excel.api.service.BizExcelService;
import pub.module.excel.biz.util.JXPathExcelWriter;

import java.io.File;
import java.util.Map;

/**
 * Excel业务服务实现类
 * 实现Excel导出业务逻辑
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Service
public class BizExcelServiceImpl implements BizExcelService {
    @Override
    public <T> File exportExcel(String templatePath, T data, Map<String, String> dictMap) {
        File templateFile = getTemplateFile(templatePath);
        JXPathExcelWriter fill = new JXPathExcelWriter(templateFile.getAbsolutePath(),dictMap);
        String outFileName = FileUtil.getTmpDirPath()+File.separator+ DateUtil.date().getTime()+FileUtil.getName(templateFile.getName());
        fill.fillToFile(data, outFileName);
        return new File(outFileName);
    }

    File getTemplateFile(String templatePath) {
        File result;
        Assert.notEmpty(templatePath, "templatePath is not null");
        if (templatePath.startsWith("http")) {
            String filePath = URLUtil.getPath(templatePath);
            result = FileUtil.file(filePath);
            if (!result.exists()) {
                HttpUtil.downloadFile(templatePath, result);
            }
        }else {
            result = FileUtil.file(templatePath);
        }
        Assert.notNull(result, "模板文件获取失败！");
        return result;
    }
}
