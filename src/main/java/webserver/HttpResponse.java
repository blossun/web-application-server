package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    //TODO: 응답 헤더 정보를 Map<String, String>으로 관리
    DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        // 1. Request에서 요청한 Content-Type 값을 가지고 결정
//        String contentType = request.getHeader("Content-Type");
//        contentType = contentType.substring(contentType.indexOf("text/" + 5), contentType.indexOf(";"));
        // 2. 넘겨받은 url의 확장자를 통해서 contentType 결정
//        String contentType = url.substring(url.indexOf(".") + 1); //이렇게 하면 다른 파일 타입에 대해서 오류남
        String contentType = "html";
        if (url.endsWith("css")) { // 요청파일이 css인 경우에만 contentType을 css로 넘김
            contentType = "css";
        }
        log.debug("contentType : {}", contentType);
        response200Header(contentType, body.length);
        responseBody(body);
    }

    //TODO: 다른 URL로 리다이렉트하는 메소드

    public void response200Header(String contentType, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/" + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
