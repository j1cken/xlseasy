package org.adorsys.xlseasy.boot;

import java.util.Map;

/**
 * Inspect date styles for fields of a given class.
 * 
 * @author Francis Pouatcha
 *
 */
public interface DateStylesInspector {

	public Map<String, String> inspectDateStyles(Class<?> sheetKlass);

}