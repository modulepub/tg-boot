package pub.module.dating.biz.mock.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.api.constants.CusKinshipCodeEnum;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.IdentityApplyProcessCodeEnum;
import pub.module.dating.api.constants.CusSourceCodeEnum;
import pub.module.dating.api.service.ApiCustomerRedundantSyncService;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.ApiDtCusMatchmakerRelService;
import pub.module.dating.api.service.ApiDtMatchmakerService;
import pub.module.dating.biz.mock.support.DatingMockDataGenerator;
import pub.module.dating.biz.mock.support.DatingMockDataGenerator.CityPick;
import pub.module.dating.biz.mock.support.DatingMockDataGenerator.EduPick;
import pub.module.dating.biz.mock.support.DatingMockPaths;
import pub.module.dating.biz.mock.support.DatingMockPhotoBundle;
import pub.module.dating.biz.mock.support.DatingMockSeedResult;
import pub.module.dating.biz.service.InitGoodsService;
import pub.module.dating.crud.entity.DtCusMatchmakerRel;
import pub.module.dating.crud.entity.DtCustomer;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.entity.DtMatchmakingCompany;
import pub.module.dating.crud.service.DtCusMatchmakerRelService;
import pub.module.dating.crud.service.DtCustomerService;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.dating.crud.service.DtMatchmakingCompanyService;
import pub.module.file.api.service.BizUploadService;
import pub.module.system.api.constants.UserSexCodeEnum;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.trade.api.dto.TdGoodsDTO;
import pub.module.trade.api.service.ApiTdGoodsService;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 从 mock 目录导入：公司 → 红娘 → 男女客户 → 随机关联红娘。
 * <p>SysUser、DtCustomer、DtMatchmaker 共用同一手机号，客户与红娘账号均可手机号+验证码（666666）登录。</p>
 */
@Slf4j
@Service
public class DatingMockSeedService {

    private static final StatusCodeEnum TEST = StatusCodeEnum.YES;

    @Resource
    private DtMatchmakingCompanyService dtMatchmakingCompanyService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;
    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private ApiDtCusMatchmakerRelService apiDtCusMatchmakerRelService;
    @Resource
    private ApiDtMatchmakerService apiDtMatchmakerService;
    @Resource
    private DtCusMatchmakerRelService dtCusMatchmakerRelService;
    @Resource
    private InitGoodsService initGoodsService;
    @Resource
    private BizUploadService bizUploadService;
    @Resource
    private ApiTdGoodsService apiTdGoodsService;
    @Resource
    private DtCustomerService dtCustomerService;
    @Resource
    private ApiCustomerRedundantSyncService apiCustomerRedundantSyncService;

