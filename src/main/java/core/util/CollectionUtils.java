package core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 14:39
 */
public abstract class CollectionUtils {

    private static class EnumerationIterator
            implements Iterator {

        public boolean hasNext() {
            return enumeration.hasMoreElements();
        }

        public Object next() {
            return enumeration.nextElement();
        }

        public void remove()
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Not supported");
        }

        private Enumeration enumeration;

        public EnumerationIterator(Enumeration enumeration) {
            this.enumeration = enumeration;
        }
    }


    public CollectionUtils() {
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static List arrayToList(Object source) {
        return Arrays.asList(ObjectUtils.toObjectArray(source));
    }

    public static void mergeArrayIntoCollection(Object array, Collection collection) {
        if (collection == null)
            throw new IllegalArgumentException("Collection must not be null");
        Object arr[] = ObjectUtils.toObjectArray(array);
        Object aobj[];
        int j = (aobj = arr).length;
        for (int i = 0; i < j; i++) {
            Object elem = aobj[i];
            collection.add(elem);
        }

    }

    public static void mergePropertiesIntoMap(Properties props, Map map) {
        if (map == null)
            throw new IllegalArgumentException("Map must not be null");
        if (props != null) {
            String key;
            Object value;
            for (Enumeration en = props.propertyNames(); en.hasMoreElements(); map.put(key, value)) {
                key = (String) en.nextElement();
                value = props.getProperty(key);
                if (value == null)
                    value = props.get(key);
            }

        }
    }

    public static boolean contains(Iterator iterator, Object element) {
        if (iterator != null)
            while (iterator.hasNext()) {
                Object candidate = iterator.next();
                if (ObjectUtils.nullSafeEquals(candidate, element))
                    return true;
            }
        return false;
    }

    public static boolean contains(Enumeration enumeration, Object element) {
        if (enumeration != null)
            while (enumeration.hasMoreElements()) {
                Object candidate = enumeration.nextElement();
                if (ObjectUtils.nullSafeEquals(candidate, element))
                    return true;
            }
        return false;
    }

    public static boolean containsInstance(Collection collection, Object element) {
        if (collection != null) {
            for (Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
                Object candidate = iterator.next();
                if (candidate == element)
                    return true;
            }

        }
        return false;
    }

    public static boolean containsAny(Collection source, Collection candidates) {
        if (isEmpty(source) || isEmpty(candidates))
            return false;
        for (Iterator iterator = candidates.iterator(); iterator.hasNext(); ) {
            Object candidate = iterator.next();
            if (source.contains(candidate))
                return true;
        }

        return false;
    }

    public static Object findFirstMatch(Collection source, Collection candidates) {
        if (isEmpty(source) || isEmpty(candidates))
            return null;
        for (Iterator iterator = candidates.iterator(); iterator.hasNext(); ) {
            Object candidate = iterator.next();
            if (source.contains(candidate))
                return candidate;
        }

        return null;
    }

    public static Object findValueOfType(Collection collection, Class type) {
        if (isEmpty(collection))
            return null;
        Object value = null;
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
            Object element = iterator.next();
            if (type == null || type.isInstance(element)) {
                if (value != null)
                    return null;
                value = element;
            }
        }

        return value;
    }

    public static Object findValueOfType(Collection collection, Class types[]) {
        if (isEmpty(collection) || ObjectUtils.isEmpty(types))
            return null;
        Class aclass[];
        int j = (aclass = types).length;
        for (int i = 0; i < j; i++) {
            Class type = aclass[i];
            Object value = findValueOfType(collection, type);
            if (value != null)
                return value;
        }

        return null;
    }

    public static boolean hasUniqueObject(Collection collection) {
        if (isEmpty(collection))
            return false;
        boolean hasCandidate = false;
        Object candidate = null;
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
            Object elem = iterator.next();
            if (!hasCandidate) {
                hasCandidate = true;
                candidate = elem;
            } else if (candidate != elem)
                return false;
        }

        return true;
    }

    public static Class findCommonElementType(Collection collection) {
        if (isEmpty(collection))
            return null;
        Class candidate = null;
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
            Object val = iterator.next();
            if (val != null)
                if (candidate == null)
                    candidate = val.getClass();
                else if (candidate != val.getClass())
                    return null;
        }

        return candidate;
    }

    public static Iterator toIterator(Enumeration enumeration) {
        return new EnumerationIterator(enumeration);
    }

}
