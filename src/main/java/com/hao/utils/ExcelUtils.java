package com.hao.utils;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

@SuppressWarnings("all")
public class ExcelUtils {

    /**
     * 创建文本单元格
     *
     * @param row
     * @param col
     * @param value
     * @param cellStyle
     */
    public static void createTextCell(Row row, int col, String value, CellStyle cellStyle) {
        // 创建单元格
        Cell cell = row.createCell(col);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(new HSSFRichTextString(value));
    }

}