package pub.module.file.biz.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pub.module.file.api.service.BizUploadService;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import pub.module.file.biz.utils.BizFileUtil;
import pub.module.file.curd.entity.BizFile;
import pub.module.file.curd.service.BizFileService;
import pub.module.common.model.vo.Result;

import jakarta.annotation.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件上传
 *
 * @author panzhen
 */
@Tag(name = "工具")
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    BizUploadService uploadService;
    @Resource
    BizFileService bizFileService;
    @Schema(description = "上传结果 VO")
    @Data
    public static class UploadResult{
        @Schema(description = "当前分片索引")
        Integer sliceIndex;
        @Schema(description = "是否完成上传")
        Boolean complete;
        @Schema(description = "文件名称")
        String fileName;
        @Schema(description = "文件路径")
        String filePath;
        @Schema(description = "分片Md5")
        String sliceFileMd5;
        @Schema(description = "文件前缀")
        String urlPredix;
        @Schema(description = "完整路径")
        String fullFilePath;
    }

    @SneakyThrows
    @Operation(summary = "通用文件上传（支持分片）")
    @PostMapping(value ="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UploadResult> uploadByFragment(@Parameter(description = "上传的文件", required = true) @RequestPart(value = "file") MultipartFile multipartFile,
                                                 @Parameter(description = "业务名称",required = true) @RequestParam(value = "biz") String biz,
                                                 @Parameter(description = "上传的文件的md5值") @RequestParam(value = "fileMd5",required = false) String fileMd5,
                                                 @Parameter(description = "上传的切片文件的md5值") @RequestParam(value = "sliceFileMd5",required = false) String sliceFileMd5,
                                                 @Parameter(description = "上传的文件片段的索引") @RequestParam(value = "sliceIndex",required = false, defaultValue = "0") Integer sliceIndex,
                                                 @Parameter(description = "上传的文件片段的总数") @RequestParam(value = "totalPieces",required = false, defaultValue = "1") Integer totalPieces) {

        JSONObject config = uploadService.getConfig();
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }
        File localFile = FileUtil.createTempFile();
        try (InputStream inputStream = multipartFile.getInputStream();
             OutputStream outputStream = new FileOutputStream(localFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        }

        String filePath = BizFileUtil.getPath(fileMd5,multipartFile.getOriginalFilename(),biz);
        if(StrUtil.isEmpty(sliceFileMd5)){
            sliceFileMd5 = MD5.create().digestHex16(filePath);
        }
        int index = uploadService.uploadByFragment(localFile, filePath,sliceFileMd5,sliceIndex,totalPieces);
        UploadResult result = new UploadResult();
        result.setFileName(multipartFile.getOriginalFilename());
        result.setFilePath(filePath);
        result.setSliceIndex(index);
        result.setSliceFileMd5(sliceFileMd5);
        result.setComplete(index == -1);
        result.setUrlPredix(config.getStr("urlPrefix"));
        result.setFullFilePath(config.getStr("urlPrefix")+filePath);
        BizFile file = BeanUtil.copyProperties(result,BizFile.class);
        file.setFileUrl(result.getFullFilePath());
        file.setFileSize(totalPieces*localFile.length());
        bizFileService.save(file);
        return Result.ok(result);
    }


}
