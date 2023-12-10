package ru.zewius.experimental.demo.event.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import ru.zewius.experimental.demo.event.EventService;
import ru.zewius.experimental.demo.event.impl.dto.RequestMessage;
import ru.zewius.experimental.demo.event.impl.dto.ResponseMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @deprecated due to the lack of requests identification, the first hit response will be processed, which is an error.
 */
@Slf4j
@Deprecated(forRemoval = true)
public class BlockingQueueEventService implements EventService<RequestMessage, ResponseMessage> {

    private final BlockingQueue<ResponseMessage> responseQueue = new LinkedBlockingQueue<>();

    private final ApplicationEventPublisher publisher;

    public BlockingQueueEventService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
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
    private void receiveEvent(ResponseMessage event) {
        log.info("Response message was read: {}", event);

        // Добавляем полученное событие в очередь ответов
        if (!responseQueue.offer(event)) {
            log.error("Response message was not added to queue");
        }
    }
}
