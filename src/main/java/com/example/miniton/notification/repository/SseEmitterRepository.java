package com.example.miniton.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SseEmitterRepository implements EmitterRepository{


    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> events = new ConcurrentHashMap<>();
    @Override
    public SseEmitter save(String eventId, SseEmitter sseEmitter) {
        emitters.put(eventId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEvent(String eventId, Object event) {
        events.put(eventId, event);
    }

    @Override
    public Map<String, SseEmitter> findAllStartWithUserId(Long userId) {
        return emitters.entrySet().stream()
                .filter(e -> e.getKey().startsWith(userId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventStartWithUserId(Long userId) {
        return events.entrySet().stream()
                .filter(e -> e.getKey().startsWith(userId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteByEventId(String eventId) {
        emitters.remove(eventId);
    }

    @Override
    public void deleteAllStartWithUserId(Long userId) {
        emitters.forEach((k,v) ->{
            if(k.startsWith(userId.toString())) events.remove(k);
        });
    }

    @Override
    public void deleteAllEventStartWithUserId(Long userId) {
        events.forEach((k,v) ->{
            if(k.startsWith(userId.toString())) events.remove(k);
        });
    }

    @Override
    public Map<String, SseEmitter> findAll() {
        return emitters;
    }
}
