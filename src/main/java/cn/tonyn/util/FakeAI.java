package cn.tonyn.util;

import net.mamoe.mirai.message.data.Image;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.*;
import java.util.Random;

public class FakeAI {
    public static int MaxAnswerPerQuestion=64;
    public static void record(String question, String answer, String who){
        if(question.startsWith("_")){
            return;
        }
        if(question.length()<=128){
            File qf=new File("data/FakeAI/records/"+question+".xlsx");
            try{
                if(!qf.isFile()){
                    XSSFWorkbook workbook=new XSSFWorkbook();
                    XSSFSheet sheet=workbook.createSheet();
                    sheet.createRow(0).createCell(0);
                    sheet.createRow(1).createCell(0);
                    sheet.createRow(2).createCell(0);
                    XSSFRow row0=sheet.getRow(0);
                    XSSFCell cell0=row0.getCell(0);
                    cell0.setCellValue(0);
                    workbook.write(new FileOutputStream(qf));
                    Log.write("File created:"+qf,"IOStream");
                    //递归实现记录
                    record(question,answer,who);
                }else{
                    XSSFWorkbook workbook=new XSSFWorkbook(new FileInputStream(qf));
                    XSSFSheet sheet=workbook.getSheetAt(0);
                    XSSFRow row0=sheet.getRow(0);
                    XSSFRow row1=sheet.getRow(1);
                    XSSFRow row2=sheet.getRow(2);
                    //获得当前记录总数
                    XSSFCell cell_line0=row0.getCell(0);
                    int i= (int) cell_line0.getNumericCellValue();
                    i++;
                    XSSFCell cell_line1=row1.createCell(i);
                    cell_line1.setCellValue(answer);

                    XSSFCell cell_line2=row2.createCell(i);
                    cell_line2.setCellValue(who);

                    //记录数自加
                    cell_line0.setCellValue(i);
                    workbook.write(new FileOutputStream(qf));
                }

            }catch (IOException e){
                Log.write(e.getMessage());
            }
        }else{
            Log.write("Too long filename!(>128)","Error");
        }


    }

    public static String getAnswer(String question){
        File qf=new File("data/FakeAI/records/"+question+".xlsx");
        try{
            if(!qf.isFile()){
                return null;
            }
            if(qf.isFile()){
                XSSFWorkbook workbook=new XSSFWorkbook(new FileInputStream(qf));
                XSSFSheet sheet=workbook.getSheetAt(0);
                XSSFRow row0=sheet.getRow(0);
                XSSFRow row1=sheet.getRow(1);
                XSSFRow row2=sheet.getRow(2);
                //获得当前记录总数
                XSSFCell cell_line0=row0.getCell(0);
                int i= (int) cell_line0.getNumericCellValue()+1;
                Random r=new Random();
                XSSFCell cell_line1=row1.getCell(r.nextInt(i));
                if(cell_line1==null){
                    return "";
                }
                String s=cell_line1.getStringCellValue();
                return s;
            }

        }catch (IOException e){
            Log.write(e.getMessage());
            return null;
        }
        return null;
    }
    public static String[] getAllAnswers(String question){
        File qf=new File("data/FakeAI/records/"+question+".xlsx");
        try{
            if(!qf.isFile()){
                System.out.println("no such file :"+question+".xlsx");
                return null;
            }
            if(qf.isFile()){
                XSSFWorkbook workbook=new XSSFWorkbook(new FileInputStream(qf));
                XSSFSheet sheet=workbook.getSheetAt(0);
                XSSFRow row0=sheet.getRow(0);
                XSSFRow row1=sheet.getRow(1);
                XSSFRow row2=sheet.getRow(2);
                //获得当前记录总数
                XSSFCell cell_line0=row0.getCell(0);
                int i= (int) cell_line0.getNumericCellValue();
                String[] s = new String[MaxAnswerPerQuestion];
                for(int a=0;a<=i;){
                    XSSFCell cell_line1=row1.getCell(a);
                    s[a]=cell_line1.getStringCellValue();
                    a++;
                }
                return s;
            }

        }catch (IOException e){
            Log.write(e.getMessage());
            return null;
        }
        return null;
    }
}
