package pub.module.common.messaging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记 MQ 根契约接口（定义在生产者 {@code *-api} 的 {@code XxxConsumer} 上）。
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqChannel {

    /** Rabbit destination，通常引用同接口内的 {@code DESTINATION} 常量 */
    String destination();

    /** Spring Cloud Function 生产者函数名，通常引用同接口内的 {@code PRODUCER_FUNCTION} 常量 */
    String producerFunction();

    MqChannelMode mode() default MqChannelMode.FIRE_AND_FORGET;
}
