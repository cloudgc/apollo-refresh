package org.cloudfun.msa.tools.apollo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;


/**
 * @author sunzhongwen;
 * @Description： 默认apollo 监听 application 刷新事件
 * @date 2018/8/30-14:53;
 */
@Configuration
public class ApolloRefreshListingConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApolloRefreshListingConfiguration.class);

//    @Autowired
//    ApolloRefreshScope apolloRefreshScope;
//
//    /**
//     * 监听 application 变化
//     * @param changeEvent apollo 回调事件
//     */
//    @ApolloConfigChangeListener
//    public void refreshNode(ConfigChangeEvent changeEvent) {
//        Config config = ConfigService.getAppConfig();
//        for (String changedKey : changeEvent.changedKeys()) {
//            LOGGER.info("change:{{}:{}}", changedKey, config.getProperty(changedKey, null));
//        }
//        apolloRefreshScope.refreshScope(changeEvent.changedKeys());
//
//    }


}
