package ru.job4j.pooh.service;

import org.junit.Test;
import ru.job4j.pooh.Request;
import ru.job4j.pooh.Response;
import ru.job4j.pooh.servise.TopicService;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TopicServiceTest {

    @Test
    public void whenTopic() {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        topicService.process(
                new Request("GET", "topic", "weather", paramForSubscriber1)
        );
        topicService.process(
                new Request("POST", "topic", "weather", paramForPublisher)
        );
        Response result1 = topicService.process(
                new Request("GET", "topic", "weather", paramForSubscriber1)
        );
        Response result2 = topicService.process(
                new Request("GET", "topic", "weather", paramForSubscriber2)
        );
        assertThat(result1.text(), is("temperature=18"));
        assertThat(result2.text(), is(""));
    }

    @Test
    public void whenPostThenGetInDifferentThreads() throws InterruptedException {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForSubscriber = "client407";
        AtomicReference<Response> result = new AtomicReference<>();
        topicService.process(
                new Request("GET", "topic", "weather", paramForSubscriber)
        );
        Thread first = new Thread(() -> topicService.process(
                new Request("POST", "topic", "weather", paramForPublisher)
        ));
        Thread second = new Thread(() -> result.set(topicService.process(
                new Request("GET", "topic", "weather", paramForSubscriber))));
        first.start();
        first.join();
        second.start();
        second.join();
        assertThat(result.get().text(), is("temperature=18"));
    }
}