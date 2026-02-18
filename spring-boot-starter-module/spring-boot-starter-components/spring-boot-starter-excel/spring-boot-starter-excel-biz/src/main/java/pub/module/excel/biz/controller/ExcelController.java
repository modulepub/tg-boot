package pub.module.excel.biz.controller;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pub.module.excel.api.service.BizExcelService;


import jakarta.annotation.Resource;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Resource
    BizExcelService bizExcelService;
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    final String importStatusKeyPredix = "excel_";

    @Data
    @Schema(description = "导出EXCEL VO")
    public static class ExportExcelVO {
        @Schema(title = "数据链接", description = "数据链接")
        String dataUrl;
        @Schema(title = "模板路径", description = "模板路径")
        String templatePath;
    }


    @SneakyThrows
    @Operation(summary = "导出EXCEL", description = "导出EXCEL")
    @PostMapping(value = "/export")
    public ResponseEntity<?> export(@RequestBody ExportExcelVO exportExcelVO, @RequestHeader HttpHeaders headers) {
        HttpRequest httpRequest = HttpUtil.createGet(exportExcelVO.getDataUrl());
        headers.forEach((key, value) -> httpRequest.header(key, value.getFirst()));
        String dataJsonStr;
        try (cn.hutool.http.HttpResponse response = httpRequest.execute()) {
            dataJsonStr = response.body();
        } catch (Exception e) {
            throw new RuntimeException("获取数据失败");
        }
        Map<String, Object> data = JSONUtil.parseObj(dataJsonStr);
        File templateFile = getTemplateFile(exportExcelVO.getTemplatePath());
        File excelFile = bizExcelService.exportExcel(templateFile, data);
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

    @SneakyThrows
    @Operation(summary = "导入EXCEL", description = "导入EXCEL")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importExcel(@Parameter(description = "上传的文件", required = true) @RequestPart(value = "file") MultipartFile multipartFile, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }
        String fileName = FileUtil.getTmpDirPath() + File.separator + IdUtil.getSnowflakeNextIdStr() + File.separator + multipartFile.getOriginalFilename();
        File localFile = FileUtil.newFile(fileName);
        FileUtil.writeBytes(multipartFile.getBytes(), localFile);
        System.err.println("上传的文件：" + localFile.getAbsolutePath());
        String importStatusKey = importStatusKeyPredix + fileName;
        String beginTime = LocalDateTimeUtil.format(LocalDateTime.now(), DateTimeFormatter.ISO_LOCAL_DATE);
        String endTime = LocalDateTimeUtil.format(LocalDateTime.now(), DateTimeFormatter.ISO_LOCAL_DATE);
        Map<String, Object> ingData = new HashMap<>();
        ingData.put("importStatus", "ing");
        ingData.put("fileName", fileName);
        ingData.put("file", localFile);
        ingData.put("beginTime", beginTime);
        ingData.put("endTime", endTime);
        session.setAttribute(importStatusKey, ingData);
        executorService.submit(() -> {
            try {
                boolean success = bizExcelService.importExcel(localFile);
                if (success) {
                    Map<String, Object> successData = new HashMap<>();
                    successData.put("importStatus", "success");
                    successData.put("fileName", fileName);
                    successData.put("file", localFile);
                    successData.put("beginTime", beginTime);
                    successData.put("endTime", LocalDateTimeUtil.format(LocalDateTime.now(), DateTimeFormatter.ISO_LOCAL_DATE));
                    session.setAttribute(importStatusKey, successData);
                } else {
                    Map<String, Object> failData = new HashMap<>();
                    failData.put("importStatus", "fail");
                    failData.put("fileName", fileName);
                    failData.put("file", localFile);
                    failData.put("beginTime", beginTime);
                    failData.put("endTime", LocalDateTimeUtil.format(LocalDateTime.now(), DateTimeFormatter.ISO_LOCAL_DATE));
                    session.setAttribute(importStatusKey, failData);
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        return ResponseEntity.ok("上传成功！");
    }


    @SneakyThrows
    @Operation(summary = "导入状态", description = "导入状态")
    @GetMapping(value = "/getImportStatus")
    public ResponseEntity<?> getImportStatus(HttpServletRequest request) {
        Enumeration<String> attrs = request.getSession().getAttributeNames();
        List<Map<String, Object>> result = new ArrayList<>();
        while (attrs.hasMoreElements()) {
            String key = attrs.nextElement();
            if (key.startsWith(importStatusKeyPredix)) {
                Map<String, Object> map = new HashMap<>();
                Object value = request.getSession().getAttribute(key);
                if (value instanceof Map<?, ?>) {
                    map.putAll((Map<String, Object>) value);
                    map.remove("file");
                }
                result.add(map);
            }
        }
        return ResponseEntity.ok(result);
    }

}
