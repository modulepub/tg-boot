package pub.module.dating.biz.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.dating.api.service.BizDtIntentionService;
import pub.module.dating.curd.constants.DtiMatchingRuleCodeEnum;
import pub.module.dating.curd.constants.DtiServiceCompleteStatusCodeEnum;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.curd.entity.DtIntention;
import pub.module.dating.curd.entity.DtRecommended;
import pub.module.dating.curd.service.DtIntentionService;
import pub.module.dating.curd.service.DtRecommendedService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 相亲推荐
 * @author tg
 * @since 2025-05-25
 * @version V1.0
 */
@Slf4j
@Service
public class BizDtIntentionImpl implements BizDtIntentionService {

    @Resource
    private DtIntentionService dtIntentionService;
    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private DtRecommendedService dtRecommendedService;;


    @Override
    public DtIntentionDTO getLastIntention(String userCode) {
        LambdaQueryWrapper<DtIntention> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DtIntention::getIntentionSysUserCode, userCode).orderByDesc(DtIntention::getCreateTime).last("limit 1");
        return BeanUtil.copyProperties(dtIntentionService.getOne(queryWrapper,false),DtIntentionDTO.class);
    }

    @Override
    public DtIntentionDTO initDtIntention(DtIntentionDTO intentionDTO) {
        DtIntention intention = BeanUtil.copyProperties(intentionDTO,DtIntention.class);
        intention.setIntentionMatchesTargetNum(10L);
        IPage<UserDTO> userPage = apiSysUserService.page(new UserDTO(),1, 10);
        List<DtRecommended> recommendedList = new ArrayList<>();
        for(UserDTO user : userPage.getRecords()){
            long count = dtRecommendedService.count(new LambdaQueryWrapper<DtRecommended>().eq(DtRecommended::getRcSysUserCode, intention.getIntentionSysUserCode())
                   .eq(DtRecommended::getRcSysUserCode, user.getUserCode()));
            if(count>0){
                continue;
            }
            DtRecommended recommended = new DtRecommended();
            recommended.setRcSysUserCode(intention.getIntentionSysUserCode());
            recommended.setRcSysUserCode(user.getUserCode());
            recommendedList.add(recommended);
        }
        if(recommendedList.size()==intention.getIntentionMatchesTargetNum()){
            intention.setIntentionSrvCompletedCode(DtiServiceCompleteStatusCodeEnum.COMPLETED.getCode());
        }
        
        intention.setIntentionMatchingRuleCode(DtiMatchingRuleCodeEnum.RECOMMEND.getCode());
        dtIntentionService.save(intention);
        dtRecommendedService.saveBatch(recommendedList);
        log.info("initDtIntention result:{}",intention);
        UserDTO sysUser = apiSysUserService.getUserByUserCode(intention.getIntentionSysUserCode());
        apiSysUserService.updateById(sysUser);
        return BeanUtil.copyProperties(intention,DtIntentionDTO.class);
    }
}
