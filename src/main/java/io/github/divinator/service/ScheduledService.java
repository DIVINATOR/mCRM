package io.github.divinator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource({"classpath:application.properties"})
public class ScheduledService {
    @Value("${application.scheduler.listen:false}")
    private boolean listen;

    public ScheduledService() {
    }
}