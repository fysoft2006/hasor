/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.db.transaction.interceptor;
import java.lang.reflect.Method;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.ApiBinder.Matcher;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
/**
 * 格式：  <修饰符> <返回值> <类名>.<方法名>(<参数签名>)
 * @version : 2014年7月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class TranInterceptorBinder {
    private ApiBinder apiBinder = null;
    public TranInterceptorBinder(ApiBinder apiBinder) {
        this.apiBinder = apiBinder;
    }
    public StrategyBind matcher(String matcher) {};
    public StrategyBind matcher(Matcher<Method> matcher) {};
    // 
    public static class StrategyBind {
        public StrategyBind withPropagation(Propagation propagation);
        public StrategyBind withPropagation(TranStrategy<Propagation> propagation);
        public StrategyBind withIsolation(Isolation isolation);
        public StrategyBind withIsolation(TranStrategy<Isolation> isolation);
        //
        public StrategyBind onAround(TranOperations around);
        //
        public void done(DataSource dataSource);
    }
}