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
package org.more.beans.core;
import java.util.HashMap;
import java.util.List;
import org.more.InvokeException;
import org.more.NoDefinitionException;
import org.more.TypeException;
import org.more.beans.BeanFactory;
import org.more.beans.BeanResource;
import org.more.beans.core.factory.CreateFactory;
import org.more.beans.core.injection.InjectionFactory;
import org.more.beans.info.BeanDefinition;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.KeepAttDecorator;
/**
 * ������BeanFactory�ӿڵ�һ�������ʵ�֣��������ָ��resource��Դ������Ϊbean����Դ��ResourceBeanFactory����������ṩ������¹��������ԡ�
 * ResourceBeanFactory���ṩ�˶�lazyInit���Ե�֧�֡�<br/>����Ĺ��������ο�{@link BeanFactory}������ĵ���<br/><br/>
 * <font color="#ff0000">
 * ע�⣺�Ҳ������ڴ���ResourceBeanFactory����֮���ٴε���setLoader����������װ�����������������������beanû�������κ�aop���߸��ӽӿ�ʵ��
 * ��ô��������ʲôǱ�����⣬����һ�������˶���BeanResource����֧�ֻ��湦�ܣ���ô�ܿ��ܻ����һЩ�й�ClassLoader����ĹŹ��쳣��<br/>
 * ԭ����������ÿ��factory��injection���������ɵ����ֽ����ļ���װ��ʱ��ʹ��BeanFactory����װ������Ϊ����װ����������һ�������װ�������滻��
 * ��ô��Щ�Ѿ����ɵ����������������Ǳ�������BeanResource�ӿ��е��ٴε�������ʱ���ǵ���װ������νṹ�Ѿ�������BeanFactory���µ���װ�ء������
 * ��������create����ioc�����Ҳ�ȷ��һ���ᰲȫִ�С�������more���������͵�װ�أ������������͵�װ�أ�����ͨ��BeanFactory����װ�������С�
 * ʹ������������ǵ�bean������ȫ��Դ��������������ط��������ڵ�ǰϵͳ��ClassPath�С�
 * </font><br/>
 * Date : 2009-11-17
 * @author ������
 */
