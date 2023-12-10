package ru.zewius.experimental.demo.event;

/**
 * A synchronous Spring event handler using the Request-Response model
 * @param <RQ> request message
 * @param <RS> response message
 */
public interface EventService<RQ, RS> {

    /**
     * Default response timeout
     *
     * @return timeout in ms
     */
    default long getTimeout() {
        return 15000L;
    }

    /**
     * Sending request to Spring Event
     *
     * @param request request message
     * @return response message
     */
    default RS sendEvent(RQ request) {
        return sendEvent(request, getTimeout());
    }

    /**
     * Sending request to Spring Event
     *
     * @param request request message
     * @param timeout timeout in ms
     * @return response message
     */
    RS sendEvent(RQ request, long timeout);

}
