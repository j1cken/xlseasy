package org.adorsys.xlseasy.annotation;

/**
 * @author Sandro Sonntag
 */
public enum ErrorCodeSheet {
	UNKNOWN,
	CONVERTER_INIT_ERROR,
	WRONG_CONVERTER_CLASS_TYPE,
	UNKNOWN_CELL_FORMAT,
	CLASS_HAS_NO_WORKBOOK_ANNOTATION, 
	CLASS_HAS_NO_SHEET_ANNOTATION,
	READ_BEAN_DATA_ERROR,
	WRONG_SHEETDATA_TYPE,
	STORE_BEAN_DATA_ERROR,
	SAVE_XLS_FAILED,
	LOAD_XLS_FAILED,
	NO_CONVERTER_FOR_TYPE,
	INSTANCIATE_WB_BEAN_FAILED, 
	INSTANCIATE_RECORD_BEAN_FAILED,
	NOT_A_WORKBOOK_SESSION,
	NOT_A_SHEETTYPE_SESSION, 
	COLUMN_DEFINITION_FROM_COLUMN_ORDER_NOT_FOUND, 
	UNCOMPATIBE_TYPE_FOR_THIS_CONVERTER, 
	BEAN_INTROSPECTION_EXCEPTION, 
	FORMATTER_INSTANCIATION_EXCEPTION,
	SEPARATOR_CHARACTER_NOT_ALLOWED_IN_KEY_STRING,
	SEPARATOR_CHARACTER_NOT_ALLOWED_STRING_VALUE,
	REFERENCED_SHEET_DOES_NOT_PROVIDE_KEY_ANNOTATION,
	FIELD_WITH_NAME_NOT_FOUND,
	CIRCULAR_DEPENDENCIES_NOT_ALLOWED;
}