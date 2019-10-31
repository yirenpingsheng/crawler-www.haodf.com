package com.hao.crawler;

import com.hao.model.DataModel;
import com.hao.model.Hospital;
import com.hao.utils.ExcelUtils;
import com.hao.utils.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@SuppressWarnings("all")
public class CrawlerHospital {

    public static void main(String[] args) {
        cHospital("https://www.haodf.com", "/yiyuan/guangdong/list.htm", "D:\\GuangDongHospital.xls", "D:\\error.txt");
    }

    /**
     * @param mainUrl      好大夫主页
     * @param provinceUrl  爬取省的Url
     * @param saveFile     数据保存路径.XLS
     * @param errorLogPath 错误信息保存路径
     */
    public static void cHospital(String mainUrl, String provinceUrl, String saveFile, String errorLogPath) {
        List<DataModel> hospitalList;
        FileOutputStream fos = null;
        Workbook wb = null;
        String[] colFields = {"index", "医院名称", "医院详情Url", "医院等级", "医院类型", "医院地址", "医院电话", "路线", "经度", "纬度"};
        try {
            System.err.println(">> 爬虫程序开始运行");

            fos = new FileOutputStream(new File(saveFile));
            wb = new HSSFWorkbook();
            // 设置单元格样式
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Crawler crawler = new Crawler(mainUrl);

            // 爬取城市医院列表
            hospitalList = crawler.getCityHospital(provinceUrl);

            System.err.println(">> 开始爬取医院详细信息");
            for (int i = 0; i < hospitalList.size(); i++) {
                String city = hospitalList.get(i).getCity();
                List<Hospital> hospitals = hospitalList.get(i).getHospitals();

                System.err.println(">> " + city);

                // 创建城市工作簿
                Sheet sheet = wb.createSheet(city);
                // 创建列名行
                Row row = sheet.createRow(0);
                for (int j = 0; j < colFields.length; j++)
                    ExcelUtils.createTextCell(row, j, colFields[j], cellStyle);

                for (int j = 0; j < hospitals.size(); j++) {
                    Hospital hospital = hospitals.get(j);

                    // 创建行
                    row = sheet.createRow(j + 1);
                    ExcelUtils.createTextCell(row, 0, "" + (j + 1), cellStyle);
                    ExcelUtils.createTextCell(row, 1, hospital.getName(), cellStyle);
                    ExcelUtils.createTextCell(row, 2, hospital.getUrl(), cellStyle);

                    System.out.println(">> index：" + (j + 1));
                    // 爬取医院详情信息
                    try {
                        crawler.getHospitalDetail(hospital);
                        Thread.sleep(500);
                    } catch (Exception e) {
                        try {
                            Thread.sleep(3000);
                            crawler.getHospitalDetail(hospital);
                        } catch (Exception e2) {
                            try {
                                Thread.sleep(10000);
                                crawler.getHospitalDetail(hospital);
                            } catch (Exception e3) {
                                // 错误信息写入日志
                                Crawler.writeErrorMsg(errorLogPath, "[ city=" + city + ", index=" + (j + 1)
                                        + ", hsptName=" + hospital.getName() + ", url=" + hospital.getUrl() + " ]");
                                Thread.sleep(20000);
                            }
                        }
                    }

                    System.out.println();

                    // 医院数据写入Excel
                    ExcelUtils.createTextCell(row, 3, hospital.getLevel(), cellStyle);
                    ExcelUtils.createTextCell(row, 4, hospital.getType(), cellStyle);
                    ExcelUtils.createTextCell(row, 5, hospital.getAddress(), cellStyle);
                    ExcelUtils.createTextCell(row, 6, hospital.getPhone(), cellStyle);
                    ExcelUtils.createTextCell(row, 7, hospital.getRoute(), cellStyle);
                    ExcelUtils.createTextCell(row, 8, hospital.getLongitude(), cellStyle);
                    ExcelUtils.createTextCell(row, 9, hospital.getLatitude(), cellStyle);
                }
            }
            System.err.println(">> 爬取医院详细信息结束");

            System.err.println(">> 爬虫程序结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                wb.write(fos);
                fos.flush();
            } catch (Exception e2) {
            }
            IOUtils.close(wb);
            IOUtils.close(fos);
        }
    }

}