    @Transactional(rollbackFor = Exception.class)
    public DatingMockSeedResult seed(String mockRootOverride) {
        return seed(mockRootOverride, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public DatingMockSeedResult seed(String mockRootOverride, boolean force) {
        Path root = DatingMockPaths.resolveRoot(mockRootOverride);
        DatingMockSeedResult result = new DatingMockSeedResult();

        DtMatchmakingCompany company = seedCompany(root, force);
        result.setCompanyCode(company.getMkCompanyCode());
        result.setCompanyName(company.getMkCompanyName());

        List<DtMatchmaker> matchmakers = seedMatchmakers(root, company, force, result);
        result.setMatchmakerCount(matchmakers.size());

        List<DtCustomerDTO> males = seedCustomers(root.resolve(DatingMockPaths.DIR_MALE), UserSexCodeEnum.MAN,
                DatingMockPaths.PHONE_MALE_BASE, force, result);
        List<DtCustomerDTO> females = seedCustomers(root.resolve(DatingMockPaths.DIR_FEMALE), UserSexCodeEnum.WOMAN,
                DatingMockPaths.PHONE_FEMALE_BASE, force, result);
        result.setMaleCustomerCount(males.size());
        result.setFemaleCustomerCount(females.size());

        int relCount = bindCustomersToMatchmakers(males, females, matchmakers);
        result.setRelationCount(relCount);
        result.setMessage("mock 数据导入完成，素材目录: " + root);
        log.info("dating mock seed done: {}", result);
        return result;
    }

    private DtMatchmakingCompany seedCompany(Path root, boolean force) {
        Path companyParent = root.resolve(DatingMockPaths.DIR_COMPANY);
        List<Path> companyDirs;
        try {
            companyDirs = Files.list(companyParent).filter(Files::isDirectory).sorted().toList();
        } catch (Exception e) {
            throw new IllegalStateException("读取婚介公司目录失败: " + companyParent, e);
        }
        if (companyDirs.isEmpty()) {
            throw new IllegalStateException("婚介公司目录为空: " + companyParent);
        }
        Path companyDir = companyDirs.get(0);
        String companyName = companyDir.getFileName().toString();
        DtMatchmakingCompany existed = dtMatchmakingCompanyService.getOne(new QueryWrapper<DtMatchmakingCompany>().lambda()
                .eq(DtMatchmakingCompany::getMkCompanyName, companyName), false);
        if (existed != null && !needsCompanyRefresh(existed, force)) {
            return existed;
        }
        DatingMockPhotoBundle photos = DatingMockPhotoBundle.scanSingleDir(companyDir);
        String photoUrl = uploadFirst(photos);

        DtMatchmakingCompany company = existed != null ? existed : new DtMatchmakingCompany();
        company.setMkCompanyName(companyName);
        company.setMkCompanyTel("0633-8788889");
        company.setMkCompanyUsciCode("91371102MAD1FH7H3T");
        company.setMkCompanyLegalName("李尧");
        company.setMkCompanyLegalIdNo("510101198501011234");
        company.setMkCompanyIdentityStatusCode(StatusCodeEnum.NO);
        company.setMkCompanyIdentityProcessCode(IdentityApplyProcessCodeEnum.DRAFT);
        company.setMkCompanyAddressDetail("中国山东省日照市东港区石臼街道海滨四路东、天津路北书香园 134 幢 5 单元 205 商");
        company.setMkCompanyAddressLatLon("119.531028,35.389456");
        company.setMkCompanyTestStatusCode(TEST);
        if (StrUtil.isNotBlank(photoUrl)) {
            company.setMkCompanyPhotos(photoUrl);
        }
        if (existed != null) {
            dtMatchmakingCompanyService.updateById(company);
        } else {
            dtMatchmakingCompanyService.save(company);
        }
        return company;
    }

    private List<DtMatchmaker> seedMatchmakers(Path root, DtMatchmakingCompany company, boolean force, DatingMockSeedResult result) {
        List<DatingMockPhotoBundle> bundles = DatingMockPhotoBundle.scanPersonDirs(root.resolve(DatingMockPaths.DIR_MATCHMAKER));
        List<DtMatchmaker> saved = new ArrayList<>();
        int phoneSeq = 0;
        for (DatingMockPhotoBundle bundle : bundles) {
            String phone = String.valueOf(DatingMockPaths.PHONE_MK_BASE + phoneSeq++);
            Random r = DatingMockDataGenerator.seeded("mk:" + bundle.displayName());
            CityPick city = DatingMockDataGenerator.city(r);

            DtMatchmaker mk = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
                    .eq(DtMatchmaker::getMkName, bundle.displayName())
                    .eq(DtMatchmaker::getMkCompanyCode, company.getMkCompanyCode())
                    .eq(DtMatchmaker::getMkTestStatusCode, TEST.getCode()), false);
            if (mk == null) {
                mk = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
                        .eq(DtMatchmaker::getMkPhone, phone)
                        .eq(DtMatchmaker::getMkTestStatusCode, TEST.getCode()), false);
            }
            boolean isNew = mk == null;
            if (mk == null) {
                mk = new DtMatchmaker();
            } else {
                phone = mk.getMkPhone();
            }

            String workPhoto = uploadFirst(bundle);
            if (StrUtil.isBlank(workPhoto) && bundle.imageFiles() != null && !bundle.imageFiles().isEmpty()) {
                log.warn("mock 红娘 {} 工作照上传失败", bundle.displayName());
            }
            UserDTO user = ensureLoginUser(phone, bundle.displayName(), UserSexCodeEnum.WOMAN, workPhoto);
            mk.setMkUserCode(user.getUserCode());
            mk.setMkPhone(phone);
            mk.setMkName(bundle.displayName());
            mk.setMkAge(DatingMockDataGenerator.age(r, 28, 39));
            mk.setMkWorkPhoto(workPhoto);
            mk.setMkCompanyCode(company.getMkCompanyCode());
            mk.setMkCompanyName(company.getMkCompanyName());
            mk.setMkCityCode(city.code());
            mk.setMkCityName(city.fullName());
            mk.setMkMoment(DatingMockDataGenerator.matchmakerMoment(r));
            mk.setMkTags(DatingMockDataGenerator.matchmakerTags(r));
            mk.setMkIdentityStatusCode(StatusCodeEnum.YES);
            mk.setMkIdentityProcessCode(IdentityApplyProcessCodeEnum.APPROVED);
            mk.setMkScore(DatingMockDataGenerator.matchmakerScore(r));
            mk.setMkServiceUserCount(50L + r.nextInt(200));
            mk.setMkIdNo(DatingMockDataGenerator.mockIdNo(r, UserSexCodeEnum.WOMAN));
            mk.setMkTestStatusCode(TEST);

            if (isNew) {
                dtMatchmakerService.save(mk);
            } else {
                dtMatchmakerService.updateById(mk);
            }
            initGoodsService.initByMk(mk);
            markMatchmakerGoodsAsTest(mk.getMkUserCode());
            apiDtMatchmakerService.syncUserRealNameFromMatchmaker(mk.getMkUserCode(), mk.getMkName());
            saved.add(mk);
            result.getMatchmakerPhones().add(phone);
        }
        return saved;
    }

