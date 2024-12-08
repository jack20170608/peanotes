package top.ilovemyhome.peanotes.common.task.exe.domain;

public record LogResult(int fromLineNum, int toLineNum, String logContent, boolean isEnd) {
}
