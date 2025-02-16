package top.ilovemyhome.peanotes.backend.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class JacksonUtil {

    public static final ObjectMapper MAPPER;

    public static String toJson(Object obj) {
        if (Objects.isNull(obj)){
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            LOGGER.error("Failed serializing object to json.", e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)){
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        }catch (Throwable t){
            LOGGER.error("Failed deserializing object to json.", t);
            throw new RuntimeException(t);
        }
    }


    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(json)){
            return null;
        }
        try {
            return MAPPER.readValue(json, typeReference);
        }catch (Throwable t){
            LOGGER.error("Failed deserializing object to json.", t);
            throw new RuntimeException(t);
        }
    }

    static {
        MAPPER = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        javaTimeModule.addDeserializer(YearMonth.class,
            new YearMonthDeserializer(DateTimeFormatter.ofPattern("yyyy-MM")));
        javaTimeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        javaTimeModule.addSerializer(YearMonth.class,
            new YearMonthSerializer(DateTimeFormatter.ofPattern("yyyy-MM")));
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }



    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtil.class);
}
