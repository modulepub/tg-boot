package pub.module.system.biz.config.mybatis;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * mybatis拦截器，自动注入创建人、创建时间、修改人、修改时间，以及查询时自动过滤已删除数据
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})
})
public class MybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        String sqlId = mappedStatement.getId();
        log.debug("sqlId{}", sqlId);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = args[1];
        log.debug("sqlCommandType:{}", sqlCommandType);
        // 处理插入操作
        if (SqlCommandType.INSERT == sqlCommandType) {
            if (parameter != null) {
                handleInsertOperation(parameter);
            }
        }
        // 处理更新操作
        else if (SqlCommandType.UPDATE == sqlCommandType) {
            if (parameter != null) {
                handleUpdateOperation(parameter);
            }
        }

        return invocation.proceed();
    }


    /**
     * 处理插入操作，自动注入创建人、创建时间等字段
     */
    private void handleInsertOperation(Object parameter) {
        Field[] fields = ReflectUtil.getFields(parameter.getClass());
        for (Field field : fields) {
            try {
                // 注入创建时间
                if ("createTime".equals(field.getName()) || "updateTime".equals(field.getName())) {
                    field.setAccessible(true);
                    Object localCreateDate = field.get(parameter);
                    field.setAccessible(false);
                    if (localCreateDate == null || "".equals(localCreateDate)) {
                        field.setAccessible(true);
                        if (field.getType().isAssignableFrom(LocalDateTime.class)) {
                            field.set(parameter, LocalDateTime.now());
                        } else {
                            field.set(parameter, new Date());
                        }
                        field.setAccessible(false);
                    }
                }

                if ("deleted".equals(field.getName())) {
                    field.setAccessible(true);
                    Object deletedObject = field.get(parameter);
                    field.setAccessible(false);
                    if (deletedObject == null) {
                        field.setAccessible(true);
                        field.set(parameter, 0);
                        field.setAccessible(false);
                    }
                }


            } catch (Exception e) {
                log.error("handleInsertOperation", e);
            }
        }
    }

    /**
     * 处理更新操作，自动注入修改人、修改时间等字段
     */
    private void handleUpdateOperation(Object parameter) {
        for (Field field : ReflectUtil.getFields(parameter.getClass())) {
            try {
                if ("updateTime".equals(field.getName())) {
                    if (field.getType().isAssignableFrom(LocalDateTime.class)) {
                        field.setAccessible(true);
                        field.set(parameter, LocalDateTime.now());
                        field.setAccessible(false);
                    } else {
                        field.setAccessible(true);
                        field.set(parameter, new Date());
                        field.setAccessible(false);
                    }

                }
            } catch (Exception e) {
                log.error("handleUpdateOperation", e);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


}
