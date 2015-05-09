package org.adorsys.xlseasy.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.adorsys.xlseasy.annotation.IgnoreCollection;
import org.adorsys.xlseasy.utils.ReflectionUtils.FieldFilter;

/**
 * Match all fields that are collection types.
 * 
 * @author Francis Pouatcha
 *
 */
public class CollectionFieldFilter implements FieldFilter {

	public boolean matches(Field field) {
		IgnoreCollection[] ignored = field.getAnnotationsByType(IgnoreCollection.class);
		if (ignored.length > 0) {
			return false;
		}

		Type genericType = field.getGenericType();
		if (genericType instanceof ParameterizedType) {  
            ParameterizedType pt = (ParameterizedType) genericType;  
            if (Collection.class.isAssignableFrom((Class<?>) pt.getRawType())) return true;
        }
		return false;
	}
}