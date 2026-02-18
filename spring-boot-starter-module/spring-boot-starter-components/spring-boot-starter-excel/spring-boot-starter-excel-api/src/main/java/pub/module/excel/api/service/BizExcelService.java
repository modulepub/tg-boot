package pub.module.excel.api.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Excel业务服务接口
 * 定义Excel导出业务操作
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Service
public interface BizExcelService {

    /**
     * 导出Excel
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 文件名
     */
    <T> File exportExcel(File templateFile, T data);

    boolean importExcel(File excelFile);

    List<Map<String, Object>> readExcel(File excelFile);


}