package persistence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.entity.Call;
import persistence.repository.CallRepository;

import java.time.LocalDateTime;

@Service
public class CallService {

    private static final Logger logger = LoggerFactory.getLogger(CallService.class);


    @Autowired
    private CallRepository repository;

    public Call save(LocalDateTime startTime) {
        Call call = new Call();
        return repository.save(call);
    }


}
