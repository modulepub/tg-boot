package pub.module.ocr.biz.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pub.module.web.vo.Result;
import pub.module.file.api.service.BizOcrService;

import java.io.File;
import java.io.IOException;

/**
 * 综合工具
 *
 * @author panzhen
 */
@Tag(name = "工具-OCR识别", description = "OCR识别")
@Slf4j
@RestController
@RequestMapping("/ocr")
public class OcrController {

    @Operation(summary = "银行卡OCR识别")
    @PostMapping(value = "/bankOcr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<BizOcrService.BankOcr> bankOcr(@Parameter(description = "上传的文件", required = true) @RequestPart(value = "file") MultipartFile multipartFile) {
        File file = FileUtil.createTempFile(FileUtil.getTmpDir());
        BizOcrService bizOcrService = SpringUtil.getBean("bizKsOcrService");
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败" + e.getMessage());
        }
        BizOcrService.BankOcr bankOcr = bizOcrService.bankOcr(file);
        return Result.ok(bankOcr);
    }


    @Operation(summary = "身份证OCR识别")
    @PostMapping(value = "/idCardOcr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<BizOcrService.IdCardOcr> idCardOcr(@Parameter(description = "上传的文件", required = true) @RequestPart(value = "file") MultipartFile multipartFile) {
        BizOcrService bizOcrService = SpringUtil.getBean("bizKsOcrService");
        File file = FileUtil.createTempFile();
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败" + e.getMessage());
        }
        BizOcrService.IdCardOcr bankOcr = bizOcrService.IdCardOcr(file);
        return Result.ok(bankOcr);
    }


}
