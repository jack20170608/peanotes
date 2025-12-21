package top.ilovemyhome.commons.database.example;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import top.ilovemyhome.commons.database.SharedResources;
import top.ilovemyhome.commons.database.flyway.FlyWayHelper;
import top.ilovemyhome.commons.database.pool.HikariDataSourceFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Disabled
public class End2EndTesting {


    @Test
    public void testFlyway() {
        HikariDataSourceFactory instance = HikariDataSourceFactory.getInstance(SharedResources.config);

        FlyWayHelper.run(SharedResources.config);

        instance.getJdbi().useTransaction(handle -> {
            String result = handle.createQuery("select version()").mapTo(String.class).one();
            System.out.println(result);
            assertThat(result).isNotEmpty();
            handle.createQuery("select * from test").mapToMap().list().forEach(System.out::println);
        });
    }
}
