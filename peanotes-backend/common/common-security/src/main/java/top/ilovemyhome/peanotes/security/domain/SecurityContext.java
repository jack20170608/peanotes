package top.ilovemyhome.peanotes.security.domain;

import io.muserver.MuRequest;
import io.muserver.MuResponse;

public interface SecurityContext {

    MuRequest getRequest();

    MuResponse getResponse();


}
