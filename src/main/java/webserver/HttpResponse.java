package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    Map<String, String> headers = new HashMap<>();
    DataOutputStream dos = null;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        if (url.endsWith(".css")) {
            addHeader("Content-Type", "text/css");
        } else if (url.endsWith(".js")) {
            addHeader("Content-Type", "text/javascript");
        } else {
            addHeader("Content-Type", "text/html;charset=utf-8");
        }
        addHeader("Content-Length", body.length + "");
        response200Header();
        responseBody(body);
    }

    public void forwardBody(String body) {
        byte[] contents = body.getBytes();
        addHeader("Content-Type", "text/html;charset=utf-8");
        addHeader("Content-Length", contents.length + "");
        response200Header();
        responseBody(contents);
    }

    public void sendRedirect(String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            addHeader("Location", url);
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void response200Header() {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processHeaders() {
        try {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                dos.writeBytes(key + ": " + headers.get(key) + " \r\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
}
