package top.ilovemyhome.task.core.share;

import java.time.LocalDate;
import java.util.List;

public record Foo(Long id, List<Bar> barList, LocalDate someDate) {}
