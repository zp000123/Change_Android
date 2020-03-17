package com.zhang.change.utils

import jxl.format.*
import jxl.write.Label
import jxl.write.Number
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet

object ExcelUtils {
    fun getContentCellFormat(): WritableCellFormat = WritableCellFormat().apply {
        alignment = Alignment.CENTRE //水平居中
        verticalAlignment = VerticalAlignment.CENTRE // 垂直居中
        setFont(WritableFont(WritableFont.TIMES, 11)) // 内容字体 11号
    }


    fun getTotalCellFormat(): WritableCellFormat = WritableCellFormat().apply {
        alignment = Alignment.CENTRE //水平居中
        verticalAlignment = VerticalAlignment.CENTRE // 垂直居中
        setFont(
            WritableFont(
                WritableFont.TIMES, 11,
                WritableFont.NO_BOLD,
                false,
                UnderlineStyle.NO_UNDERLINE,
                Colour.BLUE
            )
        )
    }


    fun getMinusCellFormat(): WritableCellFormat = WritableCellFormat().apply {
        alignment = Alignment.CENTRE //水平居中
        verticalAlignment = VerticalAlignment.CENTRE // 垂直居中
        setFont(
            WritableFont(
                WritableFont.TIMES, 11,
                WritableFont.NO_BOLD,
                false,
                UnderlineStyle.NO_UNDERLINE,
                Colour.RED
            )
        )
    }


    fun getTitleCellFormat(): WritableCellFormat = WritableCellFormat().apply {
        alignment = Alignment.CENTRE // 水平居中
        verticalAlignment = VerticalAlignment.CENTRE // 垂直居中
        setFont(WritableFont(WritableFont.TIMES, 13, WritableFont.BOLD)) // 表头字体 加粗 13号
    }


}

fun WritableSheet.addLabel(c: Int, r: Int, cont: String, st: CellFormat) {
    this.addCell(Label(c, r, cont, st))
}

fun WritableSheet.addNumber(c: Int, r: Int, value: Double, st: CellFormat) {
    this.addCell(Number(c, r, value, st))
}