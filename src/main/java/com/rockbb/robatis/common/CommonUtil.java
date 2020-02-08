package com.rockbb.robatis.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by Milton on 2015/5/25 at 0:42.
 */
public class CommonUtil {
    /**
     * 添加合适的间隔, 让后方字符串对齐
     *
     * @param str    字符串长度
     * @param size   单个缩进长度
     * @param steps  与后方间隔
     * @param adjust 字符串加长微调
     * @return
     */
    public static StringBuffer genIndentTab(String str, int size, int steps, int adjust) {
        int units = (str.length() + adjust) / size;
        units = (units > steps) ? 1 : steps + 1 - units;
        StringBuffer ts = new StringBuffer();
        for (int j = 0; j < units; j++) ts.append("\t");
        return ts;
    }

    /**
     * 添加合适的间隔, 让后方字符串对齐
     *
     * @param str    字符串长度
     * @param spaces 与后方间隔
     * @return
     */
    public static StringBuffer genIndentSpace(String str, int spaces) {
        int units = spaces - str.length();
        units = (units < 1) ? 1 : units;
        StringBuffer ts = new StringBuffer();
        for (int j = 0; j < units; j++) ts.append(" ");
        return ts;
    }

    /**
     * 检查是否匹配正则
     *
     * @param string 待检测的字符串
     * @param regex  正则
     * @return true:是, false:否
     */
    public static boolean regexMatch(String string, String regex) {
        Pattern p1 = Pattern.compile(regex);
        Matcher m = p1.matcher(string);
        return m.matches();
    }

    /**
     * 提取匹配的正则片段
     *
     * @param string 待提取的字符串
     * @param regex  正则
     * @param index  匹配编号
     * @return 匹配的片段或null
     */
    public static String regexMatch(String string, String regex, int index) {
        if (string != null && string.length() > 0) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(string);
            if (m.find()) {
                return m.group(index);
            }
        }
        return null;
    }

    public static String underscoreName(String camelCaseName) {
        StringBuilder sb = new StringBuilder();
        if (camelCaseName != null && camelCaseName.length() > 0) {
            for (int i = 0; i < camelCaseName.length(); i++) {
                char ch = camelCaseName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    if (i > 0) {
                        sb.append("_");
                    }
                    sb.append(Character.toLowerCase(ch));
                } else {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    public static String camelCaseName(String underscoreName, boolean capitalFirst) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.length() > 0) {
            underscoreName = underscoreName.toLowerCase();
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ("_".charAt(0) == ch) {
                    flag = true;
                } else {
                	if (flag || (result.length() == 0 && capitalFirst)) {
						result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }

    public static List<String> readFileToLines(String file) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void main(String[] args) {
        if (regexMatch("NUMBER(22,4)", "(?i)^number\\(\\d+,\\d+\\)$")) {
            Pattern p = Pattern.compile("(?i)^number\\((\\d+),(\\d+)\\)$");
            Matcher m = p.matcher("NUMBER(22,4)");
            if (m.find()) {
                System.out.println(m.group(1));
                System.out.println(m.group(2));
            }
        } else {
            System.out.println("false");
        }
    }
}