    private List<DtCustomerDTO> seedCustomers(Path categoryDir, UserSexCodeEnum sex, long phoneBase, boolean force,
                                            DatingMockSeedResult result) {
        List<DatingMockPhotoBundle> bundles = DatingMockPhotoBundle.scanPersonDirs(categoryDir);
        List<DtCustomerDTO> customers = new ArrayList<>();
        int phoneSeq = 0;
        for (DatingMockPhotoBundle bundle : bundles) {
            Random r = DatingMockDataGenerator.seeded("cus:" + bundle.displayName() + ":" + sex.getCode());
            List<String> imageUrls = uploadPaths(bundle.imageFiles());
            List<String> videoUrls = uploadPaths(bundle.videoFiles());
            String avatar = imageUrls.isEmpty() ? null : imageUrls.get(0);
            List<String> lifeParts = new ArrayList<>();
            if (imageUrls.size() > 1) {
                lifeParts.addAll(imageUrls.subList(1, imageUrls.size()));
            }
            lifeParts.addAll(videoUrls);
            String lifePhoto = lifeParts.isEmpty() ? null : String.join(",", lifeParts);
            if (imageUrls.isEmpty() && videoUrls.isEmpty()) {
                log.warn("mock 嘉宾 {} 未找到可上传的图片或视频", bundle.displayName());
            } else if (imageUrls.isEmpty() && !bundle.imageFiles().isEmpty()) {
                log.warn("mock 嘉宾 {} 图片上传失败，本地共 {} 张", bundle.displayName(), bundle.imageFiles().size());
            } else if (videoUrls.isEmpty() && !bundle.videoFiles().isEmpty()) {
                log.warn("mock 嘉宾 {} 视频上传失败，本地共 {} 个", bundle.displayName(), bundle.videoFiles().size());
            }

            DtCustomerDTO existingByName = findMockCustomerByName(bundle.displayName(), sex);
            String phone;
            UserDTO user;
            if (existingByName != null && StrUtil.isNotBlank(existingByName.getCusPhone())) {
                phone = existingByName.getCusPhone().trim();
                user = apiSysUserService.getUserByUserCode(existingByName.getCusUserCode());
                if (user == null) {
                    user = ensureLoginUser(phone, bundle.displayName(), sex, avatar);
                } else {
                    user = ensureLoginUser(phone, bundle.displayName(), sex, avatar);
                }
            } else {
                phone = String.valueOf(phoneBase + phoneSeq++);
                user = ensureLoginUser(phone, bundle.displayName(), sex, avatar);
            }

            if (existingByName == null) {
                apiDtCustomerService.initCustomerByUser(user);
            }
            String nickSeed = "cus:" + bundle.displayName() + ":" + sex.getCode();
            DtCustomerDTO patch = buildCustomerPatch(bundle.displayName(), sex, phone, avatar, lifePhoto, r, nickSeed);
            log.info("mock 更新客户 userCode={} cusName={} cusNickName={}",
                    user.getUserCode(), patch.getCusName(), patch.getCusNickName());
            DtCustomerDTO dto = apiDtCustomerService.updateMockCustomerPartial(user.getUserCode(), patch);
            apiCustomerRedundantSyncService.syncSnapshotAfterProfileUpdated(user.getUserCode(), dto);
            customers.add(dto);
            result.getCustomerPhones().add(phone);
        }
        return customers;
    }

