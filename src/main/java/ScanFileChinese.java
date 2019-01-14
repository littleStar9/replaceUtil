import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件中中文扫描工具类.
 *
 */
public class ScanFileChinese {
    //扫描结果
    private static List<String> chineseList = new ArrayList<String>();

    private static List<String> supplychainsoaList = new ArrayList<String>();
    private static List<String> supplychainapiList = new ArrayList<String>();
    private static List<String> supplychainpcapiList = new ArrayList<String>();
    private static List<String> scmbasesoaList = new ArrayList<String>();
    private static List<String> stocksoaList = new ArrayList<String>();
    private static List<String> dmallapiList = new ArrayList<String>();

    private static int allfileNum = 0;
    private static int allContentNum = 0;

    private static int fileNum = 0;
    private static int typeFileNum = 0;

    private static String[] allContentStrings = new String[10000];
    private static String[] contentAndPath = new String[10000];
    private static int contentNum = 0;

    //待扫描文件目录
    private static List<String> paths = new ArrayList<String>();
    //扫描过程中可忽略的文件名
    private static List<String> fileFilters = new ArrayList<String>();
    //扫描过程中可忽略的日志内容
    private static String[] loggerLabels = { "logger"};

    private static void initFilePath() {
        paths.add("D:\\workspace\\dmall-api");
    }

    private static void initFileFilters(){
        fileFilters.add("InstanceGetBill.java".toLowerCase());
    }

