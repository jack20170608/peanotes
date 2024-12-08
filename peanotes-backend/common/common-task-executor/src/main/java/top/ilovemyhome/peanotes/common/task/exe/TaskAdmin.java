package top.ilovemyhome.peanotes.common.task.exe;

import jakarta.ws.rs.core.MediaType;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.http.HttpUtils;
import top.ilovemyhome.peanotes.common.task.exe.domain.HandleCallbackParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.RegistryParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.TaskResponse;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public interface TaskAdmin {

    String CALL_BACK_URI = "task/api/callback";
    String REGISTER_URI = "task/api/register";
    String UNREGISTER_URI = "task/api/unregister";

    String getAdminServerurl();

    default String getAccessToken(){
        return null;
    }

    TaskResponse callback(List<HandleCallbackParam> callbackParamList);

    TaskResponse register(RegistryParam registryParam);

    TaskResponse unRegister(RegistryParam registryParam);

    static DefaultBuilder builder() {
        return new DefaultBuilder();
    }

    class DefaultBuilder {
        private String adminServerUrl;
        private String accessToken;

        public DefaultBuilder withAdminServerUrl(String adminServerUrl) {
            this.adminServerUrl = adminServerUrl;
            return this;
        }

        public DefaultBuilder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        private DefaultBuilder() {
        }

        public TaskAdmin build() {
            return new TaskAdminImpl(this.adminServerUrl, accessToken);
        }
    }
}

class TaskAdminImpl implements TaskAdmin {

    TaskAdminImpl(String adminServerUrl, String accessToken) {
        this.adminServerUrl = adminServerUrl;
        this.accessToken = accessToken;
        this.baseUri = URI.create(adminServerUrl);
    }

    @Override
    public String getAdminServerurl() {
        return this.adminServerUrl;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public TaskResponse callback(List<HandleCallbackParam> callbackParamList) {
        URI uri = baseUri.resolve(CALL_BACK_URI);
        String jsonBody = JacksonUtil.toJson(callbackParamList);
        HttpResponse<String> response = HttpUtils.post(uri, timeout
            , Map.of("Content-Type", MediaType.APPLICATION_JSON, "Accept", MediaType.APPLICATION_JSON)
            , jsonBody);
        return responseProcess(response);
    }

    @Override
    public TaskResponse register(RegistryParam registryParam) {
        URI uri = baseUri.resolve(REGISTER_URI);
        String jsonBody = JacksonUtil.toJson(registryParam);
        HttpResponse<String> response = HttpUtils.post(uri, timeout
            , Map.of("Content-Type", MediaType.APPLICATION_JSON, "Accept", MediaType.APPLICATION_JSON)
            , jsonBody);
        return responseProcess(response);
    }

    @Override
    public TaskResponse unRegister(RegistryParam registryParam) {
        URI uri = baseUri.resolve(UNREGISTER_URI);
        String jsonBody = JacksonUtil.toJson(registryParam);
        HttpResponse<String> response =  HttpUtils.post(uri, timeout
            , Map.of("Content-Type", MediaType.APPLICATION_JSON, "Accept", MediaType.APPLICATION_JSON)
            , jsonBody);
        return responseProcess(response);
    }

    private TaskResponse responseProcess(HttpResponse<String> response){
        int statusCode = response.statusCode();
        TaskResponse result = null;
        if (statusCode == 200) {
            result = JacksonUtil.fromJson(response.body(), TaskResponse.class);
        }else if (statusCode >= 400 && statusCode < 500) {
            result = TaskResponse.of(statusCode, "Client Error" , response.body());
        }else if (statusCode >= 500){
            result = TaskResponse.of(statusCode, "Server Error" , response.body());
        }
        return result;
    }

    @Override
    public String toString() {
        return "TaskAdminImpl{" +
            "adminServerUrl='" + adminServerUrl + '\'' +
            '}';
    }

    private transient final URI baseUri;
    private final String adminServerUrl;
    private final String accessToken;
    private final int timeout = 5;

}
