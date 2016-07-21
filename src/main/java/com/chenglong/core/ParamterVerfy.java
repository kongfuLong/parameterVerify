package com.chenglong.core;

import com.chenglong.annotations.Valid;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/21
 * @since 1.0    针对  common.annotations下的valid注解进行参数校验
 * 目前支持范围: 内部对象引用  collection map 子类容器的校验
 * 注: valid 属性的get方法必须符合规范
 */
@Component
public class ParamterVerfy {


    private Object fieldParam(String fieldName,Object father) {
        String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = null;
        Class clazz = father.getClass();
        try {
            method = clazz.getDeclaredMethod(methodName);
            return method.invoke(father);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void verifySingleFiled(Valid valid, String fieldName, Object param){
            if(valid==null)return;
            if(fieldName==null || fieldName.equals("")){
                throw new IllegalArgumentException();
            }
                //判断该字段是否为空
                if (param == null) {
                    throw new NullPointerException(fieldName + ":参数为空");
                }
                Pattern p = null;
                Matcher m = null;
                switch (valid.value()) {
                    case NUMBER:
                        // TODO: 16/7/20 数字验证
                        break;
                    case STRING:
                        break;
                    // TODO: 16/7/20 字符串验证
                    case PHONE:
                        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
                        m = p.matcher(param.toString());
                        if (!m.matches()) {
                            throw new NullPointerException(fieldName + ":手机号参数类型不匹配");
                        }
                        break;
                    case ID:
                        // TODO: 16/7/20 身份证正则验证
                        break;
                    case EMAIL:
                        p = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"); // 验证手机号
                        m = p.matcher(param.toString());
                        if (!m.matches()) {
                            throw new NullPointerException(fieldName + ":邮箱参数类型不匹配");
                        }
                        break;
                }


    }

    /**
     * 单个对象参数验证   不进行递归验证内部引用对象
     * @param o
     */
    public void verifySingleObject(Object o){
        if(o == null){
            return;
        }
        Class clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Valid valid = field.getAnnotation(Valid.class);
            if (valid != null) {
                verifySingleFiled(valid,field.getName(), o);
            }
        }
    }

    /**
     * 深度对象参数验证   递归验证内部引用对象
     * @param o
     */
    public void verifyParameter(Object o){
        if(o == null){
            return;
        }
        Class clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Valid valid = field.getAnnotation(Valid.class);
            if (valid != null) {
                String fieldName = field.getName();
                Object result = fieldParam(fieldName, o);
                //先验证字段
                verifySingleFiled(valid,fieldName, result);
                //如果字段为对象引用则深入判断
                Class fieldType = field.getType();
                //原型数据变成obejct类型后都将会被变成封装类  例如int变成Integer
                //判断是否是九大基础类型
                if ( (result instanceof String) || (result instanceof Byte)
                        || (result instanceof Short) || (result instanceof Integer)
                        || (result instanceof Long) || (result instanceof Float)
                        || (result instanceof Double) || (result instanceof Boolean)
                        || (result instanceof Character)){
                    continue;
                }
                //判断是否是集合类
                if (result instanceof Collection ){
                    Collection list = (Collection) result;
                    if(list.size()==0){
                        throw new NullPointerException(fieldName+":容器内对象空");
                    }
                    list.stream().forEach(
                            bean -> {
                                verifyParameter(bean);
                            }
                    );
                    continue;
                }
                if (result instanceof Map){
                   Map map = (Map)result;
                   if(map.size()==0){
                       throw new NullPointerException(fieldName+":容器内对象空");
                   }
                   Iterator iterator = map.entrySet().iterator();
                   while(iterator.hasNext()){
                       Map.Entry entry = (Map.Entry) iterator.next();
                       verifyParameter(entry.getValue());
                   }
                    continue;
                }

                if(fieldType.isArray()){
                    Array array = (Array)result;
                    int length = Array.getLength(array);
                    if(length!=0){
                        for(int i =0;i<length;i++){
                            verifyParameter(Array.get(array,i));
                        }
                    }else {
                            throw new NullPointerException(fieldName+":数组内对象空");
                    }
                    continue;
                }

                    //以上类型都排除后说明是普通bean对像
                    verifyParameter(result);
            }
        }

    }

}
