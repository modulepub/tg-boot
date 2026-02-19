package pub.module.excel.biz.controller;

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

import java.io.*;
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

    @SneakyThrows
    @Operation(summary = "导入EXCEL", description = "导入EXCEL")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importExcel(@Parameter(description = "上传的文件", required = true) @RequestPart(value = "file") MultipartFile multipartFile, HttpServletRequest request, @RequestHeader HttpHeaders headers) {
        String dataUrl = request.getParameter("dataUrl");
        HttpRequest httpRequest = HttpUtil.createPost(dataUrl);
        Map<String,String> headersMap = new HashMap<>();
        headers.forEach((key, value) -> {
            if ("content-type,content-length,accept-encoding".contains(key)) {
                return;
            }
            headersMap.put(key, value.getFirst());
        });
        headersMap.put("content-type", "application/json;charset=utf-8");
        httpRequest.addHeaders(headersMap);
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }
        String fileName = FileUtil.getTmpDirPath() + File.separator + IdUtil.getSnowflakeNextIdStr() + File.separator + multipartFile.getOriginalFilename();
        File localFile = FileUtil.newFile(fileName);
        FileUtil.writeBytes(multipartFile.getBytes(), localFile);
        System.err.println("上传的文件：" + localFile.getAbsolutePath());
        executorService.submit(() -> {
            try {
                JXPathExcelReader reader = new JXPathExcelReader(localFile);
                reader.push(data -> {
                    String result = "";
                    httpRequest.body(JSONUtil.toJsonStr(data));
                    try (cn.hutool.http.HttpResponse response = httpRequest.execute()) {
                        result = response.body();
                    } catch (Exception e) {
                        log.error("推送数据失败，",e);
                    }
                    return result;
                });
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
