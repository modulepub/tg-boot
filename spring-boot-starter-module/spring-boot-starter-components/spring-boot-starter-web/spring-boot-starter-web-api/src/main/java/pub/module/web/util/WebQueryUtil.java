package pub.module.web.util;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Web查询工具类
 * 构建查询条件
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
public class WebQueryUtil {

    @SneakyThrows
    public static <T> QueryWrapper<T> buildQuery(T searchObj) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Map<String, String[]> parameterMap = new HashMap<>();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            parameterMap = request.getParameterMap();
        }
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        for (Field field : ReflectUtil.getFields(searchObj.getClass())) {
            TableField tableField = field.getAnnotation(TableField.class);
            if(tableField != null){
                boolean exist = tableField.exist();
                if(!exist){
                    continue;
                }
            }
            String fieldName = field.getName();
            field.setAccessible(true);
            Object fieldValue = field.get(searchObj);
            if (fieldValue != null) {
                if (fieldValue instanceof String && StrUtil.contains(fieldValue.toString(), "%")) {
                    String searchValue = fieldValue.toString().replace("%", "");
                    queryWrapper.like(StrUtil.toUnderlineCase(fieldName),searchValue );
                } else {
                    queryWrapper.eq(StrUtil.toUnderlineCase(fieldName), fieldValue);
                }
            }
        }
        boolean notExistSortParam = true;
        if (parameterMap != null) {
            for (String key : parameterMap.keySet()) {
                String value = parameterMap.get(key)[0];
                // 查大小 BEGIN
                if (key.contains("_gt")) {
                    queryWrapper.gt(StrUtil.toUnderlineCase(key.replace("_gt", "")), value);
                }
                if (key.contains("_ge")) {
                    queryWrapper.ge(StrUtil.toUnderlineCase(key.replace("_ge", "")), value);
                }
                if (key.contains("_le")) {
                    queryWrapper.le(StrUtil.toUnderlineCase(key.replace("_le", "")), value);
                }
                if (key.contains("_lt")) {
                    queryWrapper.lt(StrUtil.toUnderlineCase(key.replace("_lt", "")), value);
                }
                // 查大小 END
                //排序
                if ("sortBy".equals(key) && StrUtil.isNotEmpty(value)) {
                    notExistSortParam = false;
                    String[] sortFields = value.split(",");
                    for (String sortField : sortFields) {
                        if (sortField.contains("-")) {
                            queryWrapper.orderByDesc(StrUtil.toUnderlineCase(sortField).replace("-", ""));
                        } else {
                            queryWrapper.orderByAsc(StrUtil.toUnderlineCase(sortField).replace("-", ""));
                        }
                    }
                }
            }
        }
        if (notExistSortParam) {
            queryWrapper.orderByDesc(StrUtil.toUnderlineCase("createTime"));
        }
        return queryWrapper;
    }
}
