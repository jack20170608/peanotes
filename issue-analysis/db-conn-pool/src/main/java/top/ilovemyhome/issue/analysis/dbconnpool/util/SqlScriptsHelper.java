package top.ilovemyhome.issue.analysis.dbconnpool.util;

import java.util.List;
import java.util.ArrayList;

public class SqlScriptsHelper {

    public static List<String> parseSqlScript(String sqlScript) {
        List<String> sqlStatements = new ArrayList<>();
        StringBuilder currentStmt = new StringBuilder();
        boolean inMultiLineComment = false;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        String[] lines = sqlScript.split("\n");
        for (String line : lines) {
            line = line.trim(); // 去除首尾空白（避免空行干扰）
            if (line.isEmpty()) continue;

            int i = 0;
            while (i < line.length()) {
                char c = line.charAt(i);

                // 1. 处理多行注释 /* ... */
                if (inMultiLineComment) {
                    if (i < line.length() - 1 && line.charAt(i) == '*' && line.charAt(i + 1) == '/') {
                        inMultiLineComment = false;
                        i += 2; // 跳过 "*/"
                    } else {
                        i++;
                    }
                    continue;
                }

                // 2. 处理单行注释 --
                if (!inSingleQuote && !inDoubleQuote && i < line.length() - 1 && line.charAt(i) == '-' && line.charAt(i + 1) == '-') {
                    break; // 跳过该行剩余部分（单行注释）
                }

                // 3. 处理单引号字符串 '...'（忽略内部的分号）
                if (c == '\'' && !inDoubleQuote) {
                    inSingleQuote = !inSingleQuote;
                    currentStmt.append(c);
                    i++;
                    continue;
                }

                // 4. 处理双引号字符串 "..."（部分数据库支持，如 PostgreSQL）
                if (c == '"' && !inSingleQuote) {
                    inDoubleQuote = !inDoubleQuote;
                    currentStmt.append(c);
                    i++;
                    continue;
                }

                // 5. 处理分号（语句结束符）：仅当不在字符串和注释中时，才分割语句
                if (c == ';' && !inSingleQuote && !inDoubleQuote) {
                    currentStmt.append(c);
                    String stmt = currentStmt.toString().trim();
                    if (!stmt.isEmpty()) {
                        sqlStatements.add(stmt);
                    }
                    currentStmt.setLength(0); // 重置 StringBuilder，准备下一个语句
                    i++;
                }
                // 6. 处理多行注释开始 /*
                else if (!inSingleQuote && !inDoubleQuote && i < line.length() - 1 && line.charAt(i) == '/' && line.charAt(i + 1) == '*') {
                    inMultiLineComment = true;
                    i += 2; // 跳过 "/*"
                }
                // 7. 其他字符：直接追加到当前语句
                else {
                    currentStmt.append(c);
                    i++;
                }
            }
        }

        // 处理文件末尾可能残留的未结束语句（无分号结尾的情况）
        String remainingStmt = currentStmt.toString().trim();
        if (!remainingStmt.isEmpty()) {
            sqlStatements.add(remainingStmt);
        }

        return sqlStatements;
    }

}
