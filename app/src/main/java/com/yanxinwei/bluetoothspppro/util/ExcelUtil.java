package com.yanxinwei.bluetoothspppro.util;

/**
 *
 * Created by yanxinwei on 16/3/14.
 */
public class ExcelUtil {

//    public static boolean test(String path){
//        try {
//            InputStream is = new FileInputStream(path);
//            XSSFWorkbook workbook = new XSSFWorkbook(is);
//            Sheet sheet = workbook.getSheetAt(0);
//            Row row = sheet.getRow(0);
//            Cell cell = row.getCell(0, Row.CREATE_NULL_AS_BLANK);
//            String content = cell.getStringCellValue();
//            L.d("@@@@"+content);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    public static void test1(String path){
//        try {
//            InputStream is = new FileInputStream(path);
//            XSSFWorkbook workbook = new XSSFWorkbook(is);
//            XSSFSheet sheet = workbook.getSheetAt(1);
//            int rowsCount = sheet.getPhysicalNumberOfRows();
//            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
//            for (int r = 0; r<rowsCount; r++) {
//                Row row = sheet.getRow(r);
//                int cellsCount = row.getPhysicalNumberOfCells();
//                for (int c = 0; c<cellsCount; c++) {
//                    String value = getCellAsString(row, c, formulaEvaluator);
//                    String cellInfo = "r:"+r+"; c:"+c+"; v:"+value;
//                    L.d(cellInfo);
//                }
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    protected static String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
//        String value = "";
//        try {
//            Cell cell = row.getCell(c);
//            CellValue cellValue = formulaEvaluator.evaluate(cell);
//            switch (cellValue.getCellType()) {
//                case Cell.CELL_TYPE_BOOLEAN:
//                    value = ""+cellValue.getBooleanValue();
//                    break;
//                case Cell.CELL_TYPE_NUMERIC:
//                    double numericValue = cellValue.getNumberValue();
//                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
//                        double date = cellValue.getNumberValue();
//                        SimpleDateFormat formatter =
//                                new SimpleDateFormat("dd/MM/yy");
//                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
//                    } else {
//                        value = ""+numericValue;
//                    }
//                    break;
//                case Cell.CELL_TYPE_STRING:
//                    value = ""+cellValue.getStringValue();
//                    break;
//                default:
//            }
//        } catch (NullPointerException e) {
//            /* proper error handling should be here */
//        }
//        return value;
//    }


}
