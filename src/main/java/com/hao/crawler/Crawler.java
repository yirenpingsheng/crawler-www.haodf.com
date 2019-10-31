package com.hao.crawler;

import com.hao.model.DataModel;
import com.hao.model.Hospital;
import com.hao.utils.CrawlerUtils;
import com.hao.utils.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class Crawler {

    // 主页
    private String mainUrl;
    // 头信息
    private List<BasicHeader> headers;
    // HttpClient对象
    private CloseableHttpClient httpClient;
    // 编码格式
    private String encoding = "UTF-8";
    // Html页面
    private String html;

    public Crawler(String mainUrl) {
        // 主页
        this.mainUrl = mainUrl;
        // 头信息
        headers = new ArrayList<BasicHeader>();
        headers.add(new BasicHeader("Host", "www.haodf.com"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Cache-Control", "max-age=0"));
        headers.add(new BasicHeader("Upgrade-Insecure-Requests", "1"));
        headers.add(new BasicHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36"));
        headers.add(new BasicHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate, br"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.9"));
        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000).build();
        // 创建HttpClient对象
        httpClient = HttpClients.custom().setDefaultHeaders(headers).setDefaultRequestConfig(requestConfig).build();
    }

    /**
     * 爬取城市医院列表
     *
     * @param provinceUrl
     * @return
     * @throws Exception
     */
    public List<DataModel> getCityHospital(String provinceUrl) throws Exception {
        List<DataModel> hospitalList = new ArrayList<DataModel>();

        HttpGet httpGet = new HttpGet(mainUrl + provinceUrl);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        html = EntityUtils.toString(response.getEntity(), encoding);
        CrawlerUtils.releaseConnection(httpGet, response);

        Document doc = Jsoup.parse(html);
        Elements cityEles = doc.select(".m_title_green");
        Elements cityHospitalEles = doc.select(".m_ctt_green");

        if (cityEles.size() != cityHospitalEles.size())
            throw new RuntimeException("城市列表抓取错误");

        // 爬取城市
        for (int i = 0; i < cityEles.size(); i++) {
            String city = cityEles.get(i).text();
            DataModel dataModel = new DataModel(city);
            hospitalList.add(dataModel);
        }

        // 爬取医院名称与详情Url
        for (int i = 0; i < cityHospitalEles.size(); i++) {
            Elements hospitalEles = cityHospitalEles.get(i).select("a");

            DataModel dataModel = hospitalList.get(i);
            System.out.println();
            System.err.println(">> 城市：" + dataModel.getCity());
            List<Hospital> hospitals = new ArrayList<Hospital>();

            for (int j = 0; j < hospitalEles.size(); j++) {
                Element hospitalEle = hospitalEles.get(j);
                String name = hospitalEle.text().trim();
                String url = hospitalEle.attr("href").trim();
                Hospital hospital = new Hospital(name, mainUrl + url);
                hospitals.add(hospital);
                System.out.println(">> 医院：" + name);
            }
            dataModel.setHospitals(hospitals);
        }

        return hospitalList;
    }

    /**
     * 爬取医院详情信息
     *
     * @param hospital
     * @throws Exception
     */
    public void getHospitalDetail(Hospital hospital) throws Exception {
        System.out.println(">> " + hospital.getName());

        HttpGet httpGet = new HttpGet(hospital.getUrl());
        CloseableHttpResponse response = httpClient.execute(httpGet);
        html = EntityUtils.toString(response.getEntity(), encoding);
        CrawlerUtils.releaseConnection(httpGet, response);

        while (html == null || html.contains("您访问频率太高，请稍候再试。")) {
            Thread.sleep(3000);
            httpGet = new HttpGet(hospital.getUrl());
            response = httpClient.execute(httpGet);
            html = EntityUtils.toString(response.getEntity(), encoding);
            CrawlerUtils.releaseConnection(httpGet, response);
        }

        Document doc = Jsoup.parse(html);

        // 医院等级、医院类型
        Elements levelAndTypeEles = doc.select(".hospital-label-item");
        if (levelAndTypeEles != null && levelAndTypeEles.size() > 0) {
            if (levelAndTypeEles.size() == 1) {
                hospital.setType(levelAndTypeEles.get(0).text().trim());
            } else if (levelAndTypeEles.size() == 2) {
                hospital.setLevel(levelAndTypeEles.get(0).text().trim());
                hospital.setType(levelAndTypeEles.get(1).text().trim());
            }
        }

        // 医院电话
        String phone = doc.select(".h-d-c-item-text.js-phone-text").text().trim();
        hospital.setPhone(phone);

        // 详情Url
        String detailUrl = doc.select(".h-d-c-item-link[href^=//map.haodf.com]").attr("href");
        if (detailUrl != null && !"".equals(detailUrl)) {
            System.out.println(">> https:" + detailUrl);
            httpGet = new HttpGet("https:" + detailUrl);
            response = httpClient.execute(httpGet);
            html = EntityUtils.toString(response.getEntity(), encoding);
            CrawlerUtils.releaseConnection(httpGet, response);

            doc = Jsoup.parse(html);

            Elements tables = doc.select(".bluepanel").select("table");
            Element table = tables.get(3);

            // 医院地址
            Elements addressEles = table.select("td:contains(地址：)");
            if (addressEles != null)
                hospital.setAddress(addressEles.next().text().trim());

            // 路线
            Elements routeEles = table.select("td:contains(怎么走：)");
            if (routeEles != null)
                hospital.setRoute(routeEles.next().text().trim());

            // 经纬度
            Pattern p = Pattern.compile("new\\s+BMap\\.Point\\((.*),\\s*(.*)\\);");
            Matcher m = p.matcher(html);
            if (m.find() && m.groupCount() == 2) {
                hospital.setLongitude(m.group(1).trim());
                hospital.setLatitude(m.group(2).trim());
            }
        } else {
            // 医院地址
            Elements addressEles = doc.select(".h-d-c-item-text span:contains(地址：)");
            if (addressEles != null)
                hospital.setAddress(addressEles.next().text().trim());

            // 路线
            Elements routeEles = doc.select(".h-d-c-item-text span:contains(路线：)");
            if (routeEles != null)
                hospital.setRoute(routeEles.next().text().trim());
        }
    }

    /**
     * 写入错误日志
     *
     * @param errorLogPath 错误文件路径
     * @param msg          错误信息
     */
    public static void writeErrorMsg(String errorLogPath, String msg) {
        File errorLogFile = new File(errorLogPath);
        FileWriter fw = null;
        try {
            if (!errorLogFile.exists())
                errorLogFile.createNewFile();

            fw = new FileWriter(errorLogFile, true);
            fw.write(msg);
            fw.write("\r\n");

            fw.flush();
        } catch (Exception e) {
        } finally {
            IOUtils.close(fw);
        }
    }

}