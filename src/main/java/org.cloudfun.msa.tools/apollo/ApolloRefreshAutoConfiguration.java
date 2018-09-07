package org.cloudfun.msa.tools.apollo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author sunzhongwen;
 * @Description： apollo 刷新自动配置;
 * @date 2018/8/29-11:41;
 */
@Configuration
@ConditionalOnClass(RefreshScope.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class ApolloRefreshAutoConfiguration {

    /**
     * 加载 ApolloRefresh 解析类
     */
    @Component
    @ConditionalOnMissingBean(ApolloRefreshScope.class)
    protected static class ApolloRefreshScopeConfiguration
            implements BeanDefinitionRegistryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                throws BeansException {
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
                throws BeansException {
            registry.registerBeanDefinition("apolloRefreshScope",
                    BeanDefinitionBuilder.genericBeanDefinition(ApolloRefreshScope.class)
                            .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                            .getBeanDefinition());
        }
    }

}
