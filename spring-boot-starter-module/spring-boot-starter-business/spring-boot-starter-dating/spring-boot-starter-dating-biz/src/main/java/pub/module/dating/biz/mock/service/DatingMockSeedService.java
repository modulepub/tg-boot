package pub.module.dating.biz.mock.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.customer.api.constants.CusSourceCodeEnum;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.customer.curd.entity.Customer;
import pub.module.customer.curd.service.CustomerService;
import pub.module.dating.api.service.ApiDtCusMatchmakerRelService;
import pub.module.dating.biz.mock.support.DatingMockDataGenerator;
import pub.module.dating.biz.mock.support.DatingMockDataGenerator.CityPick;
import pub.module.dating.biz.mock.support.DatingMockDataGenerator.EduPick;
import pub.module.dating.biz.mock.support.DatingMockPaths;
import pub.module.dating.biz.mock.support.DatingMockPhotoBundle;
import pub.module.dating.biz.mock.support.DatingMockSeedResult;
import pub.module.dating.biz.service.InitGoodsService;
import pub.module.dating.curd.entity.DtCusMatchmakerRel;
import pub.module.dating.curd.entity.DtMatchmaker;
import pub.module.dating.curd.entity.DtMatchmakingCompany;
import pub.module.dating.curd.service.DtCusMatchmakerRelService;
import pub.module.dating.curd.service.DtMatchmakerService;
import pub.module.dating.curd.service.DtMatchmakingCompanyService;
import pub.module.file.api.service.BizUploadService;
import pub.module.system.api.constants.UserSexCodeEnum;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 从 mock 目录导入：公司 → 红娘 → 男女客户 → 随机关联红娘。
 * <p>SysUser、Customer、DtMatchmaker 共用同一手机号，客户与红娘账号均可手机号+验证码（666666）登录。</p>
 */
@Slf4j
@Service
public class DatingMockSeedService {

    @Resource
    private DtMatchmakingCompanyService dtMatchmakingCompanyService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private CustomerService customerService;
    @Resource
    private ApiCustomerService apiCustomerService;
    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private ApiDtCusMatchmakerRelService apiDtCusMatchmakerRelService;
    @Resource
    private DtCusMatchmakerRelService dtCusMatchmakerRelService;
    @Resource
    private InitGoodsService initGoodsService;
    @Resource
    private BizUploadService bizUploadService;

    @Transactional(rollbackFor = Exception.class)
    public DatingMockSeedResult seed(String mockRootOverride, boolean force) {
        Path root = DatingMockPaths.resolveRoot(mockRootOverride);
        DatingMockSeedResult result = new DatingMockSeedResult();

        DtMatchmakingCompany company = seedCompany(root, force);
        result.setCompanyCode(company.getMkCompanyCode());
        result.setCompanyName(company.getMkCompanyName());

        List<DtMatchmaker> matchmakers = seedMatchmakers(root, company, force, result);
        result.setMatchmakerCount(matchmakers.size());

        List<CustomerDTO> males = seedCustomers(root.resolve(DatingMockPaths.DIR_MALE), UserSexCodeEnum.MAN,
                DatingMockPaths.PHONE_MALE_BASE, force, result);
        List<CustomerDTO> females = seedCustomers(root.resolve(DatingMockPaths.DIR_FEMALE), UserSexCodeEnum.WOMAN,
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
        if (existed != null && !force) {
            return existed;
        }
        DatingMockPhotoBundle photos = DatingMockPhotoBundle.scanSingleDir(companyDir);
        String photoUrl = uploadFirst(photos);

        DtMatchmakingCompany company = existed != null ? existed : new DtMatchmakingCompany();
        company.setMkCompanyName(companyName);
        company.setMkCompanyTel("028-88886666");
        company.setMkCompanyUsciCode("91510100MA6C" + String.format("%06d", Math.abs(companyName.hashCode() % 1_000_000)));
        company.setMkCompanyLegalName("张卿卿");
        company.setMkCompanyLegalIdNo("510101198501011234");
        company.setMkCompanyIdentityStatusCode("1");
        company.setMkCompanyAddressDetail("四川省成都市锦江区春熙路88号卿卿之恋大厦");
        company.setMkCompanyAddressLatLon("104.080989,30.657689");
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
            UserDTO user = ensureLoginUser(phone, bundle.displayName(), UserSexCodeEnum.WOMAN, uploadFirst(bundle), force);

            DtMatchmaker mk = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
                    .eq(DtMatchmaker::getMkPhone, phone), false);
            if (mk == null) {
                mk = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
                        .eq(DtMatchmaker::getMkName, bundle.displayName())
                        .eq(DtMatchmaker::getMkCompanyCode, company.getMkCompanyCode()), false);
            }
            boolean isNew = mk == null;
            if (mk == null) {
                mk = new DtMatchmaker();
            } else if (!force) {
                saved.add(mk);
                result.getMatchmakerPhones().add(phone);
                continue;
            }

