package ru.job4j.pooh;

import ru.job4j.pooh.servise.QueueService;
import ru.job4j.pooh.servise.Service;
import ru.job4j.pooh.servise.TopicService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PoohServer {

    private final HashMap<String, Service> modes = new HashMap<>();

    private static final String QUEUE = "queue";
    private static final String TOPIC = "topic";
    private static final String PROTOCOL = "HTTP/1.1 ";
    private static final int PORT = 9000;
    private static final int BUFFER_SIZE = 1_000_000;

    public void start() {
        modes.put(QUEUE, new QueueService());
        modes.put(TOPIC, new TopicService());
        ExecutorService pool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (!server.isClosed()) {
                Socket socket = server.accept();
                pool.execute(() -> {
                    try (OutputStream out = socket.getOutputStream();
                         InputStream input = socket.getInputStream()) {
                        byte[] buff = new byte[BUFFER_SIZE];
                        int total = input.read(buff);
                        String content = new String(Arrays.copyOfRange(buff, 0, total), StandardCharsets.UTF_8);
                        Request req = Request.of(content);
                        Response resp = modes.get(req.getPoohMode()).process(req);
                        String ls = System.lineSeparator();
                        out.write((PROTOCOL + resp.status() + ls).getBytes());
                        out.write((resp.text().concat(ls)).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PoohServer().start();
    }
}