package pub.module.excel.biz.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.stereotype.Service;
import pub.module.excel.api.service.BizExcelService;
import pub.module.excel.biz.util.JXPathExcelReader;
import pub.module.excel.biz.util.JXPathExcelWriter;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Excel业务服务实现类
 * 实现Excel导出业务逻辑
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Service
public class BizExcelServiceImpl implements BizExcelService {


    @Override
    public <T> File exportExcel(File templateFile, T data) {
        JXPathExcelWriter fill = new JXPathExcelWriter(templateFile.getAbsolutePath());
        String outFileName = FileUtil.getTmpDirPath() + File.separator + DateUtil.date().getTime() + FileUtil.getName(templateFile.getName());
        fill.fillToFile(data, outFileName);
        return new File(outFileName);
    }

    @Override
    public boolean importExcel(File excelFile) {
        JXPathExcelReader reader = new JXPathExcelReader(excelFile);
        return reader.push();

    }

    @Override
    public List<Map<String, Object>> readExcel(File excelFile) {
        JXPathExcelReader reader = new JXPathExcelReader(excelFile);
        return reader.read();
    }



}
