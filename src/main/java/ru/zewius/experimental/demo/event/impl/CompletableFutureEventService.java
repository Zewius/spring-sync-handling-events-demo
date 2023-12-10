package ru.zewius.experimental.demo.event.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.zewius.experimental.demo.event.EventService;
import ru.zewius.experimental.demo.event.impl.dto.RequestMessage;
import ru.zewius.experimental.demo.event.impl.dto.ResponseMessage;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
public class CompletableFutureEventService implements EventService<RequestMessage, ResponseMessage> {

    private final Map<String, CompletableFuture<ResponseMessage>> responses = new ConcurrentHashMap<>();

    private final ApplicationEventPublisher publisher;

    @Autowired
    public CompletableFutureEventService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public ResponseMessage sendEvent(final RequestMessage request, long timeout) {
        // Получаем идентификатор события-запроса
        String requestId = request.getId();

        if (requestId == null) {
            log.error("Request message has no ID");
            throw new IllegalArgumentException("Request message has no ID");
        }

        // Создаём CompletableFuture для события-ответа
        CompletableFuture<ResponseMessage> futureResponse = new CompletableFuture<>();

        // Добавляем CompletableFuture в карту по идентификатору события-запроса
        responses.put(requestId, futureResponse);

        // Публикуем событие-запрос
        publisher.publishEvent(request);

        log.info("Request message {} was published", request.getId());

        try {
            // Ожидаем событие-ответ в течение таймаута
            return futureResponse.get(timeout, TimeUnit.MILLISECONDS);
        }
        catch (ExecutionException | TimeoutException ignored) {
            return null;
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return null;
        }
        finally {
            // Удаляем CompletableFuture из Map
            responses.remove(requestId);
        }
    }

    @EventListener
    private void receiveEvent(final ResponseMessage response) {
        log.info("Response message {} was read", response.getId());

        // Получаем идентификатор события-ответа
        String id = response.getId();

        // Ищем CompletableFuture в карте по идентификатору события-ответа
        CompletableFuture<ResponseMessage> future = responses.get(id);

        if (future != null) {
            // Завершаем CompletableFuture событием-ответом
            future.complete(response);
        }
    }
}
