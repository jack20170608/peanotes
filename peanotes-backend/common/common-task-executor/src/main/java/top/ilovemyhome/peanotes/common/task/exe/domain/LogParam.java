package top.ilovemyhome.peanotes.common.task.exe.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import top.ilovemyhome.peanotes.backend.common.Constants;

import java.time.LocalDateTime;

public record LogParam(
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
    LocalDateTime logDateTime
    , Long taskId
    , String taskName
    , Long logId
    , int fromLineNum) {

}
