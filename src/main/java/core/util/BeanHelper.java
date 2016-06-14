package core.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 14:34
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class BeanHelper {

    static class ReflectionInfo {

        Method getReadMethod(String prop) {
            return prop != null ? (Method) readMap.get(prop.toLowerCase())
                    : null;
        }

        Method getWriteMethod(String prop) {
            return prop != null ? (Method) writeMap.get(prop.toLowerCase())
                    : null;
        }


        Map readMap;
        Map writeMap;

        ReflectionInfo() {
            readMap = new HashMap();
            writeMap = new HashMap();
        }
    }

    public static BeanHelper getInstance() {
        return bhelp;
    }

    private BeanHelper() {
    }

    public static List getPropertys(Object bean) {
        return Arrays.asList(getInstance().getPropertiesAry(bean));
    }


    public String[] getPropertiesAry(Object bean) {
        ReflectionInfo reflectionInfo = null;
        reflectionInfo = cachedReflectionInfo(bean.getClass());
        Set propertys = new HashSet();
        Iterator i$ = reflectionInfo.writeMap.keySet().iterator();
        do {
            if (!i$.hasNext())
                break;
            String key = (String) i$.next();
            if (reflectionInfo.writeMap.get(key) != null)
                propertys.add(key);
        } while (true);
        return (String[]) propertys.toArray(new String[0]);
    }

    public static Object getProperty(Object bean, String propertyName) throws Exception {
        Method method;
        try {
            method = getInstance().getMethod(bean, propertyName, false);
            if (propertyName != null && method == null)
                return null;
        } catch (Exception e) {
            String errStr = (new StringBuilder()).append(
                    "Failed to get property: ").append(propertyName).toString();
            throw new RuntimeException(errStr, e);
        }
        if (method == null)
            return null;
        return method.invoke(bean, NULL_ARGUMENTS);
    }

    public static Object[] getPropertyValues(Object bean, String propertys[]) {
        Object result[] = new Object[propertys.length];
        try {
            Method methods[] = getInstance().getMethods(bean, propertys, false);
            for (int i = 0; i < propertys.length; i++)
                if (propertys[i] == null || methods[i] == null)
                    result[i] = null;
                else
                    result[i] = methods[i].invoke(bean, NULL_ARGUMENTS);

        } catch (Exception e) {
            String errStr = (new StringBuilder()).append(
                    "Failed to get getPropertys from ").append(bean.getClass())
                    .toString();
            throw new RuntimeException(errStr, e);
        }
        return result;
    }

    public static Method getMethod(Object bean, String propertyName) {
        return getInstance().getMethod(bean, propertyName, true);
    }

    public static Method getGetMethod(Object bean, String propertyName) {
        return getInstance().getMethod(bean, propertyName, false);
    }

    public static Method getSetMethod(Object bean, String propertyName) {
        return getInstance().getMethod(bean, propertyName, true);
    }

    public static Method[] getMethods(Object bean, String propertys[]) {
        return getInstance().getMethods(bean, propertys, true);
    }

    private Method[] getMethods(Object bean, String propertys[],
                                boolean isSetMethod) {
        Method methods[] = new Method[propertys.length];
        ReflectionInfo reflectionInfo = null;
        reflectionInfo = cachedReflectionInfo(bean.getClass());
        for (int i = 0; i < propertys.length; i++) {
            Method method = null;
            if (isSetMethod)
                method = reflectionInfo.getWriteMethod(propertys[i]);
            else
                method = reflectionInfo.getReadMethod(propertys[i]);
            methods[i] = method;
        }

        return methods;
    }

    private Method getMethod(Object bean, String propertyName,
                             boolean isSetMethod) {
        Method method = null;
        ReflectionInfo reflectionInfo = null;
        reflectionInfo = cachedReflectionInfo(bean.getClass());
        if (isSetMethod)
            method = reflectionInfo.getWriteMethod(propertyName);
        else
            method = reflectionInfo.getReadMethod(propertyName);
        return method;
    }

    private ReflectionInfo cachedReflectionInfo(Class beanCls) {
        return cacheReflectionInfo(beanCls, null);
    }

    private ReflectionInfo cacheReflectionInfo(Class beanCls, List pdescriptor) {
        String key = beanCls.getName();
        ReflectionInfo reflectionInfo = (ReflectionInfo) cache.get(key);
        if (reflectionInfo == null) {
            reflectionInfo = (ReflectionInfo) cache.get(key);
            if (reflectionInfo == null) {
                reflectionInfo = new ReflectionInfo();
                List propDesc = new ArrayList();
                if (pdescriptor != null)
                    propDesc.addAll(pdescriptor);
                else
                    propDesc = getPropertyDescriptors(beanCls);
                Iterator i$ = propDesc.iterator();
                do {
                    if (!i$.hasNext())
                        break;
                    PropDescriptor pd = (PropDescriptor) i$.next();
                    Method readMethod = pd.getReadMethod(beanCls);
                    Method writeMethod = pd.getWriteMethod(beanCls);
                    if (readMethod != null)
                        reflectionInfo.readMap.put(pd.getName().toLowerCase(),
                                readMethod);
                    if (writeMethod != null)
                        reflectionInfo.writeMap.put(pd.getName().toLowerCase(),
                                writeMethod);
                } while (true);
                cache.put(key, reflectionInfo);
            }
        }
        return reflectionInfo;
    }

    public static void invokeMethod(Object bean, Method method, Object value) throws Exception {
        Object arguments[];
        try {
            if (method == null)
                return;
        } catch (Exception e) {
            String errStr = (new StringBuilder()).append(
                    "Failed to set property: ").append(method.getName())
                    .toString();
            throw new RuntimeException(errStr, e);
        }
        arguments = (new Object[]{value});
        method.invoke(bean, arguments);
    }

    public static void setProperty(Object bean, String propertyName,
                                   Object value) throws Exception {
        Method method;
        try {
            method = getInstance().getMethod(bean, propertyName, true);
            if (propertyName != null && method == null)
                return;
        } catch (IllegalArgumentException e) {
            String errStr = (new StringBuilder()).append(
                    "Failed to set property: ").append(propertyName).append(
                    " at bean: ").append(bean.getClass().getName()).append(
                    " with value:").append(value).append(" type:").append(
                    value != null ? value.getClass().getName() : "null")
                    .toString();
            throw new IllegalArgumentException(errStr, e);
        } catch (Exception e) {
            String errStr = (new StringBuilder()).append(
                    "Failed to set property: ").append(propertyName).append(
                    " at bean: ").append(bean.getClass().getName()).append(
                    " with value:").append(value).toString();
            throw new RuntimeException(errStr, e);
        }
        if (method == null)
            return;
        method.invoke(bean, new Object[]{value});
    }

    public Method[] getAllGetMethod(Class beanCls, String fieldNames[]) {
        Method methods[] = null;
        ReflectionInfo reflectionInfo = null;
        List al = new ArrayList();
        reflectionInfo = cachedReflectionInfo(beanCls);
        String arr$[] = fieldNames;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$++) {
            String str = arr$[i$];
            al.add(reflectionInfo.getReadMethod(str));
        }

        methods = (Method[]) al.toArray(new Method[al.size()]);
        return methods;
    }

    private List getPropertyDescriptors(Class clazz) {
        List descList = new ArrayList();
        List superDescList = new ArrayList();
        List propsList = new ArrayList();
        Class propType = null;
        Method arr$[] = clazz.getDeclaredMethods();
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$++) {
            Method method = arr$[i$];
            if (method.getName().length() < 4
                    || method.getName().charAt(3) < 'A'
                    || method.getName().charAt(3) > 'Z')
                continue;
            if (method.getName().startsWith("set")) {
                if (method.getParameterTypes().length != 1
                        || method.getReturnType() != Void.TYPE)
                    continue;
                propType = method.getParameterTypes()[0];
            } else {
                if (!method.getName().startsWith("get")
                        || method.getParameterTypes().length != 0)
                    continue;
                propType = method.getReturnType();
            }
            String propname = method.getName().substring(3, 4).toLowerCase();
            if (method.getName().length() > 4)
                propname = (new StringBuilder()).append(propname).append(
                        method.getName().substring(4)).toString();
            if (!propname.equals("class") && !propsList.contains(propname)) {
                propsList.add(propname);
                descList.add(new PropDescriptor(clazz, propType, propname));
            }
        }

        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            superDescList = getPropertyDescriptors(superClazz);
            descList.addAll(superDescList);
            if (!isBeanCached(superClazz))
                cacheReflectionInfo(superClazz, superDescList);
        }
        return descList;
    }

    private boolean isBeanCached(Class bean) {
        String key = bean.getName();
        ReflectionInfo cMethod = (ReflectionInfo) cache.get(key);
        if (cMethod == null) {
            cMethod = (ReflectionInfo) cache.get(key);
            if (cMethod == null)
                return false;
        }
        return true;
    }

    protected static final Object NULL_ARGUMENTS[] = new Object[0];
    private static Map cache = new ConcurrentHashMap();
    private static BeanHelper bhelp = new BeanHelper();

}
