import java.io.*;
import java.sql.SQLOutput;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        try {
            StringBuffer path = new StringBuffer();
            path.append(System.getenv("windir"));
            path.append("\\system32\\drivers\\etc\\");
            File file = new File(path.toString() + "HOSTS");
            String addr = getGithubAddr();
            System.out.println("new addr:" + addr);
            StringBuffer newHost = getHostContent(file);
            newHost.append("\r\n").append(addr);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(newHost.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println("更新出现异常，请重试！" + e.getMessage());
        }
        System.out.println("更新host完成.");
    }

    private static StringBuffer getHostContent(File file) throws Exception {
        StringBuffer newHost = new StringBuffer();
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String temstr = null;
        while ((temstr = bufferedReader.readLine()) != null) {
            //如果有就替换
            if (temstr.indexOf("github") == -1) {
                newHost.append(temstr).append("\r\n");
            }
        }
        bufferedReader.close();
        reader.close();
        return newHost;
    }

    private static String getGithubAddr() {
        StringBuffer newAddr = new StringBuffer();
        String[] host = {"github.com", "github.global.ssl.fastly.net"};
        for (int i = 0; i < host.length; i++) {
            String result = HttpRequest.sendGet("https://ips.ipaddress.com/report/" + host[i], "");
            String pattern = "<th>IPv4 Addresses</th><td><ul class=\"comma-separated\"><li>(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
            Pattern r = Pattern.compile(pattern);
            // 现在创建 matcher 对象
            Matcher m = r.matcher(result);
            while (m.find()) {
                newAddr.append(m.group(1));
                break;
            }
            newAddr.append(" ").append(host[i]).append("\r\n");
        }
        // 创建 Pattern 对象
        return newAddr.toString();
    }
}
