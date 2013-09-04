/*
 * Copyright 2008-2009 the original ������(zyc@hasor.net).
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
package net.hasor.context.event;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.hasor.Hasor;
import net.hasor.context.AsyncCallBackHook;
import net.hasor.context.EventManager;
import net.hasor.context.HasorEventListener;
import net.hasor.context.HasorSettingListener;
import net.hasor.context.Settings;
import org.more.util.ArrayUtils;
import org.more.util.StringUtils;
/**
 * ��׼�¼��������ӿڵ�ʵ����
 * @version : 2013-5-6
 * @author ������ (zyc@hasor.net)
 */
public class StandardEventManager implements EventManager {
    private static final EmptyAsyncCallBackHook         EmptyAsyncCallBack = new EmptyAsyncCallBackHook();
    //
    private Settings                                    settings           = null;
    private ScheduledExecutorService                    executorService    = null;
    private Map<String, HasorEventListener[]>           listenerMap        = new HashMap<String, HasorEventListener[]>();
    private ReadWriteLock                               listenerRWLock     = new ReentrantReadWriteLock();
    private Map<String, LinkedList<HasorEventListener>> onceListenerMap    = new HashMap<String, LinkedList<HasorEventListener>>();
    private Lock                                        onceListenerLock   = new ReentrantLock();
    //
    public StandardEventManager(Settings settings) {
        this.settings = settings;
        this.executorService = Executors.newScheduledThreadPool(1);
        settings.addSettingsListener(new HasorSettingListener() {
            public void onLoadConfig(Settings newConfig) {
                update();
            }
        });
        this.update();
    }
    /**��ȡSetting�ӿڶ���*/
    public Settings getSettings() {
        return this.settings;
    }
    private void update() {
        //����ThreadPoolExecutor
        int eventThreadPoolSize = this.getSettings().getInteger("hasor.eventThreadPoolSize", 20);
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) executorService;
        threadPool.setCorePoolSize(eventThreadPoolSize);
        threadPool.setMaximumPoolSize(eventThreadPoolSize);
    }
    /**��ȡִ���¼�ʹ�õ�ScheduledExecutorService�ӿڶ���*/
    protected ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }
    //
    //
    //
    //
    //
    public void pushEventListener(String eventType, HasorEventListener hasorEventListener) {
        if (StringUtils.isBlank(eventType) || hasorEventListener == null)
            return;
        this.onceListenerLock.lock();//����
        LinkedList<HasorEventListener> eventList = this.onceListenerMap.get(eventType);
        if (eventList == null) {
            eventList = new LinkedList<HasorEventListener>();
            this.onceListenerMap.put(eventType, eventList);
        }
        if (eventList.contains(hasorEventListener) == false)
            eventList.push(hasorEventListener);
        this.onceListenerLock.unlock();//����
    }
    public void addEventListener(String eventType, HasorEventListener hasorEventListener) {
        this.listenerRWLock.writeLock().lock();//����(д)
        //
        Hasor.assertIsNotNull(hasorEventListener, "add EventListener object is null.");
        HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
        if (eventListenerArray == null) {
            eventListenerArray = new HasorEventListener[] { hasorEventListener };
            this.listenerMap.put(eventType, eventListenerArray);
        } else {
            if (ArrayUtils.contains(eventListenerArray, hasorEventListener) == false) {
                eventListenerArray = (HasorEventListener[]) ArrayUtils.add(eventListenerArray, hasorEventListener);
                this.listenerMap.put(eventType, eventListenerArray);
            }
        }
        //
        this.listenerRWLock.writeLock().unlock();//����(д)
    }
    //    public void removeAllEventListener(String eventType) {
    //        this.listenerRWLock.writeLock().lock();//����(д)
    //        this.listenerMap.remove(eventType);
    //        this.listenerRWLock.writeLock().unlock();//����(д)
    //    }
    public void removeEventListener(String eventType, HasorEventListener hasorEventListener) {
        this.listenerRWLock.writeLock().lock();//����(д)
        //
        Hasor.assertIsNotNull(eventType, "remove eventType is null.");
        Hasor.assertIsNotNull(hasorEventListener, "remove EventListener object is null.");
        HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
        if (!ArrayUtils.isEmpty(eventListenerArray)) {
            int index = ArrayUtils.indexOf(eventListenerArray, hasorEventListener);
            eventListenerArray = (HasorEventListener[]) ((index == ArrayUtils.INDEX_NOT_FOUND) ? eventListenerArray : ArrayUtils.remove(eventListenerArray, index));
            this.listenerMap.put(eventType, eventListenerArray);
        }
        //
        this.listenerRWLock.writeLock().unlock();//����(д)
    }
    //    public HasorEventListener[] getEventListener(String eventType) {
    //        this.listenerRWLock.readLock().lock();//����(��)
    //        //
    //        HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
    //        if (eventListenerArray != null) {
    //            HasorEventListener[] array = new HasorEventListener[eventListenerArray.length];
    //            System.arraycopy(eventListenerArray, 0, array, 0, eventListenerArray.length);
    //            eventListenerArray = array;
    //        } else
    //            eventListenerArray = EmptyEventListener;
    //        //
    //        this.listenerRWLock.readLock().unlock();//����(��)
    //        return eventListenerArray;
    //    }
    //    public String[] getEventTypes() {
    //        this.listenerRWLock.readLock().lock();//����(��)
    //        //
    //        Set<String> eventTypes = this.listenerMap.keySet();
    //        String[] eventTypeNames = eventTypes.toArray(new String[eventTypes.size()]);
    //        //
    //        this.listenerRWLock.readLock().unlock();//����(��)
    //        return eventTypeNames;
    //    }
    //
    //
    //
    //
    //
    public void doSyncEvent(String eventType, Object... objects) throws Throwable {
        this._doSyncEvent(false, eventType, objects);
    }
    public void doSyncEventIgnoreThrow(String eventType, Object... objects) {
        try {
            this._doSyncEvent(true, eventType, objects);
        } catch (Throwable e) {
            throw new RuntimeException(e);//����ignore����Ϊtrue�����׳��¼����쳣����������ٴ��׳��쳣ԭ����ȷ�������̵�Ǳ�ڵ��쳣��Ϣ��
        }
    }
    public void doAsynEvent(String eventType, AsyncCallBackHook callBack, Object... objects) {
        _doAsynEvent(false, eventType, callBack, objects);
    }
    public void doAsynEventIgnoreThrow(final String eventType, final Object... objects) {
        _doAsynEvent(true, eventType, null, objects);
    }
    private void _doSyncEvent(boolean ignore, String eventType, Object... objects) throws Throwable {
        if (StringUtils.isBlank(eventType) == true)
            return;
        this.listenerRWLock.readLock().lock();//����(��)
        HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
        this.listenerRWLock.readLock().unlock();//����(��)
        //
        if (eventListenerArray != null) {
            for (HasorEventListener event : eventListenerArray)
                try {
                    event.onEvent(eventType, objects);
                } catch (Throwable e) {
                    if (ignore)
                        Hasor.warning("During the execution of SyncEvent ��%s�� throw an error.%s", event.getClass(), e);
                    else
                        throw e;
                }
        }
        //����OnceListener
        this.processOnceListener(ignore, eventType, EmptyAsyncCallBack, objects);
    }
    private void _doAsynEvent(final boolean ignore, final String eventType, final AsyncCallBackHook hook, final Object... objects) {
        if (StringUtils.isBlank(eventType) == true)
            return;
        final AsyncCallBackHook callBack = (hook != null) ? hook : EmptyAsyncCallBack;
        this.listenerRWLock.readLock().lock();//����(��)
        final HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
        this.listenerRWLock.readLock().unlock();//����(��)
        this.executorService.submit(new Runnable() {
            public void run() {
                if (eventListenerArray != null) {
                    for (HasorEventListener event : eventListenerArray)
                        try {
                            event.onEvent(eventType, objects);
                        } catch (Throwable e) {
                            if (ignore)
                                Hasor.warning("During the execution of AsynEvent ��%s�� throw an error.%s", event.getClass(), e);
                            else
                                callBack.handleException(eventType, objects, e);
                        }
                }
                //����OnceListener
                processOnceListener(ignore, eventType, callBack, objects);
                callBack.handleComplete(eventType, objects);
            }
        });
    }
    private void processOnceListener(boolean ignore, String eventType, AsyncCallBackHook callBack, Object... objects) {
        this.onceListenerLock.lock();//����
        LinkedList<HasorEventListener> eventList = this.onceListenerMap.get(eventType);
        if (eventList != null) {
            HasorEventListener listener = null;
            while ((listener = eventList.pollLast()) != null) {
                try {
                    listener.onEvent(eventType, objects);
                } catch (Throwable e) {
                    if (ignore)
                        Hasor.warning("During the execution of OnceListener ��%s�� throw an error.%s", listener.getClass(), e);
                    else
                        callBack.handleException(eventType, objects, e);
                }
            }
        }
        this.onceListenerLock.unlock();//����
    }
    //
    //
    //
    //
    //
    public synchronized void clean() {
        this.onceListenerLock.lock();//����
        this.onceListenerMap.clear();
        this.onceListenerLock.unlock();//����
        //
        this.executorService.shutdownNow();
        this.executorService = Executors.newScheduledThreadPool(1);
        this.update();
    }
}