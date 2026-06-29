package pub.module.common.messaging;

import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 从 {@code XxxConsumer} 契约接口解析 MQ 元数据。
 */
public final class MqContractSupport {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().findAndRegisterModules();

    private MqContractSupport() {
    }

    public static MqChannelMetadata getChannelMetadata(Class<?> contractInterface) {
        Class<?> root = resolveRootContract(contractInterface);
        MqChannel channel = root.getAnnotation(MqChannel.class);
        if (channel == null) {
            throw new IllegalArgumentException("未找到 @MqChannel: " + contractInterface.getName());
        }
        Method handlerMethod = resolveHandlerMethod(root, channel.mode());
        Class<?> payloadType = resolvePayloadType(root, channel.mode(), handlerMethod);
        Class<?> replyType = channel.mode() == MqChannelMode.REQUEST_REPLY
                ? handlerMethod.getReturnType() : Void.class;
        return new MqChannelMetadata(
                root,
                channel.destination(),
                channel.producerFunction(),
                channel.mode(),
                handlerMethod,
                payloadType,
                replyType);
    }

    public static Optional<MqSubscription> findSubscription(Class<?> handlerClass) {
        if (handlerClass == null || handlerClass.isInterface()) {
            return Optional.empty();
        }
        for (Class<?> iface : handlerClass.getInterfaces()) {
            Optional<MqSubscription> nested = findSubscriptionFromInterface(handlerClass, iface, null);
            if (nested.isPresent()) {
                return nested;
            }
        }
        return Optional.empty();
    }

    public static void validateHandlerClass(Class<?> handlerClass) {
        if (implementsRootDirectly(handlerClass) && findSubscription(handlerClass).isEmpty()) {
            throw new IllegalStateException(
                    "MQ 消费实现须继承 XxxConsumer 的嵌套槽位子接口（带 @MqSubscribe），"
                            + "不可直接实现根契约接口: " + handlerClass.getName());
        }
    }

    public static boolean isRootContract(Class<?> type) {
        return type != null && type.isInterface() && type.isAnnotationPresent(MqChannel.class);
    }

    private static Optional<MqSubscription> findSubscriptionFromInterface(
            Class<?> handlerClass, Class<?> iface, String handlerBeanName) {
        MqSubscribe subscribe = iface.getAnnotation(MqSubscribe.class);
        if (subscribe != null) {
            MqChannelMetadata channel = getChannelMetadata(iface);
            return Optional.of(new MqSubscription(
                    channel,
                    iface,
                    subscribe.group(),
                    subscribe.function(),
                    handlerClass.getName(),
                    handlerBeanName));
        }
        for (Class<?> parent : iface.getInterfaces()) {
            Optional<MqSubscription> nested = findSubscriptionFromInterface(handlerClass, parent, handlerBeanName);
            if (nested.isPresent()) {
                return nested;
            }
        }
        return Optional.empty();
    }

    private static boolean implementsRootDirectly(Class<?> handlerClass) {
        for (Class<?> iface : handlerClass.getInterfaces()) {
            if (isRootContract(iface)) {
                return true;
            }
        }
        return false;
    }

