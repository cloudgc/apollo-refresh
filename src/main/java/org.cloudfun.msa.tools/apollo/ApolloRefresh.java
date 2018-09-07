package org.cloudfun.msa.tools.apollo;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.annotation.*;

/**
 * @author sunzhongwen;
 * @Description： apollo 注解表示 需要刷新的bean
 * @date 2018/8/29-17:58;
 * @Copyright: Copyright (c) 2018 Fullgoal Fund Management  Co., Ltd. Inc. All rights reserved.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Scope("apolloRefresh")
@Documented
public @interface ApolloRefresh {

    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;

}
