package ru.zewius.experimental.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zewius.experimental.demo.event.impl.CompletableFutureEventService;
import ru.zewius.experimental.demo.event.impl.dto.Message;
import ru.zewius.experimental.demo.event.impl.dto.RequestMessage;
import ru.zewius.experimental.demo.event.impl.dto.ResponseMessage;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping
public class EventController {

    private final ApplicationEventPublisher publisher;
    private final CompletableFutureEventService eventService;

    @Autowired
    public EventController(ApplicationEventPublisher publisher,
                           CompletableFutureEventService eventService) {
        this.publisher = publisher;
        this.eventService = eventService;
    }

    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody RequestMessage message) {
        Message responseMessage = eventService.sendEvent(message);

        Map<String, Object> result = new HashMap<>();

        boolean responseReceived = responseMessage != null;

        result.put("result", responseReceived ? "done" : "empty");
        if (responseReceived) {
            result.put("message", responseMessage);
        }

        return result;
    }

    @PostMapping("/send_response")
    public Map<String, String> sendEvent(@RequestBody ResponseMessage message) {
        publisher.publishEvent(message);
        return Map.of("result", "done");
    }
}