    /**
     * 扫描文件夹.找出所有含中文的文件，输出文件名及中文内容
     *
     * @param strPath
     * @throws IOException
     */
    private static void refreshFileList(String strPath) throws IOException {
        File dir = new File(strPath);
        File[] files = dir.listFiles();

        if (files == null)
            return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                //子目录递归调用，跳过当前及上级目录
                if (!files[i].getName().startsWith(".")){
                    refreshFileList(files[i].getAbsolutePath());
                }
            } else {
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                //扫描并处理java文件
                if (strFileName.endsWith(".java") && !isFilterFile(strFileName) && !strFileName.contains("test") && !strFileName.contains("scmultimsg") && !strFileName.contains("i18n")){
                    read(files[i]);
                }
            }
        }
    }

    private static boolean isFilterFile(String strFileName) {
        for (int j = 0; j < fileFilters.size(); j++) {
            if (strFileName.endsWith(fileFilters.get(j).toLowerCase())){
                return true;
            }
        }
        return false;
    }

    private static void read(File file) throws IOException {
        FileReader reader;
        reader = new FileReader(file);
        BufferedReader bf = new BufferedReader(reader);
        try {
            String row = null;
            while ((row = bf.readLine()) != null) {
                //逐行读取，非纯注释行则处理，如注释换行，需先处理原始文件格式
                if (!row.trim().startsWith("//")
                        && !row.trim().startsWith("*")
                        && !row.trim().startsWith("/*")
                        && !row.trim().contains("@Api")){

                    dealFile(row, file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dealFile(String content, String fileName)
            throws IOException {
        if (StringUtils.isEmpty(content) || isLogger(content)) {
            return;
        }
        //中文的正则
        String key = "\"(([^\"]*)[^\\x00-\\xff]+([^\"]*))\"";
        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            if (!matcher.group().contains("/*")
                    && !matcher.group().toLowerCase().contains("logger.")){
                //如成功匹配则记录文件名及中文内容

                if (content.contains(".info") || content.contains(".error") || content.contains("errorMsg") || content.contains("Import") || content.contains(".warn") || content.contains(".debug") || content.contains("println") || fileName.toLowerCase().contains("export") || fileName.toLowerCase().contains("impor") || fileName.contains("Excel") || content.contains("Assert") || (content.contains("Exception") && !content.contains("BizException"))) {
                    continue;
                } else if (content.contains("message")){
                    add("validator : " + matcher.group() + "|" + fileName);
                } else if (content.contains("BizException") || content.contains("setMessage") || content.contains("ResultMap")  || (content.contains("setResult") && content.contains("false"))){
                    add("Exception : " + matcher.group() + "|" + fileName);
                } else if (content.contains("ApiOperation")){
                    add("ApiOperation : " + matcher.group() + "|" + fileName);
                } else if (fileName.contains("Enum")){
                    add("Enum : " + matcher.group() + "|" + fileName);
                } else if (fileName.contains("Constant")){
                    add("Constant : " + matcher.group() + "|" + fileName);
                } else {
                    add(matcher.group() + "|" + fileName);
                }
            }
        }
    }

    private static void addChinese(String content, String fileName) {
        boolean input = true;
        for (int i = 0; i < contentNum; i++) {
            if (!StringUtils.isEmpty(allContentStrings[i]) && allContentStrings[i].equals(content)) {
                input = false;
                break;
            }
        }
        if (input) {
            allContentStrings[contentNum] = content;
            contentAndPath[contentNum++] = fileName;
        }
    }

    private static void add(String content) {
        if (content.contains("supplychain-soa")) {
            supplychainsoaList.add(content);
            addChinese(content.split("\\|")[0], content.split("\\|")[1]);
        } else if (content.contains("supplychain-api")) {
            supplychainapiList.add(content);
            addChinese(content.split("\\|")[0], content.split("\\|")[1]);
        } else if (content.contains("supplychain-pc-api")) {
            supplychainpcapiList.add(content);
            addChinese(content.split("\\|")[0], content.split("\\|")[1]);
        } else if (content.contains("scmbase-soa")) {
            scmbasesoaList.add(content);
            addChinese(content.split("\\|")[0], content.split("\\|")[1]);
        } else if(content.contains("stock-soa")) {
            stocksoaList.add(content);
            addChinese(content.split("\\|")[0], content.split("\\|")[1]);
        } else if(content.contains("dmall-api")) {
            dmallapiList.add(content);
            addChinese(content.split("\\|")[0], content.split("\\|")[1]);
        }
    }

    //判断是否日志记录行，日志行无需处理中文
    private static Boolean isLogger(String content) {
        for (int i = 0; i < loggerLabels.length; i++) {
            if (content.toLowerCase().trim().startsWith(loggerLabels[i].toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    //递归检查指定目录下的所有文件，找出所有含中文的内容
    private static void scanFile4Chinese() throws IOException {
        initFilePath();
        initFileFilters();

        for (int i = 0; i < paths.size(); i++) {
            refreshFileList(paths.get(i));
        }
    }


    private static void writeByProject() throws IOException {

        OutputStreamWriter allOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("D:\\wex\\allContent.txt"));
        for (int i = 0; i < contentNum; i++) {
            allOutputStreamWriter.write(allContentStrings[i] + "\r\n");
            System.out.println(allContentStrings[i] + contentAndPath[i] + "\r\n");

        }
        allOutputStreamWriter.close();

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("D:\\wex\\test\\supplychainapi.txt"));
        writeByType(outputStreamWriter, supplychainapiList);
        allContentNum += supplychainapiList.size();

        OutputStreamWriter outputStreamWriter1 =  new OutputStreamWriter(new FileOutputStream("D:\\wex\\test\\supplychainpcapi.txt"));
        writeByType(outputStreamWriter1, supplychainpcapiList);
        allContentNum += supplychainpcapiList.size();

        OutputStreamWriter outputStreamWriter2 =  new OutputStreamWriter(new FileOutputStream("D:\\wex\\test\\supplychainsoa.txt"));
        writeByType(outputStreamWriter2, supplychainsoaList);
        allContentNum += supplychainsoaList.size();

        OutputStreamWriter outputStreamWriter3 =  new OutputStreamWriter(new FileOutputStream("D:\\wex\\test\\scmbasesoa.txt"));
        writeByType(outputStreamWriter3, scmbasesoaList);
        allContentNum += scmbasesoaList.size();

        OutputStreamWriter outputStreamWriter4 =  new OutputStreamWriter(new FileOutputStream("D:\\wex\\test\\stocksoa.txt"));
        writeByType(outputStreamWriter4, stocksoaList);
        allContentNum += stocksoaList.size();

        OutputStreamWriter outputStreamWriter5 =  new OutputStreamWriter(new FileOutputStream("D:\\wex\\test\\dmallapi.txt"));
        writeByType(outputStreamWriter5, dmallapiList);
        allContentNum += dmallapiList.size();

    }

    private static void writeByType(OutputStreamWriter outputStreamWriter, List<String> list) throws IOException {

        if (list.isEmpty()) {
            return;
        }
        String old = list.get(0);
        typeFileNum = 0;
        for (int i = 0, j = 0; i < list.size(); i++) {
            if (list.get(i).contains("validator")) {
                writeToFile(outputStreamWriter, old, list.get(i));
                old = list.get(i);
                j++;
            }
            if(i == list.size() - 1) {
                outputStreamWriter.write("validator个数：" + j + "  " + "文件个数：" + typeFileNum);
                outputStreamWriter.write("\r\n\r\n");
            }
        }

        typeFileNum = 0;
        for (int i = 0, j = 0; i < list.size(); i++) {
            if (list.get(i).contains("Exception")) {
                writeToFile(outputStreamWriter, old, list.get(i));
                old = list.get(i);
                j++;
            }
            if(i == list.size() - 1) {
                outputStreamWriter.write("Exception个数：" + j + "  " + "文件个数：" + typeFileNum);
                outputStreamWriter.write("\r\n\r\n");
            }
        }

        typeFileNum = 0;
        for (int i = 0, j = 0; i < list.size(); i++) {
            if (list.get(i).contains("ApiOperation")) {
                writeToFile(outputStreamWriter, old, list.get(i));
                old = list.get(i);
                j++;
            }
            if(i == list.size() - 1) {
                outputStreamWriter.write("ApiOperation个数：" + j + "  " + "文件个数：" + typeFileNum);
                outputStreamWriter.write("\r\n\r\n");
            }
        }

        typeFileNum = 0;
        for (int i = 0, j = 0; i < list.size(); i++) {
            if (list.get(i).contains("Constant")) {
                writeToFile(outputStreamWriter, old, list.get(i));
                old = list.get(i);
                j++;
            }
            if(i == list.size() - 1) {
                outputStreamWriter.write("Constant个数：" + j + "  " + "文件个数：" + typeFileNum);
                outputStreamWriter.write("\r\n\r\n");
            }
        }

        typeFileNum = 0;
        for (int i = 0, j = 0; i < list.size(); i++) {
            if (list.get(i).contains("Enum")) {
                writeToFile(outputStreamWriter, old, list.get(i));
                old = list.get(i);
                j++;

            }
            if(i == list.size() - 1) {
                outputStreamWriter.write("Enum个数：" + j + "  " + "文件个数：" + typeFileNum);
                outputStreamWriter.write("\r\n\r\n");
            }
        }

        typeFileNum = 0;
        for (int i = 0, j = 0; i < list.size(); i++) {
            if (!list.get(i).contains("validator") && !list.get(i).contains("Enum") && !list.get(i).contains("Exception") && !list.get(i).contains("ApiOperation") && !list.get(i).contains("Constant")) {
                writeToFile(outputStreamWriter, old, list.get(i));
                old = list.get(i);
                j++;
            }
            if(i == list.size() - 1) {
                outputStreamWriter.write("其他类型个数：" + j + "  " + "文件个数：" + typeFileNum);
                outputStreamWriter.write("\r\n\r\n");
            }
        }
        outputStreamWriter.write( "总个数：" + list.size() + " 总文件个数 ： " + fileNum);
        fileNum = 0;
        outputStreamWriter.close();
    }


    private static void writeToFile(OutputStreamWriter outputStreamWriter, String old, String str) throws IOException {
        String[] olds = old.split("\\\\");
        if (!(str.contains(olds[olds.length - 1]))) {
            outputStreamWriter.write("\r\n");
            fileNum++;
            typeFileNum++;
            allfileNum++;
        }
        outputStreamWriter.write(str);
        outputStreamWriter.write("\r\n");
    }

    private static void writeAllNum() throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("D:\\wex\\test\\allNum.txt"));
        outputStreamWriter.write("修改点：" + allContentNum + "修改文件：" + allfileNum);
        outputStreamWriter.close();
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        scanFile4Chinese();
        writeByProject();
        writeAllNum();
        if (chineseList.size() > 0){
            System.out.println("------------------------------------");
            System.out.println("中文数量："+ chineseList.size());
            for (String str : chineseList) {
                System.out.println(str);
            }
        }



    }
}