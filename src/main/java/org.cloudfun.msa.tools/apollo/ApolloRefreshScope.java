package org.cloudfun.msa.tools.apollo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.scope.GenericScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sunzhongwen;
 * @Description： apollo 刷新 属性 ;
 * @date 2018/8/29-11:36;
 */
public class ApolloRefreshScope extends GenericScope
        implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor, Ordered,
        BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApolloRefreshScope.class);

    private ApplicationContext context;
    private BeanDefinitionRegistry registry;
    private boolean eager = true;

    private static final int ORDER_LEVEL = 99;

    private int order = Ordered.LOWEST_PRECEDENCE - ORDER_LEVEL;

    private ConcurrentHashMap<String, String> beanDefineCache = new ConcurrentHashMap<>();


    /**
     * Create a scope instance and give it the default name: "refresh".
     */
    public ApolloRefreshScope() {
        super.setName("apolloRefresh");
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Flag to determine whether all beans in refresh scope should be instantiated eagerly
     * on startup. Default true.
     *
     * @param eager the flag to set
     */
    public void setEager(boolean eager) {
        this.eager = eager;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        this.registry = registry;
    }

    /**
     * ApolloRefresh 事件监听
     *
     * @param event ApolloRefresh 事件
     */
    @EventListener(ApolloRefreshScopeRefreshedEvent.class)
    public void start(ApolloRefreshScopeRefreshedEvent event) {
        if (this.context != null) {
            if (this.eager && this.registry != null) {
                for (String name : this.context.getBeanDefinitionNames()) {
                    BeanDefinition definition = this.registry.getBeanDefinition(name);
                    if (this.getName().equals(definition.getScope())
                            && !definition.isLazyInit()) {
                        this.context.getBean(name);

                    }
                }
            }
        }
    }

    /**
     * refresh 事件
     *
     * @param name 需要刷新的bean 的名称
     * @return 是否刷新成功
     */
    @ManagedOperation(description = "Dispose of the current instance of bean name provided and force a refresh"
            + " on next method execution.")
    public boolean refresh(String name) {
        if (!name.startsWith(SCOPED_TARGET_PREFIX)) {
            // User wants to refresh the bean with this name but that isn't the one in the
            // cache...
            name = SCOPED_TARGET_PREFIX + name;
        }

        //for init
        this.context.getBean(name);
        // Ensure lifecycle is finished if bean was disposable
        if (super.destroy(name)) {
            this.context.publishEvent(new ApolloRefreshScopeRefreshedEvent(name));
            return true;
        }
        return false;
    }

    /**
     * 自动根据刷新的 key 匹配 config bean 刷新
     *
     * @param changeKeys 变更的key
     * @return 刷新结果
     */
    @ManagedOperation(description = "Dispose of the current instance of bean name provided and force a "
            + "refresh by @Configuration property prefix")
    public boolean refreshScope(Set<String> changeKeys) {

        Set<String> changeName = findChangeName(changeKeys);
        if (changeName.size() > 0) {
            changeName.forEach(name -> {
                if (!refresh(name)) {
                    //ignore refresh error
                    LOGGER.warn("name:{},refresh fail", name);
                } else {
                    LOGGER.info("name:{},refresh success", name);
                }
            });
        }
        return true;

    }

    /**
     * 根据 前缀 找 bean
     *
     * @param changeKeys 变更的keys
     * @return 匹配的 bean
     */
    private Set<String> findChangeName(Set<String> changeKeys) {
        Set<String> changeName = new LinkedHashSet<>();
        for (String changeKey : changeKeys) {
            String beanName = getBeanNameByConfigurationPrefix(changeKey);
            if (beanName != null) {
                changeName.add(beanName);
            }
        }
        return changeName;
    }


    /**
     * 刷新全部 apollorefresh
     */
    @ManagedOperation(description = "Dispose of the current instance of all beans in this scope "
            + "and force a refresh on next method execution.")
    public void refreshAll() {
        super.destroy();
        this.context.publishEvent(new ApolloRefreshScopeRefreshedEvent());
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }


    /**
     * 获取缓存bean
     *
     * @param changekey 变更的key
     * @return bean
     */
    private String getBeanNameByConfigurationPrefix(String changekey) {
        for (String bean : beanDefineCache.keySet()) {
            String prefix = beanDefineCache.get(bean);
            if (changekey.startsWith(prefix)) {
                return bean;
            }
        }
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ScopedProxyFactoryBean) {
            ApolloRefresh apolloRefresh = AnnotationUtils.findAnnotation(
                    ((ScopedProxyFactoryBean) bean).getObjectType(), ApolloRefresh.class);
            if (apolloRefresh != null) {
                ConfigurationProperties configuration = AnnotationUtils.
                        findAnnotation(((ScopedProxyFactoryBean) bean).getObjectType(),
                                ConfigurationProperties.class);
                Assert.notNull(configuration, "@ApolloRefresh must couple with  @ConfigurationProperties");
                if (!StringUtils.isEmpty(configuration.prefix())) {
                    beanDefineCache.put(beanName, configuration.prefix());
                }
            }
        }
        return bean;
    }
}