            String workPhoto = uploadFirst(bundle);
            mk.setMkUserCode(user.getUserCode());
            mk.setMkPhone(phone);
            mk.setMkName(bundle.displayName());
            mk.setMkAge(DatingMockDataGenerator.age(r, 28, 48));
            mk.setMkWorkPhoto(workPhoto);
            mk.setMkCompanyCode(company.getMkCompanyCode());
            mk.setMkCompanyName(company.getMkCompanyName());
            mk.setMkCityCode(city.code());
            mk.setMkCityName(city.fullName());
            mk.setMkMoment(DatingMockDataGenerator.moment(r));
            mk.setMkTags(DatingMockDataGenerator.matchmakerTags(r));
            mk.setMkIdentityStatusCode("1");
            mk.setMkScore(BigDecimal.valueOf(4.5 + r.nextDouble()));
            mk.setMkServiceUserCount(50L + r.nextInt(200));
            mk.setMkIdNo(DatingMockDataGenerator.mockIdNo(r, UserSexCodeEnum.WOMAN));

            if (isNew) {
                dtMatchmakerService.save(mk);
                initGoodsService.initByMk(mk);
            } else {
                dtMatchmakerService.updateById(mk);
            }
            saved.add(mk);
            result.getMatchmakerPhones().add(phone);
        }
        return saved;
    }

    private List<CustomerDTO> seedCustomers(Path categoryDir, UserSexCodeEnum sex, long phoneBase, boolean force,
                                            DatingMockSeedResult result) {
        List<DatingMockPhotoBundle> bundles = DatingMockPhotoBundle.scanPersonDirs(categoryDir);
        List<CustomerDTO> customers = new ArrayList<>();
        int phoneSeq = 0;
        for (DatingMockPhotoBundle bundle : bundles) {
            String phone = String.valueOf(phoneBase + phoneSeq++);
            Random r = DatingMockDataGenerator.seeded("cus:" + bundle.displayName() + ":" + sex.getCode());
            List<String> urls = uploadAll(bundle);
            String avatar = urls.isEmpty() ? null : urls.get(0);
            String lifePhoto = urls.size() <= 1 ? null : String.join(",", urls);

            UserDTO user = ensureLoginUser(phone, bundle.displayName(), sex, avatar, force);
            Customer customer = customerService.getOne(new QueryWrapper<Customer>().lambda()
                    .eq(Customer::getCusPhone, phone), false);
            if (customer == null) {
                customer = customerService.getOne(new QueryWrapper<Customer>().lambda()
                        .eq(Customer::getCusUserCode, user.getUserCode()), false);
            }
            if (customer != null && !force) {
                CustomerDTO dto = apiCustomerService.getCusByUserCode(user.getUserCode());
                syncSysUserNickNameWithCustomer(user.getUserCode(), dto);
                customers.add(dto);
                result.getCustomerPhones().add(phone);
                continue;
            }

            apiCustomerService.initCustomerByUser(user);
            Map<String, Object> patch = buildCustomerPatch(bundle.displayName(), sex, phone, avatar, lifePhoto, r);
            CustomerDTO dto = apiCustomerService.updateCurrCustomerPartial(user.getUserCode(), patch);
            syncSysUserNickNameWithCustomer(user.getUserCode(), dto);
            customers.add(dto);
            result.getCustomerPhones().add(phone);
        }
        return customers;
    }

    private Map<String, Object> buildCustomerPatch(String name, UserSexCodeEnum sex, String phone,
                                                   String avatar, String lifePhoto, Random r) {
        CityPick city = DatingMockDataGenerator.city(r);
        EduPick edu = DatingMockDataGenerator.education(r);
        Map<String, Object> patch = new LinkedHashMap<>();
        patch.put("cusName", name);
        patch.put("cusNickName", name);
        patch.put("cusPhone", phone);
        patch.put("cusSexCode", sex.getCode());
        patch.put("cusAvatar", avatar);
        patch.put("cusLifePhoto", lifePhoto);
        patch.put("cusAge", (long) DatingMockDataGenerator.age(r, 24, 42));
        patch.put("cusHeight", DatingMockDataGenerator.heightCm(r, sex));
        patch.put("cusWeight", DatingMockDataGenerator.weightKg(r, sex));
        patch.put("cusCityResidenceCode", city.code());
        patch.put("cusCityResidenceName", city.fullName());
        patch.put("cusEducationCode", edu.code());
        patch.put("cusEducationName", edu.name());
        patch.put("cusMaritalStatusCode", "unmarried");
        patch.put("cusRemarriageStatusCode", "0");
        patch.put("cusDisabledStatusCode", "0");
        patch.put("cusHaveHouseStatusCode", r.nextBoolean() ? "1" : "0");
        patch.put("cusHaveCarStatusCode", r.nextBoolean() ? "1" : "0");
        patch.put("cusKinshipCode", "self");
        patch.put("cusMoment", DatingMockDataGenerator.moment(r));
        patch.put("cusOccupationalDescription", r.nextBoolean() ? "企业管理" : "专业技术");
        // 界面为万元，库内为元（与 parseAnnualIncomeInput 一致）
        long incomeWan = 8L + r.nextInt(25);
        patch.put("cusAnnualIncomeAmount", BigDecimal.valueOf(incomeWan * 10_000L));
        patch.put("cusComleteProfileStatusCode", "1");
        patch.put("cusIdentityAuthenticatedStatusCode", "1");
        patch.put("cusSourceCode", CusSourceCodeEnum.IMPORT.getCode());
        patch.put("cusLsStatusCode", "0");
        return patch;
    }

    private UserDTO ensureLoginUser(String phone, String realName, UserSexCodeEnum sex, String avatar, boolean force) {
        UserDTO user = apiSysUserService.registerByPhone(phone, null);
        UserDTO latest = apiSysUserService.getUserByUserCode(user.getUserCode());
        boolean needUpdate = force
                || !StrUtil.equals(realName, StrUtil.trim(latest.getUserRealName()))
                || !StrUtil.equals(realName, StrUtil.trim(latest.getUserNickName()))
                || latest.getUserSexCode() == null
                || (StrUtil.isNotBlank(avatar) && !StrUtil.equals(avatar, StrUtil.trim(latest.getUserAvatar())));
        if (needUpdate) {
            UserDTO patch = new UserDTO();
            patch.setUserCode(user.getUserCode());
            patch.setUserPhone(phone);
            patch.setUserRealName(realName);
            patch.setUserNickName(realName);
            patch.setUserSexCode(sex);
            if (StrUtil.isNotBlank(avatar)) {
                patch.setUserAvatar(avatar);
            }
            apiSysUserService.updateById(patch);
            latest = apiSysUserService.getUserByUserCode(user.getUserCode());
        }
        return latest;
    }

    /** 以 customer.cusNickName 为准，回写 sys_user.user_nick_name */
    private void syncSysUserNickNameWithCustomer(String userCode, CustomerDTO customer) {
        if (StrUtil.isBlank(userCode) || customer == null) {
            return;
        }
        String nick = StrUtil.trim(customer.getCusNickName());
        if (StrUtil.isBlank(nick)) {
            nick = StrUtil.trim(customer.getCusName());
        }
        if (StrUtil.isBlank(nick)) {
            return;
        }
        UserDTO user = apiSysUserService.getUserByUserCode(userCode);
        if (user == null || StrUtil.equals(nick, StrUtil.trim(user.getUserNickName()))) {
            return;
        }
        UserDTO patch = new UserDTO();
        patch.setUserCode(userCode);
        patch.setUserNickName(nick);
        apiSysUserService.updateById(patch);
    }

    private int bindCustomersToMatchmakers(List<CustomerDTO> males, List<CustomerDTO> females, List<DtMatchmaker> matchmakers) {
        if (matchmakers.isEmpty()) {
            return 0;
        }
        List<CustomerDTO> all = new ArrayList<>();
        all.addAll(males);
        all.addAll(females);
        int count = 0;
        Random relRandom = new Random(20260521L);
        for (CustomerDTO cus : all) {
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
                continue;
            }
            apiDtCusMatchmakerRelService.relateCustomerWithMatchmakerByMkCodeIfAbsent(cus.getCusUserCode(), mk.getMkCode());
            count++;
        }
        return count;
    }

    private String uploadFirst(DatingMockPhotoBundle bundle) {
        List<String> urls = uploadAll(bundle);
        return urls.isEmpty() ? null : urls.get(0);
    }

    private List<String> uploadAll(DatingMockPhotoBundle bundle) {
        if (bundle == null || bundle.imageFiles().isEmpty()) {
            return Collections.emptyList();
        }
        JSONObject config = bizUploadService.getConfig();
        String prefix = StrUtil.nullToDefault(config.getStr("urlPrefix"), "");
        return bundle.imageFiles().stream()
                .map(path -> {
                    try {
                        String filePath = bizUploadService.upload(path.toFile(), DatingMockPaths.UPLOAD_BIZ);
                        return prefix + filePath;
                    } catch (Exception ex) {
                        log.warn("上传 mock 图片失败 {}: {}", path, ex.getMessage());
                        return null;
                    }
                })
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }
}
