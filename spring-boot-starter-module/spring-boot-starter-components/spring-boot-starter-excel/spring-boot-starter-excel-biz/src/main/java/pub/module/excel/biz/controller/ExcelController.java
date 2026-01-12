package pub.module.excel.biz.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pub.module.excel.api.service.BizExcelService;


import jakarta.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * Excel控制器
 * 处理Excel导出相关的请求
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name = "工具")
@RestController
@RequestMapping("/cus/excel")
public class ExcelController {

    @Resource
    BizExcelService bizExcelService;


    @Data
    @Schema(description = "获取用户额度")
    public static class ExportExcelVO {
        @Schema(title = "数据链接", description = "数据链接")
        String dataUrl;
        @Schema(title = "模板路径", description = "模板路径")
        String templatePath;
        @Schema(title = "字典", description = "key:字典code+text,value:字典text")
        Map<String,String> dictMap;
    }


    @SneakyThrows
    @Operation(summary = "导出EXCEL", description = "导出EXCEL")
    @PostMapping(value = "/export")
    public ResponseEntity<?> export(@RequestBody ExportExcelVO exportExcelVO,@RequestHeader HttpHeaders headers) {
        Map<String, String> dictMap = exportExcelVO.getDictMap();
        HttpRequest httpRequest = HttpUtil.createGet(exportExcelVO.getDataUrl());
        headers.forEach((key, value) -> httpRequest.header(key, value.get(0)));
        String dataJsonStr;
        try (cn.hutool.http.HttpResponse response = httpRequest.execute()) {
            dataJsonStr = response.body();
        }catch (Exception e){
            throw new RuntimeException("获取数据失败");
        }
        Map<String, Object> data = JSONUtil.parseObj(dataJsonStr);
        File excelFile = bizExcelService.exportExcel(exportExcelVO.getTemplatePath(), data,dictMap);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= "+excelFile.getName())
                .body(resource);
    }

}
