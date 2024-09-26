package com.example.miniton.notification.infrastructure;

import com.example.miniton.notification.domain.ClockHolder;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class SystemClockHolder implements ClockHolder {

    @Override
    public long mills() {
        return Clock.systemUTC().millis();
    }
}
