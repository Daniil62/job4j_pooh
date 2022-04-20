package ru.job4j.pooh.servise;

import ru.job4j.pooh.Request;
import ru.job4j.pooh.Response;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {

    private Map<String, Queue<Response>> queues = new ConcurrentHashMap<>();

    @Override
    public Response process(Request req) {
        Response result = null;
        if (req != null) {
            Queue<Response> queue = queues.get(req.getSourceName());
            if (ServiceNotions.GET.equals(req.httpRequestType())) {
                result = queue != null && !queue.isEmpty() ? queue.poll()
                        : new Response("", ServiceNotions.FAILED_STATUS);
            } else if (ServiceNotions.POST.equals(req.httpRequestType())) {
                if (queue != null) {
                    result = new Response(req.getParam(), ServiceNotions.SUCCESSFUL_STATUS);
                    queue.offer(result);
                } else {
                    queues.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
                    queues.get(req.getSourceName())
                            .offer(new Response(req.getParam(), ServiceNotions.SUCCESSFUL_STATUS));
                }
            }
        }
        return result;
    }

}