    private DtCustomerDTO buildCustomerPatch(String name, UserSexCodeEnum sex, String phone,
                                           String avatar, String lifePhoto, Random r, String nickSeed) {
        CityPick city = DatingMockDataGenerator.city(r);
        EduPick edu = DatingMockDataGenerator.education(r);
        DtCustomerDTO patch = new DtCustomerDTO();
        patch.setCusName(name);
        patch.setCusNickName(DatingMockDataGenerator.nickname(nickSeed, sex));
        patch.setCusPhone(phone);
        patch.setCusSexCode(sex);
        patch.setCusAvatar(avatar);
        patch.setCusLifePhoto(lifePhoto);
        patch.setCusAge((long) DatingMockDataGenerator.age(r, 21, 32));
        patch.setCusHeight(DatingMockDataGenerator.heightCm(r, sex));
        patch.setCusWeight(DatingMockDataGenerator.weightKg(r, sex));
        patch.setCusCityResidenceCode(city.code());
        patch.setCusCityResidenceName(city.fullName());
        patch.setCusEducationCode(edu.code());
        patch.setCusEducationName(edu.name());
        patch.setCusMaritalStatusCode(StatusCodeEnum.NO);
        patch.setCusRemarriageStatusCode(StatusCodeEnum.fromJson("0"));
        patch.setCusDisabledStatusCode(StatusCodeEnum.fromJson("0"));
        patch.setCusHaveHouseStatusCode(StatusCodeEnum.fromJson("1"));
        patch.setCusHaveCarStatusCode(StatusCodeEnum.fromJson("1"));
        patch.setCusKinshipCode(CusKinshipCodeEnum.SELF);
        patch.setCusMoment(DatingMockDataGenerator.moment(r, sex));
        patch.setCusOccupationalDescription(r.nextBoolean() ? "企业管理" : "专业技术");
        long incomeWan = 8L + r.nextInt(25);
        patch.setCusAnnualIncomeAmount(BigDecimal.valueOf(incomeWan * 10_000L));
        patch.setCusComleteProfileStatusCode(StatusCodeEnum.fromJson("1"));
        patch.setCusIdentityAuthenticatedStatusCode(StatusCodeEnum.fromJson("1"));
        patch.setCusSourceCode(CusSourceCodeEnum.IMPORT);
        patch.setCusLsStatusCode(StatusCodeEnum.fromJson("0"));
        patch.setCusTestStatusCode(TEST);
        return patch;
    }

