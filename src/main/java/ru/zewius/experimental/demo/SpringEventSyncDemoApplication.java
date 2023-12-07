package ru.zewius.experimental.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping()
@SpringBootApplication
public class SpringEventSyncDemoApplication {

    private static final Logger log = LoggerFactory.getLogger(SpringEventSyncDemoApplication.class);

    private final ApplicationEventPublisher publisher;
    private final EventService eventService;

    @Autowired
    public SpringEventSyncDemoApplication(ApplicationEventPublisher publisher, EventService eventService) {
        this.publisher = publisher;
        this.eventService = eventService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringEventSyncDemoApplication.class, args);
    }

    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody RequestMessage message) {
        Message responseMessage = eventService.sendEvent(message);
        log.info("Response received - {}", responseMessage);

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
