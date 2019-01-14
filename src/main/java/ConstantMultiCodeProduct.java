
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstantMultiCodeProduct {

    private final static String PRE_KEY = "SC_MULTI_MSG_";
    private static int num = 826;
    private static String sourceFile = "D:\\workspace\\a\\supplychain-soa\\supplychain-provider\\src\\main\\java\\com\\dfire\\soa\\supplychain\\biz\\impl\\desktop\\BizAppHomeModuleService.java";
    private static String replaceFile = "D:\\wex\\Constant6.java";

    private static StringBuilder replaceStringBuilder = new StringBuilder();

    private static String[] contentStrings = new String[10000];
    private static String[] multiStrings = new String[10000];
    private static int p = 0;


    public static void main(String[] args) {
        if (sourceFile.contains(".java")){
            readAndReplace(sourceFile);
        } else {
            refreshFileList(sourceFile);
        }
    }

    public static void readAndReplace(String path) {
        try {

            StringBuilder sourceStringBuilder = new StringBuilder(); //源文件替换后存储在StringBuilder中，回写回源文件。

            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String row;
            while ((row = bufferedReader.readLine()) != null) {
                row += "\r\n";
                //逐行读取，非纯注释行则处理，如注释换行，需先处理原始文件格式
                if (validatorContent(row)){
                    //中文替换
                    dealFile(row, sourceStringBuilder);
                } else {
                    sourceStringBuilder.append(row);
                }
            }
            bufferedReader.close();

            //替换后写到指定文件
            OutputStreamWriter replaceOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(replaceFile));
            replaceOutputStreamWriter.write(replaceStringBuilder.toString());
            replaceOutputStreamWriter.close();

            //源文件中文替换，写回源文件
            OutputStreamWriter sourceOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(path));
            sourceOutputStreamWriter.write(sourceStringBuilder.toString());
            sourceOutputStreamWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //中文替换
    private static void dealFile(String content, StringBuilder sourceStringBuilder) throws IOException {
        //中文的正则
        String key = "\"(([^\"]*)[^\\x00-\\xff]+([^\"]*))\"";
        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            boolean bool = true;
            for (int i = 0; i < p; i++) {
                if (!contentStrings[i].equals("") && contentStrings[i].equals(matcher.group())) {
                    content = content.replace(matcher.group(), multiStrings[i]);
                    bool = false;
                    break;
                }
            }
            if (bool) {
                String enumName = PRE_KEY + String.format("%04d", num++);
                String result = enumName + "(\"\", " + matcher.group() + "),";

                replaceStringBuilder.append("\t" + result + "\r\n");

//                String sourceString = "ScmMultiCodeHelpUtil.getTranslation(SbErrException." + enumName + ".name())";
                String sourceString = "ScmMultiCodeHelpUtil.getTranslation(ScMultiMsg." + enumName + ".name())";
//                String sourceString = "\"" +enumName+"\"" ;
                contentStrings[p] = matcher.group();
                multiStrings[p] = sourceString;
                p++;
                content = content.replace(matcher.group(), sourceString);
            }
        }
        sourceStringBuilder.append(content);
    }

    //递归调用文件下的所有java文件
    public static void refreshFileList(String strPath) {
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
                String strFileName = files[i].getAbsolutePath();
                if(validatorFile(strFileName)) {
                    readAndReplace(strFileName);
                }
            }
        }
    }


    private static void readReplaceFile(StringBuilder replaceFirstStringBuilder, StringBuilder replaceEndStringBuilder) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(replaceFile));
            String row;
            int i = 0;
            while ((row = bufferedReader.readLine()) != null) {
                if (i == 0) {
                    replaceFirstStringBuilder.append(row + "\r\n");
                } else if (i == 2){
                    replaceEndStringBuilder.append(row + "\r\n");
                }
                if(row.contains("ScMultiMsg {") && i == 0) {
                    i = 1;
                }
                if (row.contains("String") && i == 1) {
                    i = 2;
                    replaceEndStringBuilder.append(row + "\r\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean validatorContent(String content) {
        boolean bool = !content.trim().startsWith("//")
                && !content.trim().startsWith("*")
                && !content.trim().startsWith("/*")
                && !content.trim().contains("@Api")
                && !content.contains(".info")
                && !content.contains("{}")
                && !content.contains(".error") && !content.contains(".warn") && !content.contains(".debug") && !content.contains("println")
                && !content.contains("Assert");
        return bool;
    }


    private static boolean validatorFile(String strFileName) {
        return strFileName.endsWith(".java")
                && strFileName.contains("Constant")
                && !strFileName.contains("Multi")
                && !strFileName.contains("Mapper")
                && !strFileName.toLowerCase().contains("export")
                && !strFileName.toLowerCase().contains("impor")
                && !strFileName.contains("Excel");
    }
}
