package adorsys.poi.example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import adorsys.poi.example.utils.*;

/**
 * Generates an Excel file with two sheets and implements some calculations
 * function.
 * 
 * @author Marius Guede <mariusguede@urframes.net>
 */
public class ExcelExample {

	private int rowIndex = 0;
	private int cellIndex = 1;
	private short defaultAlignment = 0x2;
	private short defaultFontHeightInPoints = 12;
	private static final String FILE_NAME = "excelExample.xls";

	/**
	 * 
	 * This procedure create a book, sheet, row and cell in this order. Also it
	 * records the data into the FILE_NAME.
	 * 
	 */
	public void createExcel() {

		try {
			// We create the book. If we'd like to load a book from a file we
			// should write something like this: new HSSFWorkbook(new
			// FileInputStream(file))
			HSSFWorkbook book = new HSSFWorkbook();

			// We create the sheet for this book
			HSSFSheet sheet1 = book.createSheet("1st Sheet");
			HSSFSheet sheet2 = book.createSheet("2nd Sheet");

			// We create rows and cells and set their values.
			HSSFRow row;
			HSSFCell cell;
			
			// We create a new Function Object.
			Function function = new Function();

			// 1st row
			rowIndex++;
			row = sheet1.createRow(rowIndex);
			cell = row.createCell(1);
			cell.setCellValue("Client's name");
			cell.setCellStyle(function.getMyDefaultStyle(book));

			cell = row.createCell(2);
			cell.setCellValue(new HSSFRichTextString("Price / article"));
			cell.setCellStyle(function.getMyDefaultStyle(book));

			cell = row.createCell(3);
			cell.setCellValue("Qty");
			cell.setCellStyle(function.getMyDefaultStyle(book));

			cell = row.createCell(5);
			cell.setCellValue("Total");
			cell.setCellStyle(function.getMyDefaultStyle(book));

			// other rows
			for (int i = 0; i < function.getArrayLength(); i++) {
				rowIndex++;
				row = sheet1.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue((i + 1) + ".");
				function.setMyCellAlignment(book, cell, defaultAlignment);
				function.setMyCellFont(book, cell, HSSFFont.BOLDWEIGHT_BOLD,
						defaultFontHeightInPoints);

				cell = row.createCell(1);
				cell.setCellValue(Function.name[i]);
				function.setMyCellAlignment(book, cell, defaultAlignment);

				cell = row.createCell(2);
				cell.setCellValue("2.5 EUR");
				function.setMyCellAlignment(book, cell, defaultAlignment);

				// get random index to compute total price
				int rdm = function.getRandomIndex();

				cell = row.createCell(3);
				cell.setCellValue(rdm);
				function.setMyCellAlignment(book, cell, defaultAlignment);

				cell = row.createCell(5);
				cell.setCellValue(rdm * 2.5);
				function.setMyCellAlignment(book, cell, defaultAlignment);
			}
			rowIndex++;
			row = sheet1.createRow(rowIndex);
			cell = row.createCell(5);

			// get column reference
			String ref = function.getCellByName(cell.getRowIndex(),
					cell.getColumnIndex());

			// get row's index of the last inserted value
			int indexLastInsertedValue = function.getArrayLength() + 2;

			cell.setCellFormula("SUM(" + ref + "3:" + ref + ""
					+ indexLastInsertedValue + ")");
			cell.setCellStyle(function.getMyDefaultStyle(book));

			// setup column's width
			sheet1.setColumnWidth(0, 1500);
			sheet1.setColumnWidth(1, 4000);
			sheet1.setColumnWidth(2, 4000);
			sheet1.setColumnWidth(3, 1500);
			sheet1.setColumnWidth(4, 700);
			sheet1.setColumnWidth(5, 2000);

			// setup page's margin (top, right, bottom, left)
			sheet1.setMargin(Sheet.TopMargin, 0.75);
			sheet1.setMargin(Sheet.RightMargin, 0.25);
			sheet1.setMargin(Sheet.BottomMargin, 0.75);
			sheet1.setMargin(Sheet.LeftMargin, 0.25);

			// setup header and footer margins
			sheet1.setMargin(Sheet.HeaderMargin, 0.25);
			sheet1.setMargin(Sheet.FooterMargin, 0.25);

			/**
			 * Write now on Sheet 2
			 * */

			// We create a style to apply on some cell
			HSSFCellStyle style = book.createCellStyle();

			// 2nd row
			cellIndex++;
			row = sheet2.createRow(1);
			cell = row.createCell(cellIndex);
			for (int i = 0; i < function.getArrayLength(); i++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue((i + 1) + ".");
				cellIndex++;
			}
			cellIndex = 1;

			// 3rd row
			row = sheet2.createRow(2);
			cell = row.createCell(cellIndex);
			cell.setCellValue("Client's name");
			cell.setCellStyle(function.getMyDefaultStyle(book));
			cellIndex++;

			for (int i = 0; i < function.getArrayLength(); i++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(Function.name[i]);
				cellIndex++;
			}
			cellIndex = 1;

			// 4th row
			row = sheet2.createRow(3);
			cell = row.createCell(cellIndex);
			cell.setCellValue(new HSSFRichTextString("Price / article"));
			cell.setCellStyle(function.getMyDefaultStyle(book));
			cellIndex++;

			for (int i = 0; i < function.getArrayLength(); i++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("2.5 EUR");
				cellIndex++;
			}
			cellIndex = 1;

			// 5th row
			row = sheet2.createRow(4);
			cell = row.createCell(cellIndex);
			cell.setCellValue("Qty");
			cell.setCellStyle(function.getMyDefaultStyle(book));
			cellIndex++;

			// get random index to compute total price
			int rdm;
			for (int i = 0; i < function.getArrayLength(); i++) {
				rdm = function.getRandomIndex();
				cell = row.createCell(cellIndex);
				cell.setCellValue(rdm);
				style.setAlignment((short) 0x2);
				cell.setCellStyle(style);
				cellIndex++;
			}
			cellIndex = 1;

			// 6th row
			row = sheet2.createRow(5);
			cell = row.createCell(cellIndex);
			cell.setCellValue("Total");
			cell.setCellStyle(function.getMyDefaultStyle(book));
			cellIndex++;

			for (int i = 0; i < function.getArrayLength(); i++) {
				cell = row.createCell(cellIndex);
				cell.setCellFormula(""
						+ function.getCellByReference(
								sheet2.getRow(cell.getRowIndex() - 1)
										.getRowNum(),
								sheet2.getRow(cell.getRowIndex() - 1)
										.getCell(cellIndex).getColumnIndex())
						+ "*2.5");
				style.setAlignment((short) 0x2);
				cell.setCellStyle(style);
				cellIndex++;
			}
			cellIndex = 1;

			// 7th row / just to test the function getCellByName()
			row = sheet2.createRow(6);
			cell = row.createCell(cellIndex);
			cell.setCellValue("getCellByName()");
			cellIndex++;

			for (int i = 0; i < function.getArrayLength(); i++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(function.getCellByName(cell.getRowIndex(),
						cell.getColumnIndex()));
				cell.setCellStyle(function.getMyDefaultStyle(book));
				cellIndex++;
			}
			cellIndex = 1;

			// 8th row / just to test the function getCellByNumber()
			row = sheet2.createRow(7);
			cell = row.createCell(cellIndex);
			cell.setCellValue("getCellByNumber()");
			cellIndex++;

			for (int i = 0; i < function.getArrayLength(); i++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(function.getCellByNumber(cell.getRowIndex(),
						cell.getColumnIndex()));
				cell.setCellStyle(function.getMyDefaultStyle(book));
				cellIndex++;
			}
			cellIndex = 1;

			// 9th row / just to test the function getCellByReference()
			row = sheet2.createRow(8);
			cell = row.createCell(cellIndex);
			cell.setCellValue("getCellByReference()");
			cellIndex++;

			for (int i = 0; i < function.getArrayLength(); i++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(function.getCellByReference(cell.getRowIndex(),
						cell.getColumnIndex()));
				cell.setCellStyle(function.getMyDefaultStyle(book));
				cellIndex++;
			}
			cellIndex = 1;

			// 10th row / just to test the function getCellByReference()
			row = sheet2.createRow(9);
			cell = row.createCell(cellIndex);
			cell.setCellValue("getPreviousCellValue()");
			cellIndex++;

			for (int i = 0; i < function.getArrayLength(); i++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(sheet2.getRow(cell.getRowIndex() - 1)
						.getCell(cellIndex).getRichStringCellValue());
				cell.setCellStyle(function.getMyDefaultStyle(book));
				cellIndex++;
			}
			cellIndex = 1;

			// setup B-column's width
			sheet2.setColumnWidth(1, 5000);

			// setup other column's width iteratively
			for (int i = 2; i < function.getArrayLength() + 2; i++) {
				sheet2.setColumnWidth(i, 3000);
			}

			// setup page's margin (top, right, bottom, left)
			sheet2.setMargin(Sheet.TopMargin, 0.75);
			sheet2.setMargin(Sheet.RightMargin, 0.25);
			sheet2.setMargin(Sheet.BottomMargin, 0.75);
			sheet2.setMargin(Sheet.LeftMargin, 0.25);

			// setup header and footer margins
			sheet2.setMargin(Sheet.HeaderMargin, 0.25);
			sheet2.setMargin(Sheet.FooterMargin, 0.25);

			// Now it's time to record the data in a file making sure we close
			// the file
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(FILE_NAME);
				book.write(output);
				output.close();

				System.out
						.println("The excel's file has been successfull created.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (output != null)
					output.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Create an instance of ExcelExample object and call the createExcel
	 * procedure
	 * 
	 * @param args
	 *            Command line parameters. Not required in this example
	 * 
	 */
	public static void main(String args[]) {
		ExcelExample excel = new ExcelExample();
		excel.createExcel();
	}

}
