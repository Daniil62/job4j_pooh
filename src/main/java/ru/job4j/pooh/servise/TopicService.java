package ru.job4j.pooh.servise;

import ru.job4j.pooh.Request;
import ru.job4j.pooh.Response;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {

    private Map<String, Queue<Response>> queues = new ConcurrentHashMap<>();

    @Override
    public Response process(Request req) {
        Response result = null;
        if (req != null) {
            if (ServiceNotions.GET.equals(req.httpRequestType())) {
                queues.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
                Queue<Response> queue = queues.get(req.getSourceName());
                result = queue != null && !queue.isEmpty() ? queue.poll()
                        : new Response("", ServiceNotions.FAILED_STATUS);
            } else if (ServiceNotions.POST.equals(req.httpRequestType())) {
                Queue<Response> queue = queues.get(req.getSourceName());
                if (queue != null) {
                    result = new Response(req.getParam(), ServiceNotions.SUCCESSFUL_STATUS);
                    queue.offer(result);
                }
            }
        }
        return result;
    }
}