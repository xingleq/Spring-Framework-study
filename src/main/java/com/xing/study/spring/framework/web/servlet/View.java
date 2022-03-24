package com.xing.study.spring.framework.web.servlet;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/24 15:30
 */
public class View {

    private final File viewFile;

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    Pattern pattern = Pattern.compile("\\$\\{[^}]+}", Pattern.CASE_INSENSITIVE);

    public View(File viewFile) {
        this.viewFile = viewFile;
    }


    public void render(Map<String, ?> model, HttpServletResponse resp) throws Exception {
        StringBuilder sb = new StringBuilder();

        RandomAccessFile randomAccessFile = new RandomAccessFile(this.viewFile, "r");

        String line;
        while (null != (line = randomAccessFile.readLine())) {
            // 读到的每行数据
            line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            // 去匹配的正则

            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String paramName = matcher.group();
                paramName = paramName.replaceAll("\\$\\{|}", "");
                Object paramValue = model.get(paramName);
                if (null == paramValue) {
                    continue;
                }
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher = pattern.matcher(line);
            }
            sb.append(line);
        }

        resp.setCharacterEncoding("utf-8");
        resp.setContentType(DEFAULT_CONTENT_TYPE);
        resp.getWriter().write(sb.toString());
    }

    /**
     * 处理特殊字符
     *
     * @param str 字符串
     * @return 特殊字符
     */
    private static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
