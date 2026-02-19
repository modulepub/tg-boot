package pub.module.excel.api.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XPath Excel写入工具类
 * 依赖模板EXCEL填充的导出类
 * 填充数据依据xpath来定位对象数据
 * 支持EXCEL头部尾部
 * 如果不考虑背景色的话，建议使用xls而不是xlsx来实现循环输出，性能差约5倍
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
public class JXPathExcelWriter {

    private final Workbook templateWorkbook;
    private static final Pattern pattern = Pattern.compile("\\{=(.+?)}");
    private final boolean xls2007;
    private final File templateFile;

    public JXPathExcelWriter(File templateFile) {
        this.templateFile = templateFile;
        xls2007 = templateFile.getAbsolutePath().toUpperCase().endsWith(".XLSX"); //建议使用xls模式填充
        try (FileInputStream fileInputStream = new FileInputStream(templateFile)) {
            templateWorkbook = xls2007 ? new XSSFWorkbook(fileInputStream) : new HSSFWorkbook(new POIFSFileSystem(fileInputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void setOutputCellType(Cell templateCell, Cell outputCell, JXPathContext objectContext) {
        switch (templateCell.getCellType()) {
            case Cell.CELL_TYPE_FORMULA:
                outputCell.setCellType(Cell.CELL_TYPE_FORMULA);
                outputCell.setCellFormula(templateCell.getCellFormula());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                outputCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                outputCell.setCellValue(templateCell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING:
                String templateCellValue = templateCell.getStringCellValue();
                Matcher matcher = pattern.matcher(templateCellValue);
                Map<String, Object> replacerMap = new HashMap<>();
                while (matcher.find()) {
                    String xpath = matcher.group(1);
                    Object valueObj = null;
                    try {
                        valueObj = objectContext.getValue(xpath);
                    } catch (JXPathNotFoundException e) {
                        //forget it
                        log.error(e.getMessage(), e);
                    }
                    replacerMap.put(matcher.group(0), valueObj == null ? "" : valueObj);
                }
                if (replacerMap.size() == 1 && templateCellValue.equals(replacerMap.keySet().iterator().next())) {
                    Object valueObj = replacerMap.values().iterator().next();
                    if (valueObj instanceof Integer) {
                        outputCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        outputCell.setCellValue((Integer) valueObj);
                    } else if (valueObj instanceof Double) {
                        outputCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        outputCell.setCellValue((Double) valueObj);
                    } else {
                        outputCell.setCellType(Cell.CELL_TYPE_STRING);
                        outputCell.setCellValue(valueObj.toString());
                    }
                    break;
                } else {
                    for (Entry<String, Object> entry : replacerMap.entrySet()) {
                        templateCellValue = templateCellValue.replace(entry.getKey(), entry.getValue().toString());
                    }
                }
                outputCell.setCellType(Cell.CELL_TYPE_STRING);
                outputCell.setCellValue(templateCellValue);
                break;
            default:
                outputCell.setCellType(Cell.CELL_TYPE_BLANK);
                outputCell.setCellValue("");
                break;
        }
    }

    public File fillToFile(Object object) {
        String outFileName = FileUtil.getTmpDirPath() + File.separator + DateUtil.date().getTime() + FileUtil.getName(templateFile.getName());
        JXPathContext objectContext = JXPathContext.newContext(object);
        try (
                Workbook workbook = xls2007 ? new XSSFWorkbook() : new HSSFWorkbook()
        ) {
            Sheet outputSheet = workbook.createSheet();
            Sheet templateSheet = ensureOpenSheet(templateWorkbook);

            outputSheet.setDefaultColumnWidth(templateSheet.getDefaultColumnWidth());
            outputSheet.setDefaultRowHeight(templateSheet.getDefaultRowHeight());
            for (int i = 0; i < 100; i++) {
                outputSheet.setColumnWidth(i, (int) (templateSheet.getColumnWidth(i) * 1.15));
            }

            int iterateRow = -1;
            int iterateCount = 1; //占位会有一行，如果没有输出循环，则行数-1

            //拷贝行列数据
            for (int j = 0; j < templateSheet.getLastRowNum() + 1; j++) {
                Row templateRow = templateSheet.getRow(j);

                //检查是否有循环标记
                Comment commentExpression = templateRow.getCell(0).getCellComment();
                if (commentExpression != null) { //循环输出
                    iterateRow = j;
                    iterateCount = 0; //准备输出
                    try {
                        Object iterateObj = objectContext.getValue(commentExpression.getString().getString());
                        if (iterateObj.getClass().isArray() || iterateObj instanceof Collection) {
                            List<Object> iterateDataList = (iterateObj instanceof Collection ? new ArrayList<>((Collection<?>) iterateObj) : Collections.singletonList(iterateObj));
                            List<CellStyle> cellStyleCache = new ArrayList<>();
                            for (Object rowObj : iterateDataList) {
                                JXPathContext rowObjectContext = JXPathContext.newContext(rowObj);

                                Row outputRow = outputSheet.createRow(j + iterateCount);
                                iterateCount++;

                                outputRow.setHeight(templateRow.getHeight());
                                Iterator<Cell> cellIterator = templateRow.cellIterator();

                                for (int i = 0; cellIterator.hasNext(); i++) {
                                    Cell outputCell = outputRow.createCell(i);
                                    Cell templateCell = cellIterator.next();
                                    if (i > cellStyleCache.size() - 1) {
                                        CellStyle style = workbook.createCellStyle();
                                        style.cloneStyleFrom(templateCell.getCellStyle());
                                        outputCell.setCellStyle(style);
                                        cellStyleCache.add(style);
                                    } else {
                                        CellStyle style = cellStyleCache.get(i);
                                        outputCell.setCellStyle(style);
                                    }

                                    setOutputCellType(templateCell, outputCell, rowObjectContext);
                                }
                            }
                        }
                    } catch (JXPathNotFoundException e) {
                        //forget it
                    }
                } else { //直接输出
                    Row outputRow = outputSheet.createRow(j - 1 + iterateCount);

                    outputRow.setHeight(templateRow.getHeight());
                    Iterator<Cell> cellIterator = templateRow.cellIterator();

                    while (cellIterator.hasNext()) {
                        Cell templateCell = cellIterator.next();
                        Cell outputCell = outputRow.createCell(templateCell.getColumnIndex());
                        CellStyle style = workbook.createCellStyle();
                        style.cloneStyleFrom(templateCell.getCellStyle());
                        outputCell.setCellStyle(style);
                        setOutputCellType(templateCell, outputCell, objectContext);
                    }
                }
            }

            //拷贝合并
            for (int i = 0; i < templateSheet.getNumMergedRegions(); i++) {
                CellRangeAddress region = templateSheet.getMergedRegion(i);
                CellRangeAddress copyRegion = region.copy();
                //位置修正：跳过循环体
                if (iterateRow > -1) {
                    //如果合并单元格跨循环体或在循环体则忽略
                    if (copyRegion.getFirstRow() >= iterateRow && copyRegion.getLastRow() <= iterateRow) {
                        continue;
                    } else {
                        if (copyRegion.getFirstRow() > iterateRow) {
                            copyRegion.setFirstRow(copyRegion.getFirstRow() - 1 + iterateCount);
                            copyRegion.setLastRow(copyRegion.getLastRow() - 1 + iterateCount);
                        }
                    }
                }
                outputSheet.addMergedRegion(copyRegion);
            }

            File excelFile = new File(outFileName);
            if (excelFile.getParentFile() != null && !excelFile.getParentFile().exists()) {
                boolean mkDirResult = excelFile.getParentFile().mkdirs();
                if (!mkDirResult) {
                    throw new RuntimeException("创建目录失败");
                }
            }
            try (
                    FileOutputStream fileOutputStream = new FileOutputStream(outFileName)
            ) {
                workbook.write(fileOutputStream);
            }
            return excelFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected Sheet ensureOpenSheet(Workbook workbook) {
        int sheetIndex = workbook.getNumberOfSheets();
        if (sheetIndex == 0) {
            workbook.createSheet();
        }
        return workbook.getSheetAt(0);
    }
}
