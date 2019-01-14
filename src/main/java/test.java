import sun.security.util.Resources_zh_CN;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shiluo
 * @date 2019/1/7
 **/
public class test
{
    public static void main(String[] args) {
        String key1 = "MULTI_SC0780";
        String key = "提示：\\r\\n 1.当前仓库出品的商品，就算其所属的分类选择了其他出品仓库，该商品的出品仓库仍为当前仓库。\r\n如：当前仓库出品的商品“西瓜”，其所属分类为“水果”，即使“水果”关联了其他仓库，“西瓜”的出品仓库仍为当前仓库\r\n2.同一商品可以设置多个出品仓库，但加料只可以对应一个出品仓库";
//        System.out.println(key);
        key = key.replace("\\r", "\\\\r").replace("\\n", "\\\\n");
//        System.out.println("1111111111111111111111"+key);
        Pattern pattern = Pattern.compile("(MULTI_[A-Z,0-9]{3}\\d{3}|((?<!\\\"errorCode\\\"\\:\\\")ERR_[A-Z]{2,5}\\d{2,6}))(\\([^\\)]{1,}\\))?");
        Matcher matcher = pattern.matcher(key1);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb, key);
        }
        matcher.appendTail(sb);
        System.out.println(sb.toString());

//        String key = "String\\.format\\(MsgConstant\\.([\\(\\)\\)]|[[a-z]{}\\)]]) ";
//        String key = "String\\.format\\(MsgConstant\\.[\\s\\S]*([\\(\\)\\)]|[[a-z]{1,}\\)])$";
//        String key = "String\\.format\\(MsgConstant\\.[\\s\\S]*[\\(\\)]*(\\(\\)\\)|[A-Z]\\)){0,1}";
//        String key = "String\\.format\\(MsgConstant\\.[\\s\\S]*[\\(\\)]*(\\(\\)\\)|[A-Z]\\)){1}";
/*
        String key = "String\\.format\\(StringConstant\\.[\\s\\S]*(\\(\\)\\)|[a-zA-Z]\\)){1}";
        Pattern pattern = Pattern.compile(key);
        String content = "String selfStock = String.format(StringConstant.STRING_FORMAT_SELF_STOCK, stockNum, inventoryUnitName);";
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            System.out.println(matcher.group());
            String[] paramsArray = matcher.group().split(",");
            paramsArray[0] = paramsArray[0].substring(14, paramsArray[0].length()) + " + \"(\" +";
            String strings = paramsArray[0];
            for(int i = 1; i < paramsArray.length; i++) {
                strings = strings + paramsArray[i].substring(1, paramsArray[i].length()) + " + \",\" + " ;
            }
            strings = strings.substring(0, strings.length()-9)+ "+ \")\"";
            content = content.replace(matcher.group(), strings);
            System.out.println(content);
        }
*/

/*        String content3 = "%s年-第%s期".replaceFirst("%s", "2017");
        System.out.println(content3);
        content3 = content3.replaceFirst("%s", "2017");
        System.out.println(content3);
        String content1 = String.format("%d年-第%d期", 2018,1);
        System.out.println(content1);
        String content2 = String.format("%d年-第%d期", 2017,1);
        System.out.println(content2);
        String content3 = String.format("%s年-第%s期", "2017",1);
        System.out.println(content3);
        MessageFormat.format("%s年-第%d期", "2017",1);
*/

/*        String content3 = "%s年-第%s期";
        String[] paramsArray = new String[10];
        paramsArray[0] = "1";
        paramsArray[1] = "2";

        if (paramsArray[0] != null){
            for(int i = 0; i<paramsArray.length; i++ ){
                content3 = content3 + paramsArray[0];
            }
            System.out.println(content3 );
        }*/

    }
}
