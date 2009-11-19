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
package org.more.beans.info;
import org.more.beans.BeanContext;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * ����һ�����ö����������ԵĶ��塣��������һ����Ϊ���������ǡ����������ԡ���������bean���塢���ô���������Ԫ��Ϣ���ԡ���<br/><br/>
 * һ�����������ԣ�PRV_ContextAtt����<br/>
 *   bean��������ֻ��ʵ����{@link IAttribute}�ӿڻ�����{@link BeanContext}�ӿڵ�ʵ����ʱ��ž������������ԣ�
 *   ʹ������������ע��ʱ��ζ����ע������ʱ����ֵ��Ѱ���ǵ������Ķ����{@link IAttribute}�ӿ���Ѱ�ҡ�
 * ������������bean���壨PRV_Mime����<br/>
 *   �������Ͷ��壬����ע����Ҫ����ע��һ��������{@link BeanDefinition}����Ķ��󡣶��������ͨ�������ٴα�����{@link BeanDefinition}�����á�
 * �������ô���������PRV_Bean����<br/>
 *   ��������ע����ָ��������getBean(name,params)����ʱ��ѡ���params���������е�ĳһ��������Ϊ����ֵ�������������ǲ��߱���ȷ�������͵ġ�
 * �ġ�Ԫ��Ϣ���ԣ�PRV_Param����<br/>
 *   Ԫ��Ϣ�������õ�������Դ����info�����п��Ի�õ�������������ֵ����Щ����ֵ�������{@link AttBase}�����С�info�������е������඼�Ѿ��̳���{@link AttBase}���͡�
 *   ����������{@link AttBase}��û���ҵ����Ԫ��Ϣ��ϵͳ���Զ�����һ���ṹ�в������ԡ������û�ҵ����ٴ�����Ѱ��һֱѰ�ҵ�<b>����������</b>Ϊֹ��
 *   <br/>��ʾ��BeanDefinition�Ĳ�νṹ�ο�info������������
 * <br/>Date : 2009-11-18
 * @author ������
 */
public class PropRefValue extends BeanProp {
    //========================================================================================Field
    /**��ʾ���õ�������һ��bean�����bean��һ���Ѿ������bean��*/
    public static final String PRV_Bean         = "bean";
    /**��ʾ���Դ�Factory�����л�ȡ��*/
    public static final String PRV_ContextAtt   = "context";
    /**��ʾ������������getBeanʱ���ݵĻ��������С�*/
    public static final String PRV_Param        = "param";
    /**��ʾ���Դ������mime�����л�ȡ��*/
    public static final String PRV_Mime         = "mime";
    /**  */
    private static final long  serialVersionUID = -194590250590692070L;
    private String             refValue         = null;                //����ֵ
    private String             refType          = null;                //�������ͣ���PRV_�������塣
    //==================================================================================Constructor
    /**�������ö������͡�*/
    public PropRefValue(String refValue, String refType) {
        this.refValue = refValue;
        this.setRefType(refType);
    }
    //==========================================================================================Job
    /**��ȡ����ֵ��*/
    public String getRefValue() {
        return refValue;
    }
    /**��������ֵ��*/
    public void setRefValue(String refValue) {
        this.refValue = refValue;
    }
    /**��ȡ�������ͣ���PRV_�������塣*/
    public String getRefType() {
        return refType;
    }
    /**�����������ͣ���PRV_�������塣*/
    public void setRefType(String refType) {
        this.refType = refType;
    }
}