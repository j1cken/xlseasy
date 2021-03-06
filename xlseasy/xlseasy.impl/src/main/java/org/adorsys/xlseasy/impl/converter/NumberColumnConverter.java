package org.adorsys.xlseasy.impl.converter;

import org.adorsys.xlseasy.annotation.ISheetSession;
import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * The Class NumberColumnConverter.
 */
public abstract class NumberColumnConverter extends CellConverter {

	/**
	 * Instantiates a new number column converter.
	 */
	public NumberColumnConverter() {
		super();
	}

	/**
	 * Sets cell's type to Numeric and saves the value.
	 * */
	public void setHSSFCell(Object cellObject, Object value, Class<?> objectType, ISheetSession<?, ?> session) {
		HSSFCell cell = (HSSFCell) cellObject;
		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		if (value != null) {
			cell.setCellValue(((Number)value).doubleValue());
		}
	}
}