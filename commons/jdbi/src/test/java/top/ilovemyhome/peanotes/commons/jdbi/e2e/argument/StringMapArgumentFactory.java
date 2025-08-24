package top.ilovemyhome.peanotes.commons.jdbi.e2e.argument;

import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import top.ilovemyhome.peanotes.commons.jdbi.utils.JacksonUtil;

import java.sql.Types;
import java.util.Map;

public class StringMapArgumentFactory extends AbstractArgumentFactory<Map<String,String>> {

    public StringMapArgumentFactory() {
        super(Types.VARCHAR);
    }

    @Override
    protected Argument build(Map<String, String> value, ConfigRegistry config) {
        String jsonVal = JacksonUtil.toJson(value);
        return (position, statement, ctx) -> statement.setString(position, jsonVal);
    }
}
