package pub.module.excel.api.util;

import cn.hutool.core.lang.Assert;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * XPath Excel导入工具类
 * 依赖模板EXCEL填充的导出类
 * 填充数据依据xpath来定位对象数据
 * 支持EXCEL头部尾部
 * 如果不考虑背景色的话，建议使用xls而不是xlsx来实现循环输出，性能差约5倍
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Data
@Slf4j
public class ImportHandler {

    private final Workbook dataWorkbook;
    private final File excelFile;

    public ImportHandler(File excelFile) {
        this.excelFile = excelFile;
        String filePath = excelFile.getAbsolutePath();
        boolean xls2007 = filePath.toUpperCase().endsWith(".XLSX"); //建议使用xls模式填充
        try (FileInputStream fileInputStream = new FileInputStream(excelFile)) {
            dataWorkbook = xls2007 ? new XSSFWorkbook(fileInputStream) : new HSSFWorkbook(new POIFSFileSystem(fileInputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCommentStr(Cell cell) {
        Comment cellComment = cell.getCellComment();
        Assert.notNull(cellComment, "非法的数据形式，请检查模板！");
        Assert.notEmpty(cellComment.getString().toString(), "非法的数据形式，请检查模板！");
        String comStr = cellComment.getString().toString().trim();
        comStr = comStr.replaceAll("\\s*[\\r\\n]+\\s*", "");
        if (comStr.contains(":")) {
            comStr = comStr.split(":")[1];
        }
        return comStr;
    }

    public void push(PushDataHandler pushDataHandler) {
        Sheet templateSheet;
        templateSheet = dataWorkbook.getSheetAt(0);
        List<String> keys = new ArrayList<>();
        Row titleRow = templateSheet.getRow(1);
        for (int i = 0; i < titleRow.getPhysicalNumberOfCells(); i++) {
            Cell cell = titleRow.getCell(i);
            keys.add(getCommentStr(cell));
        }
        Cell titleRowCell = titleRow.createCell(titleRow.getPhysicalNumberOfCells());
        titleRowCell.setCellType(Cell.CELL_TYPE_STRING);
        titleRowCell.setCellValue("**导入结果**");
        //拷贝行列数据
        for (int j = 2; j < templateSheet.getLastRowNum() + 1; j++) {
            Row dataRow = templateSheet.getRow(j);
            Map<String, Object> dataMap = new HashMap<>();
            int cellNum = dataRow.getPhysicalNumberOfCells();
            //读取每一列的数据
            for (int i = 0; i < cellNum; i++) {
                Cell cell = dataRow.getCell(i);
                dataMap.put(keys.get(i), getValue(cell));
            }
            Cell outputCell = dataRow.createCell(cellNum);
            try {
                //执行业务数据写入
                String result = pushDataHandler.push(dataMap);
                outputCell.setCellType(Cell.CELL_TYPE_STRING);
                outputCell.setCellValue(result);
            } catch (Exception e) {
                outputCell.setCellType(Cell.CELL_TYPE_STRING);
                outputCell.setCellValue("fail:" + e.getMessage());
                log.error("Excel导入行失败！", e);
            }
        }
        System.err.println(excelFile.getAbsolutePath());
        try (FileOutputStream fileOutputStream = new FileOutputStream(excelFile)) {
            dataWorkbook.write(fileOutputStream);
        } catch (IOException e) {
            log.error("excel写入失败", e);
        }
        try {
            dataWorkbook.close();
        } catch (IOException e) {
            log.error("excel关闭失败", e);
        }
    }

    private Object getValue(Cell cell) {
        Object value = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = cell.getNumericCellValue();
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return value;
    }

    public List<Map<String, Object>> read() {
        List<Map<String, Object>> result = new ArrayList<>();
        Sheet templateSheet = dataWorkbook.getSheetAt(0);
        List<String> keys = new ArrayList<>();
        Row interfaceRow = templateSheet.getRow(0);
        Cell interfaceRowCell = interfaceRow.getCell(0);
        String interfaceStr = getCommentStr(interfaceRowCell);
        Assert.isTrue(interfaceStr.contains("."), "数据模板标题错误，未设置数据接口地址");
        Row titleRow = templateSheet.getRow(1);
        for (int i = 0; i < titleRow.getPhysicalNumberOfCells(); i++) {
            Cell cell = titleRow.getCell(i);
            keys.add(getCommentStr(cell));
        }
        //拷贝行列数据
        for (int j = 2; j < templateSheet.getLastRowNum() + 1; j++) {
            Row dataRow = templateSheet.getRow(j);
            Map<String, Object> dataMap = new HashMap<>();
            for (int i = 0; i < dataRow.getPhysicalNumberOfCells(); i++) {
                Cell cell = dataRow.getCell(i);
                dataMap.put(keys.get(i), cell.getStringCellValue());
                result.add(dataMap);
            }
            result.add(dataMap);
        }
        return result;
    }
}