    private UserDTO ensureLoginUser(String phone, String realName, UserSexCodeEnum sex, String avatar) {
        UserDTO user = apiSysUserService.registerByPhone(phone, null);
        UserDTO latest = apiSysUserService.getUserByUserCode(user.getUserCode());
        boolean needUpdate = !StrUtil.equals(realName, StrUtil.trim(latest.getUserRealName()))
                || latest.getUserSexCode() == null
                || latest.getUserTestStatusCode() != TEST
                || (StrUtil.isNotBlank(avatar) && !StrUtil.equals(avatar, StrUtil.trim(latest.getUserAvatar())));
        if (needUpdate) {
            UserDTO patch = new UserDTO();
            patch.setUserCode(user.getUserCode());
            patch.setUserPhone(phone);
            patch.setUserRealName(realName);
            patch.setUserSexCode(sex);
            patch.setUserTestStatusCode(TEST);
            if (StrUtil.isNotBlank(avatar)) {
                patch.setUserAvatar(avatar);
            }
            apiSysUserService.updateById(patch);
            latest = apiSysUserService.getUserByUserCode(user.getUserCode());
        }
        return latest;
    }

    private int bindCustomersToMatchmakers(List<DtCustomerDTO> males, List<DtCustomerDTO> females, List<DtMatchmaker> matchmakers) {
        if (matchmakers.isEmpty()) {
            return 0;
        }
        List<DtCustomerDTO> all = new ArrayList<>();
        all.addAll(males);
        all.addAll(females);
        int count = 0;
        Random relRandom = new Random(20260521L);
        for (DtCustomerDTO cus : all) {
            if (cus == null || StrUtil.isBlank(cus.getCusUserCode()) || StrUtil.isBlank(cus.getCusCode())) {
                continue;
            }
            DtMatchmaker mk = matchmakers.get(relRandom.nextInt(matchmakers.size()));
            if (StrUtil.isBlank(mk.getMkCode())) {
                continue;
            }
            long existed = dtCusMatchmakerRelService.lambdaQuery()
                    .eq(DtCusMatchmakerRel::getCusCode, cus.getCusCode())
                    .eq(DtCusMatchmakerRel::getMkCode, mk.getMkCode())
                    .count();
            if (existed > 0) {
                refreshRelSnapshot(cus, mk);
                continue;
            }
            apiDtCusMatchmakerRelService.relateCustomerWithMatchmakerByMkCodeIfAbsent(cus.getCusUserCode(), mk.getMkCode());
            refreshRelSnapshot(cus, mk);
            count++;
        }
        return count;
    }

    private String uploadFirst(DatingMockPhotoBundle bundle) {
        List<String> urls = uploadPaths(bundle.imageFiles());
        return urls.isEmpty() ? null : urls.get(0);
    }

    private List<String> uploadPaths(List<Path> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        JSONObject config = bizUploadService.getConfig();
        String prefix = StrUtil.nullToDefault(config.getStr("urlPrefix"), "");
        return files.stream()
                .map(path -> {
                    try {
                        String filePath = bizUploadService.upload(path.toFile(), DatingMockPaths.UPLOAD_BIZ);
                        return prefix + filePath;
                    } catch (Exception ex) {
                        log.warn("上传 mock 媒体失败 {}: {}", path, ex.getMessage());
                        return null;
                    }
                })
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }

    /** mock 再次执行时按真实姓名 + 性别 + 测试标记查找已有客户，存在则全量更新 */
    private DtCustomerDTO findMockCustomerByName(String name, UserSexCodeEnum sex) {
        String trimmed = StrUtil.trim(name);
        if (StrUtil.isBlank(trimmed) || sex == null) {
            return null;
        }
        DtCustomer byName = dtCustomerService.getOne(new QueryWrapper<DtCustomer>().lambda()
                .eq(DtCustomer::getCusName, trimmed)
                .eq(DtCustomer::getCusSexCode, sex.getCode())
                .eq(DtCustomer::getCusTestStatusCode, TEST), false);
        if (byName != null) {
            return BeanUtil.copyProperties(byName, DtCustomerDTO.class);
        }
        List<DtCustomer> legacy = dtCustomerService.list(new QueryWrapper<DtCustomer>().lambda()
                .eq(DtCustomer::getCusName, trimmed)
                .eq(DtCustomer::getCusSexCode, sex.getCode()));
        for (DtCustomer candidate : legacy) {
            if (candidate == null || StrUtil.isBlank(candidate.getCusUserCode())) {
                continue;
            }
            UserDTO user = apiSysUserService.getUserByUserCode(candidate.getCusUserCode());
            if (user != null && TEST.equals(user.getUserTestStatusCode())) {
                return BeanUtil.copyProperties(candidate, DtCustomerDTO.class);
            }
        }
        return null;
    }

