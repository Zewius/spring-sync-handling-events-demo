package ru.zewius.experimental.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private static final long DEFAULT_TIMEOUT = 15000L;

    private final BlockingQueue<ResponseMessage> responseQueue = new LinkedBlockingQueue<>();

    private final ApplicationEventPublisher publisher;

    @Autowired
    public EventService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public ResponseMessage sendEvent(RequestMessage event) {
        return sendEvent(event, DEFAULT_TIMEOUT);
    }

    public ResponseMessage sendEvent(RequestMessage event, long timeout) {
        // Публикуем событие (запрос)
        publisher.publishEvent(event);

        log.info("Request message was published: {}", event);

        try {
            // Ждем ответное событие в течение указанного таймаута
            ResponseMessage responseEvent = responseQueue.poll(timeout, TimeUnit.MILLISECONDS);

            if (responseEvent == null) {
                // Не получили ответ в пределах тайм-аута
                log.warn("Timeout waiting for response event");
                return null;
            }

            return responseEvent;
        } catch (InterruptedException ie) {
            log.error("Error: {}", ie.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @EventListener
    public void receiveEvent(ResponseMessage event) {
        log.info("Response message was read: {}", event);

        // Добавляем полученное событие в очередь ответов
        if (!responseQueue.offer(event)) {
            log.error("Response message was not added to queue");
        }
    }
}
