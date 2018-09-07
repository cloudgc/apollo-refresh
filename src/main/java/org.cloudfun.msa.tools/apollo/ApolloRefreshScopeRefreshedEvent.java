package org.cloudfun.msa.tools.apollo;

import org.springframework.context.ApplicationEvent;

/**
 * @author sunzhongwen;
 * @Description： apollo 刷新事件
 * @date 2018/8/29-18:17;
 */
public class ApolloRefreshScopeRefreshedEvent extends ApplicationEvent {

    public static final String DEFAULT_NAME = "__refreshAll__";
    private String name;

    public ApolloRefreshScopeRefreshedEvent() {
        this(DEFAULT_NAME);
    }

    public ApolloRefreshScopeRefreshedEvent(String name) {
        super(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
