# 5.1 HTTP 웹 서버 리팩토링 실습

환경 셋팅

- Maven 프로젝트로 설정
- [Project 환경] Properties > Resource > Text file encoding : `UTF-8` 로 설정
- [Intellij 전역 환경] 환경설정(`cmd + ,`) > General > Workspace > Text file encoding > Default 가 원하는 인코딩 설정이 아니면, Other 로 설정해주면 됨

## 5.1.1 리팩토링할 부분 찾기

[리팩토링: 코드 품질을 개선하는 객체지향 사고법](https://www.notion.so/e95877feb5d74870a25a2ad241c2c5f7)

책에서는 리팩토링이 필요한 시점에 대한 정확한 기준을 제시하기보다 경험적으로 인간의 직관에 맡기고 있다. 리팩토링을 할 때 어떤 기준을 가지고 하기 보다는 직관에 의존해 진행한다.

이런 직관을 키우려면 좋은 코드, 나쁜 코드를 가리지 말고 다른 개발자가 구현한 많은 코드를 읽을 필요가 있다.

다음 단계는 소스코드를 직접 구현해 보는 것이다. (많은 코드를 구현한다고 해서 리팩토링 실력이 늘지 않는다.)

자신이 구현한 코드에 대해 지속적으로 의도적인 리팩토링을 할 때 한 단계 성장할 수 있다.

2장 문자열 계산기를 더 이상 리팩토링할 부분이 없다고 판단될 때까지 극단적으로 연습해 보는 것도 좋은 습관이다.



RequestHandler 클래스의 책임을 분리한다. • RequestHandler 클래스는 많은 책임을 가지고 있다. 객체 지향 설계 원칙 중 “단일 책임의 원칙”에 따라RequestHandler 클래스가 가지고 있는 책임을 찾아 각 책임을 새로운 클래스를 만들어 분리한다.

------

- 힌트
  - 클라이언트 요청 데이터를 담고 있는 InputStream을 생성자로 받아 HTTP 메소드, URL, 헤더, 본문을 분리하는 작업을 한다.
  - 헤더는 Map<String, String>에 저장해 관리하고 getHeader("필드 이름") 메소드를 통해 접근 가능하도록 구현한다.
  - GET과 POST 메소드에 따라 전달되는 인자를 Map<String, String>에 저장해 관리하고 getParameter("인자 이름") 메소드를 통해 접근 가능하도록 구현한다.
  - RequestHandler가 새로 추가한 HttpRequest를 사용하도록 리팩토링한다.

# 테스트 코드 구현

위 요구사항을 구현하기 위해 새로운 클래스를 만들어 구현할 때 테스트 코드 기반으로 개발할 수 있다. `src/test/resources` 디렉토리에 `Http_GET.txt` 라는 이름으로 요청 데이터를 담고 있는 테스트 파일을 추가한다.

※ `Http_GET.txt` 파일의 마지막 라인에 빈 공백 문자열을 포함해야 한다.

이 파일을 이용한 테스트 코드 작성

- Http_GET.txt

```
GET /user/create?userId=solar&password=1234&name=Sunyoung HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
```

- Http_POST.txt

```
GET /user/create HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Content-Length: 46
Content-Type: application/x-www-form-urlencoded
Accept: */*

userId=solar&password=1234&name=Sunyoung
```

- 테스트 코드

```java
public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws FileNotFoundException {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals("GET", request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("solar", request.getParameter("userId"));
    }

		@Test
		public void request_POST() throws FileNotFoundException {
		    InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
		    HttpRequest request = new HttpRequest(in);
		
		    assertEquals("POST", request.getMethod());
		    assertEquals("/user/create", request.getPath());
		    assertEquals("keep-alive", request.getHeader("Connection"));
		    assertEquals("solar", request.getParameter("userId"));
		}
}
```

테스트를 위한 HTTP 요청 데이터를 텍스트 파일에 생성 한 후, FileInputStream으로 읽은 후 이 InputStream을 새로 생성한 HttpRequest 클래스의 생성자로 전달하는 방식으로 테스트할 수 있다.

위에 작성한 테스트 코드를 만족하는 HttpRequest 코드를 구현하면 된다.

테스트 데이터를 위해 파이릉ㄹ 만들고 InputStream을 생성하는 과정이 번거롭게 느껴지면 String으로 문자열을 전달해 테스트할 수 있는 방법을 고민해보자