public class ResourceBeanFactory implements BeanFactory, IAttribute {
    /**  */
    private static final long       serialVersionUID   = -2164352693306612896L;
    //========================================================================================Field
    private BeanResource            resource           = null;                         //Bean��Դ
    private HashMap<String, Object> singletonBeanCache = new HashMap<String, Object>(); //���ڱ��浥̬bean
    private ClassLoader             loader             = null;                         //��װ��
    /**������󴴽�*/
    protected CreateFactory         createFactory      = new CreateFactory();
    /**�����������ע��*/
    protected InjectionFactory      injectionFactory   = new InjectionFactory();
    /**
     * �������Լ��ϣ�ResourceBeanFactory�ڹ��췽���л��Զ���this�������Լ����У�
     * ��������thisΪ�������ԣ���Ϊ�������Բ����Ա���д���йر������������
     *  {@link org.more.util.attribute.KeepAttDecorator}��
     * ��������ʹ��{#name}����ע���������
     */
    protected IAttribute            attribute          = null;
    //==================================================================================Constructor
    /**
     * ����ResourceBeanFactory���Ͷ��󣬴����ö������ָ��resource�������������NullPointerException�쳣��
     * @param resource ResourceBeanFactory��ʹ�õ�bean��Դ��
     * @param loader ResourceBeanFactory��ʹ�õ�ClassLoader���������null���ȡThread.currentThread().getContextClassLoader();
     */
    public ResourceBeanFactory(BeanResource resource, ClassLoader loader) {
        if (resource == null)
            throw new NullPointerException("����resource����Ϊ�ա�");
        if (loader == null)
            this.loader = Thread.currentThread().getContextClassLoader();
        this.resource = resource;
        KeepAttDecorator kad = new KeepAttDecorator(new AttBase());
        this.attribute = kad;
        kad.setAttribute("this", this);
        kad.setKeep("this", true);
        init();
    }
    //==========================================================================================Job
    /**�������Bean���棬��������װ��lazyInit����Ϊfalse��bean��*/
    public void reload() {
        clearBeanCache();
        this.init();
    }
    /**�������Bean���沢��֪ͨresource������ջ��棬�÷������ᵼ������װ��������lazyInit���Ե�bean��*/
    public void clearBeanCache() {
        this.singletonBeanCache.clear();
        injectionFactory.run();
        if (this.resource.isCacheBeanMetadata() == true)
            this.resource.clearCache();
    }
    /**��ʼ��������lazyInit����Ϊfalse��bean������Щbeanһ���ǵ�̬�ġ�*/
    protected void init() {
        List<String> initBeanNames = this.resource.getStrartInitBeanDefinitionNames();
        if (initBeanNames == null)
            return;
        for (String initBN : initBeanNames) {
            BeanDefinition bd = this.resource.getBeanDefinition(initBN);
            if (bd.isSingleton() == false || bd.isLazyInit() == true)
                continue;
            this.singletonBeanCache.put(initBN, this.getBean(initBN));
        }
    }
    @Override
    public boolean containsBean(String name) {
        if (singletonBeanCache.containsKey(name) == true)
            return true;
        else
            return this.resource.containsBeanDefinition(name);
    }
    @Override
    public Object getBean(String name, Object... objects) {
        Object obj = null;
        if (singletonBeanCache.containsKey(name) == true)
            return singletonBeanCache.get(name);
        //
        BeanDefinition definition = this.resource.getBeanDefinition(name);
        if (definition == null)
            throw new NoDefinitionException("û�ж�������Ϊ[" + name + "]��bean��");
        try {
            obj = this.createFactory.newInstance(definition, objects, this);//��������
            this.injectionFactory.ioc(obj, objects, definition, this);//ִ������ע��
            if (definition.isSingleton() == true && obj != null)
                this.singletonBeanCache.put(name, obj);
            return obj;
        } catch (Throwable e) {
            throw new InvokeException("�޷�����[" + name + "]���ڴ��������з����쳣��������ִ��ע��ʱ�����쳣", e);
        }
    }
    @Override
    public Class<?> getBeanType(String name) {
        BeanDefinition definition = this.resource.getBeanDefinition(name);
        if (definition == null)
            throw new NoDefinitionException("û�ж�������Ϊ[" + name + "]��bean��");
        String type = definition.getType();
        try {
            return this.loader.loadClass(type);
        } catch (Exception e) {
            throw new TypeException("�޷�װ������" + type, e);
        }
    }
    @Override
    public boolean isTypeMatch(String name, Class<?> targetType) {
        return this.getBeanType(name).isAssignableFrom(targetType);
    }
    @Override
    public ClassLoader getBeanClassLoader() {
        return this.loader;
    }
    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }
    @Override
    public BeanResource getBeanResource() {
        return this.resource;
    }
    @Override
    public boolean isFactory(String name) {
        return this.resource.isFactory(name);
    }
    @Override
    public boolean isPrototype(String name) {
        return this.resource.isPrototype(name);
    }
    @Override
    public boolean isSingleton(String name) {
        return this.resource.isSingleton(name);
    }
    //===================================================================================IAttribute
    /**������л������ԣ����Ǳ���this���ԡ�*/
    @Override
    public void clearAttribute() {
        this.attribute.clearAttribute();
        if (this.attribute instanceof KeepAttDecorator == true) {
            KeepAttDecorator kad = (KeepAttDecorator) this.attribute;
            if (kad.contains("this") == true)
                kad.setKeep("this", false);
            kad.setAttribute("this", this);
            kad.setKeep("this", true);
        } else
            this.attribute.setAttribute("this", this);
    }
    @Override
    public boolean contains(String name) {
        return this.attribute.contains(name);
    }
    @Override
    public Object getAttribute(String name) {
        return this.attribute.getAttribute(name);
    }
    @Override
    public String[] getAttributeNames() {
        return this.attribute.getAttributeNames();
    }
    @Override
    public void removeAttribute(String name) {
        this.attribute.removeAttribute(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.attribute.setAttribute(name, value);
    }
}