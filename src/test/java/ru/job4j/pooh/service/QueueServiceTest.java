package ru.job4j.pooh.service;

import org.junit.Test;
import ru.job4j.pooh.Request;
import ru.job4j.pooh.Response;
import ru.job4j.pooh.servise.QueueService;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class QueueServiceTest {

    @Test
    public void whenPostThenGetQueue() {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        queueService.process(
                new Request("POST", "queue", "weather", paramForPostMethod));
        Response result = queueService.process(
                new Request("GET", "queue", "weather", null));
        assertThat(result.text(), is(paramForPostMethod));
    }

    @Test
    public void whenQueueIsEmpty() {
        QueueService queueService = new QueueService();
        Response result = queueService.process(
                new Request("GET", "queue", "weather", null));
        assertThat(result.text(), is(""));
        assertThat(result.status(), is("204"));
    }

    @Test
    public void when2PostThenGetQueue() {
        QueueService queueService = new QueueService();
        String param1 = "temperature=18";
        String param2 = "wind_speed=5m/s";
        queueService.process(
                new Request("POST", "queue", "weather", param1));
        queueService.process(
                new Request("POST", "queue", "weather", param2));
        Response result1 = queueService.process(
                new Request("GET", "queue", "weather", null));
        Response result2 = queueService.process(
                new Request("GET", "queue", "weather", null));
        assertThat(result1.text(), is(param1));
        assertThat(result2.text(), is(param2));
    }

    @Test
    public void when2PostsIn2DifferentQueuesThenGetThem() {
        QueueService queueService = new QueueService();
        String param1 = "temperature=18";
        String param2 = "Argentina-Jamaica_5:0";
        queueService.process(
                new Request("POST", "queue", "weather", param1));
        queueService.process(
                new Request("POST", "queue", "sport", param2));
        Response result1 = queueService.process(
                new Request("GET", "queue", "weather", null));
        Response result2 = queueService.process(
                new Request("GET", "queue", "sport", null));
        assertThat(result1.text(), is(param1));
        assertThat(result2.text(), is(param2));
    }

    @Test
    public void whenPostThenGetInDifferentThreads() throws InterruptedException {
        QueueService queueService = new QueueService();
        String param = "temperature=18";
        AtomicReference<Response> result = new AtomicReference<>();
        Thread first = new Thread(() -> queueService.process(
                new Request("POST", "queue", "weather", param)));
        Thread second = new Thread(() -> result.set(queueService.process(
                new Request("GET", "queue", "weather", null))));
        first.start();
        first.join();
        second.start();
        second.join();
        assertThat(result.get().text(), is(param));
    }
}