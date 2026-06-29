package pub.module.architecture;

import com.baomidou.mybatisplus.annotation.TableId;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pub.module.common.model.po.BaseEntity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 实体与 Api 契约层补充规则（与 AGENTS.md 检查清单对齐）。
 */
class EntityAndApiArchitectureRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void importProjectClasses() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("pub.module");
    }

    @Test
    void crudEntitiesMustExtendBaseEntity() {
        ArchRule rule = classes()
                .that().resideInAnyPackage("..crud.entity..")
                .and().areNotInterfaces()
                .should().beAssignableTo(BaseEntity.class)
                .because("持久层实体须继承 BaseEntity（技术主键 id + 审计字段）");

        rule.check(importedClasses);
    }

    @Test
    void tableIdMustNotBeOnBusinessCodeField() {
        ArchRule rule = classes()
                .that().resideInAnyPackage("..crud.entity..")
                .should(notHaveTableIdOnCodeField())
                .because("业务主键 xxxCode 不得标注 @TableId，技术主键仅 BaseEntity.id");

        rule.check(importedClasses);
    }

    @Test
    void apiServicesMustNotUseBizOrCrudTypesInContracts() {
        ArchRule rule = noClasses()
                .that().resideInAnyPackage("..api.service..")
                .and().haveSimpleNameStartingWith("Api")
                .should(dependOnBizOrCrudTypesInPublicApi())
                .because("Api**Service 入参/返回值须为 *-api 内 DTO/VO/枚举，禁止引用 crud.entity 或 biz 实现类型");

        rule.check(importedClasses);
    }

    private static ArchCondition<JavaClass> notHaveTableIdOnCodeField() {
        return new ArchCondition<>("not annotate *Code fields with @TableId") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (JavaField field : javaClass.getFields()) {
                    if (!field.getName().endsWith("Code")) {
                        continue;
                    }
                    if (field.isAnnotatedWith(TableId.class)) {
                        String message = javaClass.getFullName() + "." + field.getName() + " 不得使用 @TableId";
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                    }
                }
            }
        };
    }

    private static ArchCondition<JavaClass> dependOnBizOrCrudTypesInPublicApi() {
        return new ArchCondition<>("not expose biz/crud types in Api service API surface") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                if (!javaClass.isInterface()) {
                    return;
                }
                javaClass.getMethods().forEach(method -> {
                    checkType(javaClass, method.getRawReturnType(), events, "返回值");
                    method.getRawParameterTypes().forEach(paramType ->
                            checkType(javaClass, paramType, events, "参数"));
                });
            }

            private void checkType(JavaClass source, JavaClass type, ConditionEvents events, String role) {
                if (type == null || type.isPrimitive() || type.isEquivalentTo(void.class)) {
                    return;
                }
                String pkg = type.getPackageName();
                if (pkg.contains(".crud.") || (pkg.contains(".biz.") && !pkg.contains(".api."))) {
                    String message = source.getFullName() + " 的" + role + " 使用了实现层类型 " + type.getFullName();
                    events.add(SimpleConditionEvent.violated(source, message));
                }
            }
        };
    }
}
