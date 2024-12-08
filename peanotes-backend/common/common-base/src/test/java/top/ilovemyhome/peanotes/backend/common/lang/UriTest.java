package top.ilovemyhome.peanotes.backend.common.lang;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UriTest {

    @Test
    public void testUriCreate() throws Exception{
        URI baseUri = new URI("https://localhost:9090/task");
        assertThat(baseUri.getPath()).isEqualTo("/task");
        String relativePath = "api/v1/get";
        assertThat(baseUri.resolve(baseUri.getPath()).resolve(relativePath).toString()).isEqualTo("https://localhost:9090/api/v1/get");


    }
}
