package top.ilovemyhome.peanotes.autotest.service;

import top.ilovemyhome.peanotes.autotest.domain.KarateRequest;

import java.util.List;
import java.util.Optional;

public interface KarateRequestCrudService {

    List<KarateRequest> getByIds(List<Long> ids);

    Optional<KarateRequest> getById(Long id);

    int create(KarateRequest karateRequest);

    int update(Long id, KarateRequest karateRequest);


}
