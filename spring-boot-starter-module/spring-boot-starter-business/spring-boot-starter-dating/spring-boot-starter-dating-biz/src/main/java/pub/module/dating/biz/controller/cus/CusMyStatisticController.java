package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.api.constants.DtLikeDegreeCodeEnum;
import pub.module.dating.curd.entity.DtContact;
import pub.module.dating.curd.entity.DtPreference;
import pub.module.dating.curd.entity.DtRecommended;
import pub.module.dating.curd.service.DtContactService;
import pub.module.dating.curd.service.DtPreferenceService;
import pub.module.dating.curd.service.DtRecommendedService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;


/**
 * 用户端「我的」相关统计
 *
 * @author tg
 * @since 2025-07-21
 * @version V1.0
 */
@Tag(name = "用户端-我的")
@RestController
@RequestMapping("/cus/dating/statistic")
@Slf4j
public class CusMyStatisticController {

	@Resource
	private ApiCustomerService apiCustomerService;
	@Resource
	private DtPreferenceService dtPreferenceService;
	@Resource
	private DtContactService dtContactService;
	@Resource
	private DtRecommendedService dtRecommendedService;

	@Schema(description = "用户端-头部统计")
	@Data
	public static class MyStatistic {
		@Schema(description = "用户端-喜欢我的总数")
		private long likeMeTotal;
		@Schema(description = "用户端-我喜欢的总数")
		private long meLikeTotal;
		@Schema(description = "用户端-好友总数")
		private long friendTotal;
		@Schema(description = "用户端-推荐总数")
		private long recommendTotal;
	}

	@Operation(summary = "用户端-我的-头部统计")
	@GetMapping(value = "/myStatistic")
	public Result<MyStatistic> statisticResult() {
		UserDTO user = UserUtil.getCurrentSysUser();
		String userCode = user.getUserCode();
		CustomerDTO customer = apiCustomerService.getCusByUserCode(userCode);
		String myCusCode = customer.getCusCode();

		MyStatistic stat = new MyStatistic();

		// 喜欢我：偏好表中「目标为我方客户编码」且标记为喜欢（他人对我点了喜欢）
		long likeMe = dtPreferenceService.count(new QueryWrapper<DtPreference>().lambda()
				.eq(DtPreference::getPreferenceTargetCusCode, myCusCode)
				.eq(DtPreference::getPreferenceLikeStatusCode, DtLikeDegreeCodeEnum.like.getCode()));
		stat.setLikeMeTotal(likeMe);

		// 我喜欢：当前用户作为操作方，且为喜欢
		long meLike = dtPreferenceService.count(new QueryWrapper<DtPreference>().lambda()
				.eq(DtPreference::getPreferenceCusCode, myCusCode)
				.eq(DtPreference::getPreferenceLikeStatusCode, DtLikeDegreeCodeEnum.like.getCode()));
		stat.setMeLikeTotal(meLike);

		// 好友：联系人表，归属当前登录用户
		long friends = dtContactService.count(new QueryWrapper<DtContact>().lambda()
				.eq(DtContact::getUserCode, userCode));
		stat.setFriendTotal(friends);

		// 推荐：与「用户端-对象推荐-分页列表」同一过滤条件（排除本人客户编码）
		long reco = dtRecommendedService.count(new QueryWrapper<DtRecommended>().lambda()
				.eq(DtRecommended::getUserCode, userCode));
		stat.setRecommendTotal(reco);

		return Result.ok(stat);
	}

}
