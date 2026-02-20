package pub.module.excel.biz.controller;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import pub.module.excel.api.util.JXPathExcelReader;
import pub.module.excel.api.util.JXPathExcelWriter;
import pub.module.web.vo.Result;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Excel控制器
 * 处理Excel导出相关的请求
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Slf4j
@Tag(name = "工具")
@RestController
@RequestMapping("/cus/excel")
public class ExcelController {

    ExecutorService executorService = Executors.newFixedThreadPool(1);
    final String IMPORT_PREDIX = "excel_";
    Map<String, ImportExcelVO> IMPORT_STATUS_MAP = new HashMap<>();

    @Data
    @Schema(description = "导出EXCEL VO")
    public static class ExportExcelVO {
        @Schema(title = "数据链接", description = "数据链接")
        String dataUrl;
        @Schema(title = "模板路径", description = "模板路径")
        String templatePath;
    }


    public Map<String, String> copyGeneralHeader(HttpHeaders headers) {
        Map<String, String> headersMap = new HashMap<>();
        headers.forEach((key, value) -> {
            if ("content-type,content-length,accept-encoding".contains(key)) {
                return;
            }
            headersMap.put(key, value.getFirst());
        });
        headersMap.put("content-type", "application/json;charset=utf-8");
        return headersMap;
    }

    @SneakyThrows
    @Operation(summary = "导出EXCEL", description = "导出EXCEL")
    @GetMapping(value = "/export")
    public ResponseEntity<?> export(ExportExcelVO exportExcelVO, @RequestHeader HttpHeaders headers) {
        HttpRequest httpRequest = HttpUtil.createGet(exportExcelVO.getDataUrl());
        httpRequest.addHeaders(this.copyGeneralHeader(headers));
        String dataJsonStr;
        try (cn.hutool.http.HttpResponse response = httpRequest.execute()) {
            dataJsonStr = response.body();
        } catch (Exception e) {
            throw new RuntimeException("获取数据失败");
        }
        Map<String, Object> data = JSONUtil.parseObj(dataJsonStr);
        File templateFile = getTemplateFile(exportExcelVO.getTemplatePath());
        //File templateFile = new File("E:\\workspace_public\\tg-boot\\spring-boot-starter-module\\spring-boot-starter-components\\spring-boot-starter-excel\\exportTemplate.xlsx");
        JXPathExcelWriter fill = new JXPathExcelWriter(templateFile);
        File excelFile = fill.fillToFile(data);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= " + excelFile.getName())
                .body(resource);
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
        } else {
            result = FileUtil.file(templatePath);
        }
        Assert.notNull(result, "模板文件获取失败！");
        return result;
    }

    @Data
    public static class ImportExcelVO {
        String batchId;
        String excelName;
        Boolean completed;
        Boolean hasError;
        String filePath;
        String beginImportTime;
        String completeImportTime;
    }

    @SneakyThrows
    @Operation(summary = "导入EXCEL", description = "导入EXCEL")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<?> importExcel(@Parameter(description = "上传的文件", required = true) @RequestPart(value = "file") MultipartFile multipartFile, HttpServletRequest request, @RequestHeader HttpHeaders headers) {
        String pushUrl = request.getParameter("pushUrl");
        HttpRequest httpRequest = HttpUtil.createPost(pushUrl);
        httpRequest.addHeaders(this.copyGeneralHeader(headers));
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }
        String filePath = FileUtil.getTmpDirPath() + File.separator + IdUtil.getSnowflakeNextIdStr() + File.separator + multipartFile.getOriginalFilename();
        File localFile = FileUtil.newFile(filePath);
        FileUtil.writeBytes(multipartFile.getBytes(), localFile);
        String batchId = IMPORT_PREDIX + IdUtil.getSnowflakeNextIdStr();
        ImportExcelVO importExcelVO = new ImportExcelVO();
        importExcelVO.setBatchId(batchId);
        importExcelVO.setExcelName(multipartFile.getOriginalFilename());
        importExcelVO.setCompleted(false);
        importExcelVO.setBeginImportTime(LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        importExcelVO.setFilePath(filePath);
        IMPORT_STATUS_MAP.put(batchId, importExcelVO);
        executorService.submit(() -> {
            try {
                JXPathExcelReader reader = new JXPathExcelReader(localFile);
                reader.push(data -> {
                    String result = "导入成功";
                    httpRequest.body(JSONUtil.toJsonStr(data));
                    try (cn.hutool.http.HttpResponse response = httpRequest.execute()) {
                        JSONObject res = JSONUtil.parseObj(response.body());
                        if(res.getInt("code")!=null&& res.getInt("code")!=0){
                            importExcelVO.setHasError(true);
                            result = res.getStr("msg");
                        }else {
                            importExcelVO.setHasError(false);
                        }
                    } catch (Exception e) {
                        log.error("推送数据失败，", e);
                    }
                    return result;
                });
                importExcelVO.setCompleted(true);
                importExcelVO.setCompleteImportTime(LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
                IMPORT_STATUS_MAP.put(batchId, importExcelVO);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        return Result.ok(batchId);
    }


    @SneakyThrows
    @Operation(summary = "导入状态", description = "导入状态")
    @GetMapping(value = "/getImportStatus")
    public Result<?> getImportStatus(@RequestParam String keys) {
        List<ImportExcelVO> result = new ArrayList<>();
        for (String key : IMPORT_STATUS_MAP.keySet()) {
            if (keys.contains(key)) {
                result.add(IMPORT_STATUS_MAP.get(key));
            }
        }
        return Result.ok(result);
    }

    @SneakyThrows
    @Operation(summary = "下载导入EXCEL结果", description = "下载导入EXCEL结果")
    @GetMapping(value = "/downloadImportResult")
    public ResponseEntity<?> export(@RequestParam String batchId) {
        File excelFile = new File(IMPORT_STATUS_MAP.get(batchId).filePath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= " + excelFile.getName())
                .body(resource);
    }

}
