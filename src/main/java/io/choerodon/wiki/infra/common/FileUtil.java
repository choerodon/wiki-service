package io.choerodon.wiki.infra.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import io.choerodon.core.exception.CommonException;

/**
 *
 * @author Zenger
 * @date 2018/7/3
 */
public class FileUtil {

    private FileUtil(){

    }

    /**
     * 通过inputStream流 替换文件的参数
     *
     * @param inputStream 流
     * @param params      参数
     * @return String
     */
    public static String replaceReturnString(InputStream inputStream, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            byte[] b = new byte[32768];
            for (int n; (n = inputStream.read(b)) != -1; ) {
                String content = new String(b, 0, n);
                if (params != null) {
                    for (Object o : params.entrySet()) {
                        Map.Entry entry = (Map.Entry) o;
                        content = content.replace(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                    }
                }
                stringBuilder.append(content);
                stringBuilder.append(System.getProperty("line.separator"));
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new CommonException("error.param.render");
        }
    }

    public static String inputStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            byte[] b = new byte[32768];
            for (int n; (n = inputStream.read(b)) != -1;) {
                stringBuilder.append(new String(b, 0, n));
            }
            return  stringBuilder.toString();
        } catch (IOException e) {
            throw new CommonException("error.param.render");
        }
    }
}
