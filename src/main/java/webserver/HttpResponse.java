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

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    Map<String, String> header = new HashMap<>();
    DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        String contentType = "html";
        if (url.endsWith("css")) {
            contentType = "css";
        }
        log.debug("contentType : {}", contentType);
        response200Header(contentType, body.length);
        responseBody(body);
    }

    public void sendRedirect(String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            addHeader("Location", url);
            for (Map.Entry<String, String> entry : header.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                dos.writeBytes(k + ": " + v + "\r\n");
            }
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void response200Header(String contentType, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            addHeader("Content-Type", "text/" + contentType + ";charset=utf-8");
            addHeader("Content-Length", String.valueOf(lengthOfBodyContent));
            for (Map.Entry<String, String> entry : header.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                dos.writeBytes(k + ": " + v + "\r\n");
            }
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

    public void addHeader(String key, String value) {
        header.put(key, value);
    }
}
