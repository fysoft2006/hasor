/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.db.transaction;
import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.sql.DataSource;

import net.hasor.core.Hasor;
import net.hasor.db.datasource.ConnectionHolder;
import net.hasor.db.datasource.DataSourceManager;
import net.hasor.db.transaction.support.JdbcTransactionManager;
/**
 * 某一个数据源的事务管理器
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public class TranManager extends DataSourceManager {
    private final static ThreadLocal<ConcurrentMap<DataSource, DefaultTransactionManager>> managerMap;
    private final static ThreadLocal<ConcurrentMap<DataSource, ConnectionHolder>>          currentMap;

    static {
        managerMap = new ThreadLocal<ConcurrentMap<DataSource, DefaultTransactionManager>>() {
            protected ConcurrentMap<DataSource, DefaultTransactionManager> initialValue() {
                return new ConcurrentHashMap<DataSource, DefaultTransactionManager>();
            }
        };
        currentMap = new ThreadLocal<ConcurrentMap<DataSource, ConnectionHolder>>() {
            protected ConcurrentMap<DataSource, ConnectionHolder> initialValue() {
                return new ConcurrentHashMap<DataSource, ConnectionHolder>();
            }
        };
    }

    //
    public static ConnectionHolder currentConnectionHolder(DataSource dataSource) {
        Hasor.assertIsNotNull(dataSource);
        ConcurrentMap<DataSource, ConnectionHolder> localMap = currentMap.get();
        ConnectionHolder holder = localMap.get(dataSource);
        if (holder == null) {
            holder = localMap.putIfAbsent(dataSource, genConnectionHolder(dataSource));
            holder = localMap.get(dataSource);
        }
        return holder;
    }
    public static Connection currentConnection(DataSource dataSource) {
        ConnectionHolder holder = currentConnectionHolder(dataSource);
        return newProxyConnection(holder);
    }
    /**改变当前{@link ConnectionHolder}*/
    protected static void currentConnection(DataSource dataSource, ConnectionHolder holder) {
        ConcurrentMap<DataSource, ConnectionHolder> localMap = currentMap.get();
        if (holder == null) {
            if (localMap.containsKey(dataSource)) {
                localMap.remove(dataSource);
            }
        } else {
            localMap.put(dataSource, holder);
        }
    }
    //
    /**获取事务管理器*/
    private static synchronized DefaultTransactionManager getTransactionManager(final DataSource dataSource) {
        Hasor.assertIsNotNull(dataSource);
        ConcurrentMap<DataSource, DefaultTransactionManager> localMap = managerMap.get();
        DefaultTransactionManager manager = localMap.get(dataSource);
        if (manager == null) {
            manager = localMap.putIfAbsent(dataSource, new DefaultTransactionManager(dataSource));
            manager = localMap.get(dataSource);
        }
        return manager;
    }
    /**获取{@link TransactionManager}*/
    public static synchronized TransactionManager getManager(DataSource dataSource) {
        return getTransactionManager(dataSource);
    }
    /**获取{@link TransactionTemplate}*/
    public static synchronized TransactionTemplate getTemplate(DataSource dataSource) {
        DefaultTransactionManager manager = getTransactionManager(dataSource);
        if (manager == null) {
            return null;
        }
        return manager.getTransactionTemplate();
    }
}
class DefaultTransactionManager extends JdbcTransactionManager {
    public DefaultTransactionManager(final DataSource dataSource) {
        super(dataSource);
    }
}