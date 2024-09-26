package com.example.miniton.notification.controller;

import com.example.miniton.notification.dto.EventPayload;
import com.example.miniton.notification.service.NotificationService;
import com.example.miniton.oauth.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
@Log4j2
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/api/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue =  "") String lastEventId,
            @AuthenticationPrincipal LoginUser loginUser){
        System.out.println("userId " + loginUser.getUserId());
        return notificationService.subscribe(loginUser.getUserId(), lastEventId);
    }

    @PostMapping("/api/broadcast")
    public ResponseEntity<?> broadcast(@RequestBody EventPayload eventPayload){
        notificationService.broadcast(eventPayload);
        return ResponseEntity.ok().build();
    }
}