    private void markMatchmakerGoodsAsTest(String mkUserCode) {
        if (StrUtil.isBlank(mkUserCode)) {
            return;
        }
        try {
            for (TdGoodsDTO goods : apiTdGoodsService.listByTdGdSysUserCode(mkUserCode)) {
                if (StrUtil.isBlank(goods.getId())) {
                    continue;
                }
                goods.setTdGdTestStatusCode(StatusCodeEnum.YES.getCode());
                apiTdGoodsService.updateGoods(goods);
            }
        } catch (Exception ex) {
            log.warn("标记测试商品失败 mkUserCode={}: {}", mkUserCode, ex.getMessage());
        }
    }

    private boolean needsCompanyRefresh(DtMatchmakingCompany company, boolean force) {
        if (force) {
            return true;
        }
        if (company.getMkCompanyTestStatusCode() != TEST) {
            return true;
        }
        return StrUtil.isBlank(company.getMkCompanyPhotos());
    }

    private void refreshRelSnapshot(DtCustomerDTO cus, DtMatchmaker mk) {
        if (cus == null || mk == null || StrUtil.isBlank(cus.getCusCode()) || StrUtil.isBlank(mk.getMkCode())) {
            return;
        }
        dtCusMatchmakerRelService.lambdaUpdate()
                .eq(DtCusMatchmakerRel::getCusCode, cus.getCusCode())
                .eq(DtCusMatchmakerRel::getMkCode, mk.getMkCode())
                .set(DtCusMatchmakerRel::getCusName, cus.getCusName())
                .set(DtCusMatchmakerRel::getCusNickName, StrUtil.trimToNull(cus.getCusNickName()))
                .set(DtCusMatchmakerRel::getCusIdentityAuthenticatedStatusCode, cus.getCusIdentityAuthenticatedStatusCode())
                .set(DtCusMatchmakerRel::getCusAvatar, cus.getCusAvatar())
                .set(DtCusMatchmakerRel::getCusSexCode, cus.getCusSexCode())
                .set(DtCusMatchmakerRel::getCusMoment, cus.getCusMoment())
                .set(DtCusMatchmakerRel::getCusHiddenStatusCode, cus.getCusHiddenStatusCode())
                .set(DtCusMatchmakerRel::getCusPhone, StrUtil.trimToNull(cus.getCusPhone()))
                .set(DtCusMatchmakerRel::getMkUserCode, mk.getMkUserCode())
                .set(DtCusMatchmakerRel::getMkWorkPhoto, mk.getMkWorkPhoto())
                .set(DtCusMatchmakerRel::getMkName, mk.getMkName())
                .set(DtCusMatchmakerRel::getMkIdNo, mk.getMkIdNo())
                .set(DtCusMatchmakerRel::getMkCompanyCode, mk.getMkCompanyCode())
                .set(DtCusMatchmakerRel::getMkCompanyName, mk.getMkCompanyName())
                .set(DtCusMatchmakerRel::getMkCityCode, mk.getMkCityCode())
                .set(DtCusMatchmakerRel::getMkCityName, mk.getMkCityName())
                .set(DtCusMatchmakerRel::getMkMoment, mk.getMkMoment())
                .set(DtCusMatchmakerRel::getMkIdentityStatusCode, mk.getMkIdentityStatusCode())
                .set(DtCusMatchmakerRel::getMkScore, mk.getMkScore())
                .set(DtCusMatchmakerRel::getMkPhone, mk.getMkPhone())
                .set(DtCusMatchmakerRel::getCusMkRelTestStatusCode, TEST)
                .update();
    }
}
