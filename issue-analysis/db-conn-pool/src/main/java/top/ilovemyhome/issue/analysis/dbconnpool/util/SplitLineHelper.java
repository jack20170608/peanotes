package top.ilovemyhome.issue.analysis.dbconnpool.util;

public class SplitLineHelper {

    public static String getTitleSeparator(String title) {
        int totalLength = 50;
        int titleLength = title.length();
        int sideLength = (totalLength - titleLength - 2) / 2; // 两侧符号长度

        String side = "*".repeat(sideLength); // Java 11+ 支持 String.repeat()
        return String.format("%s %s %s", side, title, side);
    }

    public static String getDynamicSeparator(String content) {
        int length = Math.max(content.length(), 30); // 最小长度30
        String line = "─".repeat(length);
        return String.format("╭%s╮\n│ %s │\n╰%s╯", line, content, line);
    }
}
