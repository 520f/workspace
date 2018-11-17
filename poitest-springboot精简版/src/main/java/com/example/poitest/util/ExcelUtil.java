package com.example.poitest.util;


import com.example.poitest.annotation.ExcelVOAttribute;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * User:xuyalun
 * Date 2018/11/16
 * Time 15:25
 *
 * ExcelUtil工具类实现功能: 
 * 导出时传入list<T>,即可实现导出为一个excel,其中每个对象Ｔ为Excel中的一条记录. 
 * 导入时读取excel,得到的结果是一个list<T>.T是自己定义的对象. 
 * 需要导出的实体对象只需简单配置注解就能实现灵活导出,通过注解您可以方便实现下面功能: 
 * 1.实体属性配置了注解就能导出到excel中,每个属性都对应一列. 
 * 2.列名称可以通过注解配置. 
 * 3.导出到哪一列可以通过注解配置. 
 * 4.鼠标移动到该列时提示信息可以通过注解配置. 
 * 5.用注解设置只能下拉选择不能随意填写功能. 
 * 6.用注解设置是否只导出标题而不导出内容,这在导出内容作为模板以供用户填写时比较实用. 
 * 本工具类以后可能还会加功能,请关注我的博客: http://blog.csdn.net/lk_blog 
 */  
public class ExcelUtil<T> {  
    Class<T> clazz;  
    public ExcelUtil(Class<T> clazz) {
        this.clazz = clazz;  
    }
    /**
     * @param filePath  导入excel文件的路径
     * @return           文件内容集合
     */
    public List<T> importExcel(String filePath) {
        try {
            return importExcel("sheet1",new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    /**
     * @param fileInputStream  输入流
     * @return                   文件内容集合
     */
    public List<T> importExcel(FileInputStream fileInputStream) {
        return importExcel("sheet1",fileInputStream);
    }
    /**
     *
     * @param sheetName        sheet名字
     * @param fileInputStream  输入流
     * @return                   文件内容集合
     */
    public List<T> importExcel(String sheetName,FileInputStream fileInputStream) {
        int maxCol = 0;
        List<T> list = new ArrayList<T>();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet sheet = workbook.getSheet(sheetName);
            if (!sheetName.trim().equals("")) {
                // 如果指定sheet名,则取指定sheet中的内容.
                sheet = workbook.getSheet(sheetName);
            }
            if (sheet == null) {
                // 如果传入的sheet名不存在则默认指向第1个sheet.
                sheet = workbook.getSheetAt(0);
            }
            int rows = sheet.getPhysicalNumberOfRows();
            // 有数据时才处理
            if (rows > 0) {
                // Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
                List<Field> allFields = getMappedFiled(clazz, null);
                // 定义一个map用于存放列的序号和field.
                Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();
                for (Field field : allFields) {
                    // 将有注解的field存放到map中.
                    if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
                        ExcelVOAttribute attr = field
                                .getAnnotation(ExcelVOAttribute.class);
                        // 获得列号
                        int col = getExcelCol(attr.column());
                        maxCol = Math.max(col, maxCol);
                        // System.out.println(col + "====" + field.getName());
                        // 设置类的私有字段属性可访问.
                        field.setAccessible(true);
                        fieldsMap.put(col, field);
                    }
                }
                // 从第2行开始取数据,默认第一行是表头.
                for (int i = 1; i < rows; i++) {
                    HSSFRow row = sheet.getRow(i);
                    // int cellNum = row.getPhysicalNumberOfCells();
                    // int cellNum = row.getLastCellNum();
                    int cellNum = maxCol;
                    T entity = null;
                    for (int j = 0; j <=cellNum; j++) {
                        HSSFCell cell = row.getCell(j);
                        if (cell == null) {
                            continue;
                        }
                        int cellType = cell.getCellType();
                        String c = "";
                        if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
                            c = String.valueOf(cell.getNumericCellValue());
                        } else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {
                            c = String.valueOf(cell.getBooleanCellValue());
                        } else {
                            c = cell.getStringCellValue();
                        }
                        if (c == null || c.equals("")) {
                            continue;
                        }
                        // 如果不存在实例则新建.
                        entity = (entity == null ? clazz.newInstance() : entity);
                        // System.out.println(cells[j].getContents());
                        // 从map中得到对应列的field.
                        Field field = fieldsMap.get(j);
                        if (field==null) {
                            continue;
                        }
                        // 取得类型,并根据对象类型设置值.
                        Class<?> fieldType = field.getType();
                        if (String.class == fieldType) {
                            field.set(entity, String.valueOf(c));
                        } else if ((Integer.TYPE == fieldType)
                                || (Integer.class == fieldType)) {
                            field.set(entity, Integer.parseInt(c));
                        } else if ((Long.TYPE == fieldType)
                                || (Long.class == fieldType)) {
                            field.set(entity, Long.valueOf(c));
                        } else if ((Float.TYPE == fieldType)
                                || (Float.class == fieldType)) {
                            field.set(entity, Float.valueOf(c));
                        } else if ((Short.TYPE == fieldType)
                                || (Short.class == fieldType)) {
                            field.set(entity, Short.valueOf(c));
                        } else if ((Double.TYPE == fieldType)
                                || (Double.class == fieldType)) {
                            field.set(entity, Double.valueOf(c));
                        } else if (Character.TYPE == fieldType) {
                            if ((c != null) && (c.length() > 0)) {
                                field.set(entity, Character
                                        .valueOf(c.charAt(0)));
                            }
                        }
                    }
                    if (entity != null) {
                        list.add(entity);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 对list数据源将其里面的数据导入到excel表单 
     *  
     * @param sheetNames
     *            工作表的名称 
     * @param output 
     *            java输出流 
     */  
    public boolean exportExcel(List<T> lists[], String sheetNames[],OutputStream output) {
        HSSFWorkbook workbook = exportExcelForWeb(lists, sheetNames);
        try {  
            output.flush();  
            workbook.write(output);  
            output.close();  
            return true;  
        } catch (IOException e) {  
            e.printStackTrace();  
            System.out.println("Output is closed ");  
            return false;  
        }  

    }  
    /**
     * 对list数据源将其里面的数据导入到excel表单 
     *  
     * @param sheetName 
     *            工作表的名称 
     * //@param sheetSize
     *            每个sheet中数据的行数,此数值必须小于65536 
     * @param output 
     *            java输出流 
     */  
    @SuppressWarnings("unchecked")
    public boolean exportExcel(List<T> list, String sheetName,OutputStream output) {
        //此处 对类型进行转换
        List<T> ilist = new ArrayList<>();
        for (T t : list) {
            ilist.add(t);
        }
        List<T>[] lists = new ArrayList[1];  
        lists[0] = ilist;  
        String[] sheetNames = new String[1];
        sheetNames[0] = sheetName;  
        return exportExcel(lists, sheetNames, output);
    }  
    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @param list                   要导出的内容集合
     * @param sheetName              sheet名称
     * @return                        含有内容的HSSFWorkbook对象
     */
    @SuppressWarnings("unchecked")
    public HSSFWorkbook exportExcelForWeb(List<T> list, String sheetName) {
        //此处 对类型进行转换
        List<T> ilist = new ArrayList<>();
        for (T t : list) {
            ilist.add(t);
        }
        List<T>[] lists = new ArrayList[1];
        lists[0] = ilist;

        String[] sheetNames = new String[1];
        sheetNames[0] = sheetName;
        return exportExcelForWeb(lists, sheetNames);
    }
    /**
     * 对list数据源将其里面的数据写入到excel表单
     *
     * @param sheetNames
     *            工作表的名称
     */
    public HSSFWorkbook exportExcelForWeb(List<T> lists[], String sheetNames[]) {
        if (lists.length != sheetNames.length) {
            System.out.println("数组长度不一致");
            return new HSSFWorkbook();
        }
        // 产生工作薄对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (int ii = 0; ii < lists.length; ii++) {
            List<T> list = lists[ii];
            String sheetName = sheetNames[ii];
            List<Field> fields = getMappedFiled(clazz, null);
            // 产生工作表对象
            HSSFSheet sheet = workbook.createSheet();
            workbook.setSheetName(ii, sheetName);
            HSSFRow row;
            HSSFCell cell;// 产生单元格
            HSSFCellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
            style.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);
            // 产生一行
            row = sheet.createRow(0);
            // 写入各个字段的列头名称
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
                // 获得列号
                int col = getExcelCol(attr.column());
                // 创建列
                cell = row.createCell(col);
                // 设置列中写入内容为String类型
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                // 写入列名
                cell.setCellValue(attr.name());
                // 如果设置了提示信息则鼠标放上去提示.
                if (!attr.prompt().trim().equals("")) {
                    // 这里默认设了2-101列提示.
                    setHSSFPrompt(sheet, "", attr.prompt(), 1, 100, col, col);
                }
                // 如果设置了combo属性则本列只能选择不能输入
                if (attr.combo().length > 0) {
                    // 这里默认设了2-101列只能选择不能输入.
                    setHSSFValidation(sheet, attr.combo(), 1, 100, col, col);
                }
                cell.setCellStyle(style);
            }
            int startNo = 0;
            int endNo = list.size();
            // 写入各条记录,每条记录对应excel表中的一行
            for (int i = startNo; i < endNo; i++) {
                row = sheet.createRow(i + 1 - startNo);
                // 得到导出对象.
                T vo = (T) list.get(i);
                for (int j = 0; j < fields.size(); j++) {
                    // 获得field.
                    Field field = fields.get(j);
                    // 设置实体类私有属性可访问
                    field.setAccessible(true);
                    ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
                    try {
                        // 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                        if (attr.isExport()) {
                            // 创建cell
                            cell = row.createCell(getExcelCol(attr.column()));
                            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                            cell.setCellValue(field.get(vo) == null ? ""
                                    // 如果数据存在就填入,不存在填入空格.
                                    : String.valueOf(field.get(vo)));
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return workbook;
    }
    /** 
     * 将EXCEL中A,B,C,D,E列映射成0,1,2,3 
     *  
     * @param col 
     */  
    public static int getExcelCol(String col) {  
        col = col.toUpperCase();  
        // 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。  
        int count = -1;  
        char[] cs = col.toCharArray();  
        for (int i = 0; i < cs.length; i++) {  
            count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);  
        }  
        return count;  
    }  
    /**
     * 设置单元格上提示 
     *  
     * @param sheet 
     *            要设置的sheet. 
     * @param promptTitle 
     *            标题 
     * @param promptContent 
     *            内容 
     * @param firstRow 
     *            开始行 
     * @param endRow 
     *            结束行 
     * @param firstCol 
     *            开始列 
     * @param endCol 
     *            结束列 
     * @return 设置好的sheet. 
     */  
    public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle,  
            String promptContent, int firstRow, int endRow, int firstCol,  
            int endCol) {  
        // 构造constraint对象  
        DVConstraint constraint = DVConstraint  
                .createCustomFormulaConstraint("DD1");  
        // 四个参数分别是：起始行、终止行、起始列、终止列  
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                endRow, firstCol, endCol);  
        // 数据有效性对象  
        HSSFDataValidation data_validation_view = new HSSFDataValidation(  
                regions, constraint);  
        data_validation_view.createPromptBox(promptTitle, promptContent);  
        sheet.addValidationData(data_validation_view);  
        return sheet;  
    }  
    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet
     *            要设置的sheet.
     * @param textlist
     *            下拉框显示的内容
     * @param firstRow
     *            开始行
     * @param endRow
     *            结束行
     * @param firstCol
     *            开始列
     * @param endCol
     *            结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFValidation(HSSFSheet sheet,
            String[] textlist, int firstRow, int endRow, int firstCol,  
            int endCol) {  
        // 加载下拉列表内容  
        DVConstraint constraint = DVConstraint  
                .createExplicitListConstraint(textlist);  
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列  
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,  
                endRow, firstCol, endCol);  
        // 数据有效性对象  
        HSSFDataValidation data_validation_list = new HSSFDataValidation(  
                regions, constraint);  
        sheet.addValidationData(data_validation_list);  
        return sheet;  
    }  
    /**
     * 得到实体类所有通过注解映射了数据表的字段 
     *  
     * @param fields
     * @return 
     */  
    private List<Field> getMappedFiled(Class clazz, List<Field> fields) {
        if (fields == null) {  
            fields = new ArrayList<>();
        }  

        Field[] allFields = clazz.getDeclaredFields();// 得到所有定义字段  
        // 得到所有field并存放到一个list中.  
        for (Field field : allFields) {  
            if (field.isAnnotationPresent(ExcelVOAttribute.class)) {  
                fields.add(field);  
            }  
        }  
        if (clazz.getSuperclass() != null  
                && !clazz.getSuperclass().equals(Object.class)) {  
            getMappedFiled(clazz.getSuperclass(), fields);  
        }  
        return fields;
    }
    /**
     *
     * @param response          响应流
     * @param fileName          保存excel的文件名（文件名要唯一否则并发报错）
     * @param list              要保存的对象列表
     * @param sheetName         sheet名字
     */
    public  void webOutputExcel(List<T> list, String sheetName, HttpServletResponse response, String fileName) {
        HSSFWorkbook exportExcelForWeb = exportExcelForWeb(list, sheetName);
        try {
            OutputStream output = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename="+fileName);
            response.setContentType("application/msexcel");
            exportExcelForWeb.write(output);
            output.close();
        } catch (Exception e) {

        }
    }
    /**
     *
     * @param response          响应流
     * @param fileName          保存excel的文件名（文件名要唯一否则并发报错）
     * @param list              要保存的对象列表
     */
    public  void webOutputExcel(List<T> list, HttpServletResponse response, String fileName) {
        webOutputExcel(list,"sheet1",response,fileName);
    }
}