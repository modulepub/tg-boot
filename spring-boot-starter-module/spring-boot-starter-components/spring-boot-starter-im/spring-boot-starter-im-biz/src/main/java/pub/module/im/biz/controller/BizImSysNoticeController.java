package pub.module.im.biz.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.web.vo.Result;
import pub.module.im.api.constants.ImSysNoticePublishStateCodeEnum;
import pub.module.im.api.service.BizImSysNoticeService;
import pub.module.im.curd.entity.ImSysNotice;
import pub.module.im.curd.service.IImSysNoticeService;

import jakarta.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 系统通知
 *
 * @author tg
 * @version V1.0
 * @since 2025-10-05
 */

@Tag(name = "融云即时通讯")
@RestController
@RequestMapping("/im/biz/imSysNotice")
@Slf4j
public class BizImSysNoticeController {
    @Resource
    private BizImSysNoticeService bizImSysNoticeService;
    @Resource
    private IImSysNoticeService sysNoticeService;
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Data
    public static class PublishVO {
        public String sysConfigCode;
        public String imSysNoticeCode;
        public String userCode;
    }

    @Operation(summary = "融云即时通讯-发布系统通知")
    @PostMapping(value = "/publishByCode")
    public Result<?> publishByCode(@RequestBody PublishVO publishVO) {
        ImSysNotice imSysNotice = sysNoticeService.getOne(new QueryWrapper<ImSysNotice>().lambda().eq(ImSysNotice::getImSysNoticeCode, publishVO.getImSysNoticeCode()),false);
        Assert.notNull(imSysNotice,"imSYsNotice is null");
        executorService.submit(() -> {
            try {
                bizImSysNoticeService.publish(imSysNotice);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });

        return Result.ok("发送成功",imSysNotice);
    }

    @Operation(summary = "融云即时通讯-取消发布系统通知")
    @PostMapping(value = "/cancelPublishByCode")
    public Result<?> cancelPublishByCode(@RequestBody PublishVO publishVO) {
        ImSysNotice imSysNotice = sysNoticeService.getOne(new QueryWrapper<ImSysNotice>().lambda().eq(ImSysNotice::getImSysNoticeCode, publishVO.getImSysNoticeCode()),false);
        Assert.notNull(imSysNotice,"imSYsNotice is null");
        imSysNotice.setImSysNoticePublishStateCode(ImSysNoticePublishStateCodeEnum.NOT.getCode());
        sysNoticeService.updateById(imSysNotice);

        return Result.ok("取消成功",imSysNotice);
    }
}
