package org.adorsys.xlseasy.impl.proc;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.adorsys.xlseasy.annotation.ErrorCodeSheet;
import org.adorsys.xlseasy.annotation.FreezePaneObject;
import org.adorsys.xlseasy.annotation.ISheetSession;
import org.adorsys.xlseasy.annotation.Sheet;
import org.adorsys.xlseasy.annotation.SheetColumn;
import org.adorsys.xlseasy.annotation.SheetFormatter;
import org.adorsys.xlseasy.annotation.SheetObject;
import org.adorsys.xlseasy.annotation.SheetSystemException;
import org.adorsys.xlseasy.annotation.filter.AnnotationUtil;
import org.adorsys.xlseasy.impl.converter.KeyGenerator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * The Class SheetDesc.
 *
 * @param <T> the generic type
 * @param <WT> the generic type
 */
public class SheetDesc<T, WT> implements SheetDescIF<T, WT>{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The xls column name2desc. */
    private final Map<String, ColumnDesc> xlsColumnName2desc = new HashMap<String, ColumnDesc>();
    
    /** The property name2desc. */
    private final Map<String, ColumnDesc> propertyName2desc = new HashMap<String, ColumnDesc>();
    
    /** The column order. */
    private final List<ColumnDesc> columnOrder = new ArrayList<ColumnDesc>();

    /** The record class. */
    private final Class<T> recordClass;
    
    /** The label. */
    private final String label;
    
    /** The workbook property. */
    private final String workbookProperty;
    
    /** The sheet. */
    private final SheetObject sheet;
    
    /** The workbook. */
    private final WorkbookDesc<WT> workbook;
    
    /** The key generator. */
    private final KeyGenerator keyGenerator;
    
    /** The sheet index. */
    private final int sheetIndex;

