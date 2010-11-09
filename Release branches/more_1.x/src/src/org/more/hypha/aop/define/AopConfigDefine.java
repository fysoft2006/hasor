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
package org.more.hypha.aop.define;
import java.util.ArrayList;
import java.util.List;
import org.more.core.classcode.BuilderMode;
import org.more.hypha.AbstractDefine;
/**
 * aop����
 * @version 2010-9-25
 * @author ������ (zyc@byshell.org)
 */
public class AopConfigDefine extends AbstractDefine {
    private String                      name                  = null; //������
    private AopConfigDefine             useTemplate           = null; //ʹ�õ�ģ��
    private BuilderMode                 aopMode               = null; //aop�����ɲ���
    private AbstractPointcutDefine      defaultPointcutDefine = null; //Ĭ���е�
    //
    private ArrayList<AbstractInformed> aopInformedList       = null;
    //
    /**����{@link AopConfigDefine}����*/
    public AopConfigDefine() {
        this.aopMode = BuilderMode.Super;
        this.aopInformedList = new ArrayList<AbstractInformed>();
    }
    //
    /**��ȡaop��������*/
    public String getName() {
        return this.name;
    }
    /**����aop��������*/
    public void setName(String name) {
        this.name = name;
    }
    /**��ȡĬ��aop�е�����*/
    public AbstractPointcutDefine getDefaultPointcutDefine() {
        return this.defaultPointcutDefine;
    }
    /**����Ĭ��aop�е�����*/
    public void setDefaultPointcutDefine(AbstractPointcutDefine defaultPointcutDefine) {
        this.defaultPointcutDefine = defaultPointcutDefine;
    }
    /**��ȡaop������ʹ�õ�����ģ�塣*/
    public AopConfigDefine getUseTemplate() {
        return this.useTemplate;
    }
    /**����aop������ʹ�õ�����ģ�塣*/
    public void setUseTemplate(AopConfigDefine useTemplate) {
        this.useTemplate = useTemplate;
    }
    /**��ȡaop�е�ִ����*/
    public List<AbstractInformed> getAopInformedList() {
        return this.aopInformedList;
    }
    /**����һ���е�ִ����*/
    public void addInformed(AbstractInformed informed, AbstractPointcutDefine refPointcut) {
        informed.setRefPointcut(refPointcut);
        this.aopInformedList.add(informed);
    };
    /**����һ���е�ִ����*/
    public void addInformed(AbstractInformed informed) {
        if (informed.getRefPointcut() == null)
            informed.setRefPointcut(this.defaultPointcutDefine);
        this.aopInformedList.add(informed);
    }
    /**��ȡaop�����ɲ��ԡ�*/
    public BuilderMode getAopMode() {
        return aopMode;
    }
    /**����aop�����ɲ��ԡ�*/
    public void setAopMode(BuilderMode aopMode) {
        this.aopMode = aopMode;
    };
}