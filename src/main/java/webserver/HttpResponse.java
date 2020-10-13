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
        if (url.endsWith("css")) { // 요청파일이 css인 경우에만 contentType을 css로 넘김
            contentType = "css";
        }
        log.debug("contentType : {}", contentType);
        // 헤더에 값을 저장하는 코드를 어느 메소드에서 진행해야할지 고민
        // forward() 또는 response200Header
        response200Header(contentType, body.length);
        responseBody(body);
    }

    //TODO: 다른 URL로 리다이렉트하는 메소드

    public void response200Header(String contentType, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            header.put("Content-Type", "text/" + contentType + ";charset=utf-8");
            header.put("Content-Length", String.valueOf(lengthOfBodyContent));
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
}