    /**
     * Instantiates a new sheet desc.
     *
     * @param workbook the workbook
     * @param recordClass the record class
     * @param label the label
     * @param workbookProperty the workbook property
     * @param sheetIndex the sheet index
     */
    public SheetDesc(WorkbookDesc<WT> workbook, Class<T> recordClass, String label, String workbookProperty, int sheetIndex) {
        super();
        this.recordClass = recordClass;
        this.workbook = workbook;
        this.sheetIndex = sheetIndex;

        Collection<Annotation> sheet = AnnotationUtil.findClassAnnotations(recordClass, true, Sheet.class);

        //Ordered columns
        if (sheet.size() > 0) {
            this.sheet = new SheetObject((Sheet) sheet.iterator().next());

            if (label == null) {
                label = StringUtils.trimToNull(this.sheet.name());
            }
        } else {
            throw new SheetSystemException(ErrorCodeSheet.CLASS_HAS_NO_SHEET_ANNOTATION).addValue("class", recordClass);
        }
        this.workbookProperty = workbookProperty;
        if (label != null) {
            this.label = label;
        } else if (workbookProperty != null) {
            this.label = workbookProperty;
        } else {
            this.label = recordClass.getCanonicalName();
        }
        this.keyGenerator = new KeyGenerator(recordClass);
        initColumnDescs();
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#getColumnDescs()
     */
    public List<ColumnDesc> getColumnDescs() {
        return Collections.unmodifiableList(columnOrder);
    }

    /**
     * Inits the column descs.
     */
    private void initColumnDescs() {
        Map<PropertyDescriptor, Map<Class<?>, Annotation>> propertyDescriptorAnnotations = AnnotationUtil
                .findBeanPropertyDescriptorAnnotations(recordClass, true, SheetColumn.class);
        Map<String, PropertyDescriptor> key2PropertyDescriptor = AnnotationUtil.extractPropertyKey2PropertyDescriptor(propertyDescriptorAnnotations.keySet());

        //Ordered columns
        if (sheet.columnOrder().length > 0) {
            String[] value = sheet.columnOrder();
            for (int i = 0; i < value.length; i++) {
                String prop = value[i];
                if (!key2PropertyDescriptor.containsKey(prop)) {
                    throw new SheetSystemException(ErrorCodeSheet.COLUMN_DEFINITION_FROM_COLUMN_ORDER_NOT_FOUND)
                            .addValue("property", prop).addValue("recordClass", recordClass);
                }
                addColumn(propertyDescriptorAnnotations,
                        key2PropertyDescriptor, prop, i);
            }
        } else {
            //alpha sort
            TreeSet<String> sortedProps = new TreeSet<String>(key2PropertyDescriptor.keySet());
            int columnIndex = 0;
            for (String prop : sortedProps) {
                addColumn(propertyDescriptorAnnotations,
                        key2PropertyDescriptor, prop, columnIndex);
                columnIndex++;
            }
        }
    }

    /**
     * Adds the column.
     *
     * @param propertyDescriptorAnnotations the property descriptor annotations
     * @param key2PropertyDescriptor the key2 property descriptor
     * @param prop the prop
     * @param columnIndex the column index
     */
    private void addColumn(
            Map<PropertyDescriptor, Map<Class<?>, Annotation>> propertyDescriptorAnnotations,
            Map<String, PropertyDescriptor> key2PropertyDescriptor, String prop, int columnIndex) {
        PropertyDescriptor pd = key2PropertyDescriptor.get(prop);
        
        Map<Class<?>, Annotation> map = propertyDescriptorAnnotations.get(pd);
        Field field = AnnotationUtil.findField(recordClass, pd.getName());
        if(field==null) throw new SheetSystemException(ErrorCodeSheet.FIELD_WITH_NAME_NOT_FOUND).addValue("fieldName", pd.getName());
        ColumnDesc columnDesc = new ColumnDesc(pd, (SheetColumn) map.get(SheetColumn.class), columnIndex, field);
        columnOrder.add(columnDesc);
        xlsColumnName2desc.put(columnDesc.getXlsColumnLabel(), columnDesc);
        propertyName2desc.put(columnDesc.getPropertyName(), columnDesc);
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#getColumnDescForXlsColumnName(java.lang.String)
     */
    public ColumnDesc getColumnDescForXlsColumnName(String xlsColumnName) {
        return xlsColumnName2desc.get(xlsColumnName);
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#getColumnDescForPropertyName(java.lang.String)
     */
    public ColumnDesc getColumnDescForPropertyName(String propertyName) {
        return propertyName2desc.get(propertyName);
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#getLabel()
     */
    public String getLabel() {
        return label;
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#getSheetData(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public List<T> getSheetData(Object workbookObj) {
        try {
            return (List<T>) PropertyUtils.getProperty(workbookObj, workbookProperty);
        } catch (IllegalAccessException e) {
            throw new SheetSystemException(ErrorCodeSheet.READ_BEAN_DATA_ERROR, e);
        } catch (InvocationTargetException e) {
            throw new SheetSystemException(ErrorCodeSheet.READ_BEAN_DATA_ERROR, e);
        } catch (NoSuchMethodException e) {
            throw new SheetSystemException(ErrorCodeSheet.READ_BEAN_DATA_ERROR, e);
        }
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#setSheetData(java.lang.Object, java.util.List)
     */
    public void setSheetData(Object workbookObj, List<T> records) {
        try {
            PropertyUtils.setProperty(workbookObj, workbookProperty, records);
        } catch (IllegalAccessException e) {
            throw new SheetSystemException(ErrorCodeSheet.READ_BEAN_DATA_ERROR, e);
        } catch (InvocationTargetException e) {
            throw new SheetSystemException(ErrorCodeSheet.READ_BEAN_DATA_ERROR, e);
        } catch (NoSuchMethodException e) {
            throw new SheetSystemException(ErrorCodeSheet.READ_BEAN_DATA_ERROR, e);
        }
    }

    /**
     * load the headers found in the sheet.
     *
     * @param sheet the sheet
     * @return the list
     */
    private List<ColumnDesc> loadXlsHeader(HSSFSheet sheet) {
        List<ColumnDesc> columnDescs = new ArrayList<ColumnDesc>();
        HSSFRow row = sheet.getRow(0);
        Iterator<?> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            HSSFCell cell = (HSSFCell) cellIterator.next();
            String headerValue = cell.getRichStringCellValue().getString();
            ColumnDesc columnDesc = xlsColumnName2desc.get(headerValue);
            columnDescs.add(columnDesc);
        }
        return columnDescs;
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#loadAndSetBeanRecords(org.apache.poi.hssf.usermodel.HSSFSheet, java.lang.Object, org.adorsys.xlseasy.annotation.ISheetSession)
     */
    public List<T> loadAndSetBeanRecords(HSSFSheet sheet, Object workbook, ISheetSession<?, ?> session) {
        //if not loaded, load it!
        List<T> sheetData = loadBeanRecords(sheet, session);
        setSheetData(workbook, sheetData);
        return sheetData;
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#loadBeanRecords(org.apache.poi.hssf.usermodel.HSSFSheet, org.adorsys.xlseasy.annotation.ISheetSession)
     */
    public List<T> loadBeanRecords(HSSFSheet sheet, ISheetSession<?, ?> session) {
        List<T> records = new ArrayList<T>();
        List<ColumnDesc> loadXlsHeader = loadXlsHeader(sheet);
        int loadXlsHeaderSize = loadXlsHeader.size();
        Iterator<?> rowIterator = sheet.rowIterator();
        if (rowIterator.hasNext())
            //skipp header
            rowIterator.next();

        while (rowIterator.hasNext()) {
            HSSFRow row = (HSSFRow) rowIterator.next();
            T record = newRecordInstance();
            Iterator<?> cellIterator = row.cellIterator();
//			for (ColumnDesc columnDesc : loadXlsHeader) {
//				if (!cellIterator.hasNext()) {
//					break;
//				}
//				HSSFCell cell = (HSSFCell) cellIterator.next();
//				if (columnDesc != null) {
//					columnDesc.copyCellValueToBean(record, cell);
//				}
//			}
            while (cellIterator.hasNext()) {
                HSSFCell cell = (HSSFCell) cellIterator.next();
                int index = cell.getColumnIndex();
                if (loadXlsHeaderSize <= index) {
                    break;// No additional column descriptor, so no need to continue.
                }
                ColumnDesc columnDesc = loadXlsHeader.get(index);
                if (columnDesc != null) {
                    //if no columndesc for index then skip this column
                    columnDesc.copyCellValueToBean(record, cell, session);
                }
            }
            records.add(record);
            session.setObjectByKey(recordClass, keyGenerator.getKey(record), record);
        }
        return records;
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#newRecordInstance()
     */
    public T newRecordInstance() {
        try {
            return recordClass.newInstance();
        } catch (InstantiationException e) {
            throw new SheetSystemException(ErrorCodeSheet.INSTANCIATE_RECORD_BEAN_FAILED, e).addValue("class",
                    recordClass.getClass().getName());
        } catch (IllegalAccessException e) {
            throw new SheetSystemException(ErrorCodeSheet.INSTANCIATE_RECORD_BEAN_FAILED, e).addValue("class",
                    recordClass.getClass().getName());
        }
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#getSheet()
     */
    public SheetObject getSheet() {
        return sheet;
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#createSheet(java.util.Collection, org.adorsys.xlseasy.impl.proc.SheetSession)
     */
    public void createSheet(Collection<?> sheetData, SheetSession<?, ?> session) {
        HSSFSheet sheet = session.getWorkbook().createSheet(getLabel());
        createData(session, sheet, sheetData);
        createHeader(session, sheet);
        formatSheet(sheet);
        addConstraints(sheet);
    }

    /**
     * Format sheet.
     *
     * @param hssfSheet the hssf sheet
     */
    private void formatSheet(HSSFSheet hssfSheet) {
        if (sheet != null) {
            Class<? extends SheetFormatter> formatter = sheet.formatter();
            try {
                SheetFormatter formatterInstance = formatter.newInstance();
                formatterInstance.format(hssfSheet);
            } catch (InstantiationException e) {
                throw new SheetSystemException(ErrorCodeSheet.FORMATTER_INSTANCIATION_EXCEPTION, e);
            } catch (IllegalAccessException e) {
                throw new SheetSystemException(ErrorCodeSheet.FORMATTER_INSTANCIATION_EXCEPTION, e);
            }
        }

    }

    /**
     * Creates the header.
     *
     * @param session the session
     * @param sheet the sheet
     */
    protected void createHeader(SheetSession<?, ?> session, HSSFSheet sheet) {
        HSSFRow row = sheet.createRow(0);
        List<ColumnDesc> columnDescs = getColumnDescs();
        for (int i = 0; i < columnDescs.size(); i++) {
            HSSFCell cell = row.createCell(i);
            ColumnDesc columnDesc = columnDescs.get(i);
            columnDesc.setHeaderLabel(cell, session);
            columnDesc.formatHeaderCell(session, cell);
            if (getSheet() != null && getSheet().autoSizeColumns()) {
                sheet.autoSizeColumn((short) i);
            }
        }
        FreezePaneObject freezePane = getSheet().freezePane();
        sheet.createFreezePane(freezePane.colSplit(), freezePane.rowSplit(), freezePane.leftmostColumn(),
                freezePane.topRow());
    }

    /**
     * Creates the data.
     *
     * @param session the session
     * @param sheet the sheet
     * @param sheetData the sheet data
     */
    protected void createData(SheetSession<?, ?> session, HSSFSheet sheet,
                              Collection<?> sheetData) {
        if (sheetData != null) {
            int i = 0;
            for (Object object : sheetData) {
                HSSFRow row = sheet.createRow(i + 1);
                fillRow(session, row, object);
                i++;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.adorsys.xlseasy.impl.proc.SheetDescIF#getRecordClass()
     */
    public Class<T> getRecordClass() {
        return recordClass;
    }

    /**
     * Adds the constraints.
     *
     * @param sheet the sheet
     */
    protected void addConstraints(HSSFSheet sheet) {
        int index = 0;
        for (ColumnDesc c : columnOrder) {
            Sheet sheetAnnotation = c.getType().getAnnotation(Sheet.class);
            if (sheetAnnotation != null) {
                SheetDesc<?, WT> sheetDesc = (SheetDesc<?, WT>) workbook.getSheet(c.getType());
                String keyColumnName = sheetDesc.keyGenerator.getKeyColumnName();
                if (keyColumnName != null) {
                    ColumnDesc refrerencedKeyColumn = sheetDesc.getColumnDescForPropertyName(keyColumnName);
                    String from = new CellReference(sheetDesc.getLabel(), 1, refrerencedKeyColumn.getColumnIndex(), true, true).formatAsString();
                    String to = new CellReference(65000, refrerencedKeyColumn.getColumnIndex()).formatAsString();
                    String validationRange = from + ":" + to;
                    DVConstraint createFormulaListConstraint = DVConstraint.createFormulaListConstraint(validationRange);

                    CellRangeAddressList constraintRange = new CellRangeAddressList(1, 65000, index, index);
                    HSSFDataValidation hssfDataValidation = new HSSFDataValidation(constraintRange, createFormulaListConstraint);
                    hssfDataValidation.setErrorStyle(HSSFDataValidation.ErrorStyle.STOP);
                    hssfDataValidation.createErrorBox("Constraint violation", "Please select a valid value!");
                    sheet.addValidationData(hssfDataValidation);
                }
            }
            index++;
        }
    }

    /**
     * Fill row.
     *
     * @param session the session
     * @param row the row
     * @param bean the bean
     */
    private void fillRow(SheetSession<?, ?> session, HSSFRow row, Object bean) {
        for (int i = 0; i < columnOrder.size(); i++) {
            ColumnDesc columnDesc = columnOrder.get(i);
            HSSFCell cell = row.createCell(i);
            columnDesc.copyBeanPropertyValueToCell(bean, cell, session);
            columnDesc.formatDataCell(session, cell);
        }
    }
}