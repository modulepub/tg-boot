package pub.module.dating.biz.controller.pub;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dating.curd.entity.DtCusMatchmaker;
import pub.module.dating.curd.service.DtCusMatchmakerService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import java.util.Collection;


/**
 * 我的红娘 Controller
 *
 * @author tg
 *  2026-01-07 23:30:24
 */
@Tag(name="移动端首页统计")
@RestController
@RequestMapping("/pub/dating/statistic")
@Slf4j
public class PubStatisticController {


        @Data
        public static class Info{
            long recommentTotal;
            String recommentTotalDetail;
            long matchedTotal;
            String matchedTotalDetail;
            long marriedTotal;
            String marriedTotalDetail;
        }

        @Operation(summary="小程序统计")
        @GetMapping(value = "/center")
        public Result<Info> info() {
            Info result = new Info();
            result.setRecommentTotal(10000);
            result.setRecommentTotalDetail("http://www.baidu.com");
            result.setMatchedTotal(8090);
            result.setMatchedTotalDetail("http://www.baidu.com");
            result.setMarriedTotal(111);
            result.setMarriedTotalDetail("http://www.baidu.com");
            return Result.ok(result);
        }



}