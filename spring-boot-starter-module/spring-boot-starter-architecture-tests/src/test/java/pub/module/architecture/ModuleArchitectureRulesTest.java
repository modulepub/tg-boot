package pub.module.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * ArchUnit：api 层禁止 SPI；biz/crud 禁止依赖他域实现包。
 */
class ModuleArchitectureRulesTest {

    private static final Pattern MODULE_DOMAIN =
            Pattern.compile("pub\\.module\\.([a-z][a-z0-9]*)\\.(biz|crud)\\.");

    private static JavaClasses importedClasses;

    @BeforeAll
    static void importProjectClasses() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("pub.module");
    }

    @Test
    void apiPackagesMustNotDefineSpiInterfaces() {
        ArchRule rule = noClasses()
                .that().haveSimpleNameStartingWith("Spi")
                .should().resideInAnyPackage("..api..")
                .because("SPI 仅允许定义在 *-biz/service，契约接口使用 Api 前缀放在 *-api");

        rule.check(importedClasses);
    }

    @Test
    void bizAndCrudMustNotDependOnOtherModulesImplementationPackages() {
        ArchRule rule = noClasses()
                .that().resideInAnyPackage("pub.module..biz..", "pub.module..crud..")
                .should(notDependOnOtherModuleBizOrCrud())
                .because("跨模块只允许依赖 *-api；他域 biz/crud 实现不可直接引用");

        rule.check(importedClasses);
    }

    private static ArchCondition<JavaClass> notDependOnOtherModuleBizOrCrud() {
        return new ArchCondition<>("not depend on other modules' biz/crud packages") {
            @Override
            public void check(JavaClass source, ConditionEvents events) {
                String sourceDomain = extractDomain(source.getPackageName());
                if (sourceDomain == null) {
                    return;
                }
                source.getDirectDependenciesFromSelf().forEach(dependency -> {
                    JavaClass target = dependency.getTargetClass();
                    String targetPackage = target.getPackageName();
                    String targetDomain = extractDomain(targetPackage);
                    if (targetDomain == null || sourceDomain.equals(targetDomain)) {
                        return;
                    }
                    if (targetPackage.contains(".biz.") || targetPackage.contains(".crud.")) {
                        String message = source.getFullName() + " 依赖他域实现 " + target.getFullName();
                        events.add(SimpleConditionEvent.violated(source, message));
                    }
                });
            }
        };
    }

    private static String extractDomain(String packageName) {
        Matcher matcher = MODULE_DOMAIN.matcher(packageName + ".");
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
