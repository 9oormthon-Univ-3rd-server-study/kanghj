package com.example.miniton.notification.service;


import com.example.miniton.notification.domain.ClockHolder;
import com.example.miniton.notification.dto.EventPayload;
import com.example.miniton.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Log4j2
public class NotificationService {

    private final EmitterRepository emitterRepository;
    private final ClockHolder clockHolder;
    private static final long TIMEOUT = 60*1000L*10;
    private static final long RECONNECTION_TIMEOUT = 1000L;

    public SseEmitter subscribe(Long userId, String lastEventId){
        // 매 연결마다 고유 이벤트 id 부여
        String eventId = generateEventId(userId);
        // SseEmitter 인스턴스 생성 후 Map에 저장
        SseEmitter sseEmitter = emitterRepository.save(eventId, new SseEmitter(TIMEOUT));
        // 연결에서 발생할 수 있는 다양한 상황을 처리
        registerEmitterHandler(eventId,sseEmitter);
        // 첫 연결 시 503 Service Unavailable 방지용 더미 Event 전송
        sendToClient(eventId, sseEmitter, "알림 구독 성공 [userId] = " + userId); // 첫 연결 후 아무 데이터가 보내지지않은채, 재연결을 시도하면 503 에러가 발생한다고 한다.
        // 클라이언트가 미수신한 Event 목록이 존재할 경우 모두 전송
        recoverData(userId, lastEventId, sseEmitter);
        return sseEmitter;
    }

    public void broadcast(EventPayload eventPayload){
        Map<String, SseEmitter> emitters = emitterRepository.findAll();
        emitters.forEach((k, v) ->{
            try {
                v.send(SseEmitter.event()
                        .name("broadcast event")
                        .id("broadcast event 1")
                        .reconnectTime(RECONNECTION_TIMEOUT)
                        .data(eventPayload, MediaType.APPLICATION_JSON));
                log.info("sent notification, id={}, payload={}", k, eventPayload);

            } catch (IOException e){
                log.error("fail to send emiiter id = {}, {}", k,e.getMessage());
            }
        });
    }

    private void recoverData(Long userId, String lastEventId, SseEmitter sseEmitter){
        if(Objects.nonNull(lastEventId)){
            Map<String, Object> events = emitterRepository.findAllEventStartWithUserId(userId);
            events.entrySet().stream()
                    .filter(e -> lastEventId.compareTo(e.getKey()) < 0)
                    .forEach(e -> sendToClient(e.getKey(), sseEmitter, e.getValue()));
        }
    }

    private void sendToClient(String eventId, SseEmitter sseEmitter, Object data){
        SseEmitter.SseEventBuilder event = getSseEvent(eventId, data);
        try {
            sseEmitter.send(event);
        }catch (IOException e){
            log.error("구독 실패, eventId ={}, {}", eventId, e.getMessage());
        }
    }

    private void registerEmitterHandler(String eventId, SseEmitter sseEmitter){
        // 이벤트 전송시
        sseEmitter.onCompletion(()-> {
            log.info("연결이 끝났습니다. : eventId = {}",eventId);
            emitterRepository.deleteByEventId(eventId);
        });

        // 이벤트 스트림 연결 끊길 시
        sseEmitter.onTimeout(()-> {
            log.info("Timeout이 발생했습니다. : eventId={}",eventId);
            emitterRepository.deleteByEventId(eventId);
        });

        // 에러 발생시
        sseEmitter.onError((e) ->{
            log.info("에러가 발생했습니다. error={}, eventId={}", e.getMessage(),eventId);
            emitterRepository.deleteByEventId(eventId);
        });
    }

    private SseEmitter.SseEventBuilder getSseEvent(String eventId, Object data){
        return SseEmitter.event()
                .id(eventId)
                .data(data)
                .reconnectTime(RECONNECTION_TIMEOUT);
    }

    private String generateEventId(Long userId){
        return userId + "_"+ clockHolder.mills();
    }
}