    private static Class<?> resolveRootContract(Class<?> type) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            if (current.isInterface() && current.isAnnotationPresent(MqChannel.class)) {
                return current;
            }
            if (current.isInterface() && current.getInterfaces().length > 0) {
                current = current.getInterfaces()[0];
                continue;
            }
            break;
        }
        throw new IllegalArgumentException("无法解析 @MqChannel 根接口: " + type.getName());
    }

    private static Method resolveHandlerMethod(Class<?> root, MqChannelMode mode) {
        List<Method> candidates = new ArrayList<>();
        for (Method method : root.getMethods()) {
            if (method.isDefault() || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getDeclaringClass() == MqMessageConsumer.class
                    || method.getDeclaringClass() == MqReplyConsumer.class
                    || method.getDeclaringClass() == Object.class) {
                continue;
            }
            candidates.add(method);
        }
        if (candidates.isEmpty()) {
            throw new IllegalStateException("MQ 契约接口缺少业务方法: " + root.getName());
        }
        if (mode == MqChannelMode.REQUEST_REPLY) {
            return candidates.stream()
                    .filter(m -> m.getParameterCount() == 1 && !Void.TYPE.equals(m.getReturnType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "request-reply 契约须声明 respond(Query): Reply 方法: " + root.getName()));
        }
        return candidates.stream()
                .filter(m -> m.getParameterCount() == 1 && Void.TYPE.equals(m.getReturnType()))
                .findFirst()
                .orElse(candidates.get(0));
    }

    private static Class<?> resolvePayloadType(Class<?> root, MqChannelMode mode, Method handlerMethod) {
        ResolvableType messageConsumer = ResolvableType.forClass(root).as(MqMessageConsumer.class);
        if (messageConsumer.hasGenerics()) {
            Class<?> resolved = messageConsumer.getGeneric(0).resolve();
            if (resolved != null) {
                return resolved;
            }
        }
        ResolvableType replyConsumer = ResolvableType.forClass(root).as(MqReplyConsumer.class);
        if (replyConsumer.hasGenerics()) {
            Class<?> resolved = replyConsumer.getGeneric(0).resolve();
            if (resolved != null) {
                return resolved;
            }
        }
        if (handlerMethod.getParameterCount() == 1) {
            return handlerMethod.getParameterTypes()[0];
        }
        throw new IllegalStateException("无法解析 payload 类型: " + root.getName());
    }

    public static Set<Class<?>> collectAllInterfaces(Class<?> type) {
        Set<Class<?>> result = new LinkedHashSet<>();
        collectInterfaces(type, result);
        return result;
    }

    private static void collectInterfaces(Class<?> type, Set<Class<?>> result) {
        if (type == null || type == Object.class) {
            return;
        }
        if (type.isInterface()) {
            result.add(type);
        }
        for (Class<?> iface : type.getInterfaces()) {
            collectInterfaces(iface, result);
        }
        if (!type.isInterface()) {
            collectInterfaces(type.getSuperclass(), result);
        }
    }

    public static Class<?> resolveClass(String className, ClassLoader classLoader) {
        if (!StringUtils.hasText(className)) {
            return null;
        }
        try {
            return ClassUtils.forName(className, classLoader);
        } catch (ClassNotFoundException | LinkageError ex) {
            return null;
        }
    }

    /**
     * 基于 ASM 元数据识别 MQ Handler，避免 {@code Class.forName} 加载全部 Bean（含损坏的 .class）。
     */
    public static Optional<MqSubscription> findSubscriptionFromMetadata(
            Iterable<String> implementedInterfaceNames,
            String handlerClassName,
            String handlerBeanName,
            ClassLoader classLoader) {
        for (String ifaceName : implementedInterfaceNames) {
            Class<?> iface = resolveClass(ifaceName, classLoader);
            if (iface == null) {
                continue;
            }
            MqSubscribe subscribe = iface.getAnnotation(MqSubscribe.class);
            if (subscribe == null) {
                continue;
            }
            MqChannelMetadata channel = getChannelMetadata(iface);
            return Optional.of(new MqSubscription(
                    channel,
                    iface,
                    subscribe.group(),
                    subscribe.function(),
                    handlerClassName,
                    handlerBeanName));
        }
        return Optional.empty();
    }

    public static Object invokeHandler(Object handler, MqChannelMetadata channel, Object payload) {
        Object resolvedPayload = resolvePayload(payload, channel);
        try {
            return channel.handlerMethod().invoke(handler, resolvedPayload);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new IllegalStateException(
                    "MQ 消费方法调用失败: " + handler.getClass().getName() + "#"
                            + channel.handlerMethod().getName(), cause);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(
                    "MQ 消费方法调用失败: " + handler.getClass().getName() + "#"
                            + channel.handlerMethod().getName(), ex);
        }
    }

    private static Object resolvePayload(Object payload, MqChannelMetadata channel) {
        Object resolved = payload instanceof Message<?> message ? message.getPayload() : payload;
        Class<?> targetType = channel.payloadType();
        if (resolved == null || targetType.isInstance(resolved)) {
            return resolved;
        }
        if (resolved instanceof Map<?, ?>) {
            return JSON_MAPPER.convertValue(resolved, targetType);
        }
        if (resolved instanceof byte[] bytes) {
            try {
                return JSON_MAPPER.readValue(bytes, targetType);
            } catch (IOException ex) {
                throw new IllegalStateException("MQ payload 反序列化失败: " + targetType.getName(), ex);
            }
        }
        if (resolved instanceof String text && StringUtils.hasText(text)) {
            try {
                return JSON_MAPPER.readValue(text, targetType);
            } catch (IOException ex) {
                throw new IllegalStateException("MQ payload 反序列化失败: " + targetType.getName(), ex);
            }
        }
        return resolved;
    }

    public static MqSubscribe getSlotAnnotation(Class<?> slotInterface) {
        MqSubscribe subscribe = slotInterface.getAnnotation(MqSubscribe.class);
        if (subscribe == null) {
            throw new IllegalArgumentException("嵌套槽位缺少 @MqSubscribe: " + slotInterface.getName());
        }
        return subscribe;
    }

    public static void assertSlotOfContract(Class<?> contractInterface, Class<?> slotInterface) {
        if (!contractInterface.isAssignableFrom(slotInterface)) {
            throw new IllegalArgumentException(
                    "应答槽位 " + slotInterface.getName() + " 不属于契约 " + contractInterface.getName());
        }
        if (slotInterface.getAnnotation(MqSubscribe.class) == null) {
            throw new IllegalArgumentException("应答槽位缺少 @MqSubscribe: " + slotInterface.getName());
        }
    }
}
