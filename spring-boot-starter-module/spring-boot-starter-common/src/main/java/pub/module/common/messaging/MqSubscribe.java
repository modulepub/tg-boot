package pub.module.common.messaging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记 {@code XxxConsumer} 嵌套子接口上的消费方槽位（group + Stream function 名）。
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqSubscribe {

    /** consumer group */
    String group();

    /** Spring Cloud Function 名（不含 {@code -in-0} 后缀） */
    String function();
}
