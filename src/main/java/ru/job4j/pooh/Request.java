package ru.job4j.pooh;

public class Request {

    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String QUEUE = "queue";
    private static final String TOPIC = "topic";
    private static final String ERROR_MESSAGE = "Invalid content";

    public Request(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Request of(String content) {
        validate(content);
        String[] parsedContent = parse(content);
        return new Request(parsedContent[0], parsedContent[1], parsedContent[2], parsedContent[3]);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }

    private static String[] parse(String content) {
        String[] result = new String[4];
        String[] lines = content.split(System.lineSeparator());
        String[] firstLine = lines[0].split(" ");
        String[] modeAndNameBlock = firstLine[1].split("/");
        String type = firstLine[0];
        String mode = modeAndNameBlock[1];
        result[0] = type;
        result[1] = mode;
        result[2] = modeAndNameBlock[2];
        result[3] = GET.equals(type) && QUEUE.equals(mode) ? ""
                : GET.equals(type) && TOPIC.equals(mode) ? modeAndNameBlock[3]
                : POST.equals(type) ? lines[lines.length - 1] : modeAndNameBlock[2];
        return result;
    }

    private static void validate(String content) {
        if (content == null
                || !((content.startsWith(GET) || content.startsWith(POST))
                        && (content.contains(QUEUE) || content.contains(TOPIC)))) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
    }
}