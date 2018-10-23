package cases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

/**
 * Created by zhengfan on 2017/10/31 0031.
 */
public class POITest {

    @Test
    public void writeExcelFile() throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("sheet");

        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow((short) 0);
        // Create a cell and put a value in it.
        Cell cell = row.createCell(0);
        cell.setCellValue("问题列表");

        for (int i = 1; i < 10; i++) {
            Row row2 = sheet.createRow(i);
            Cell cel2 = row2.createCell(0);
            cel2.setCellValue("这里是您的第" + gettCH(i) + "个问题");
        }

        File f = new File("workbook.xlsx");
        FileOutputStream fileOut = new FileOutputStream(f);
        wb.write(fileOut);
        fileOut.close();
    }

    private static String gettCH(int input) {
        String sd = "";
        switch (input) {
            case 1:
                sd = "一";
                break;
            case 2:
                sd = "二";
                break;
            case 3:
                sd = "三";
                break;
            case 4:
                sd = "四";
                break;
            case 5:
                sd = "五";
                break;
            case 6:
                sd = "六";
                break;
            case 7:
                sd = "七";
                break;
            case 8:
                sd = "八";
                break;
            case 9:
                sd = "九";
                break;
            default:
                break;
        }
        return sd;
    }

    public List<String> readExcelFile(InputStream excelFile) throws IOException {
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet dataTypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = dataTypeSheet.iterator();
        List<String> list = new ArrayList<>(dataTypeSheet.getLastRowNum());
        while (iterator.hasNext()) { // row

            Row currentRow = iterator.next();
            currentRow.getLastCellNum();
            Iterator<Cell> cellIterator = currentRow.iterator();

            while (cellIterator.hasNext()) { // col
                Cell cell = cellIterator.next();
                cell.setCellType(Cell.CELL_TYPE_STRING);
                list.add(cell.getStringCellValue());
                break;
            }
        }
        return list;

    }
}
