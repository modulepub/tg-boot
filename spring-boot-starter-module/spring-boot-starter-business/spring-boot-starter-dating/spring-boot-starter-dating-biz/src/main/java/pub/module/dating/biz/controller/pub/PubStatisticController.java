package pub.module.dating.biz.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;


/**
 * 公开-我的红娘
 *
 * @author tg
 *  2026-01-07 23:30:24
 */
@Tag(name="公开-移动端首页统计")
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

        @Operation(summary="公开-小程序统计")
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