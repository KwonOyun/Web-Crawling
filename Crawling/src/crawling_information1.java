import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore.TrustedCertificateEntry;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class crawling_information1{

	static Document doc;
	static Document url;
	static int contentindex;
	static JSONArray contentarray = new JSONArray();  //내용url을 담는 제이슨 배열 

	public static void main(String[] args){

		TrustManager[] trustAllCerts = new TrustManager[]{    //로그인 뚫기
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}

		// Now you can access an https URL without having the certificate in the truststore
		try {
			URL url = new URL("http://computer.cnu.ac.kr/");
		} catch (MalformedURLException e) {
		}
		Crawling();
	}


	public static void Crawling(){    //크롤링을 실행하는 클래스

		String[] urllist = new String[100];  //url리스트 배열
		try{
			JSONArray informationarray = new JSONArray();
			doc = Jsoup.connect("http://computer.cnu.ac.kr/index.php?mid=notice").get();  //Jsoup라이브러리의 connect함수를 이용해 크롤링한 내용을 doc에 저장
			url = Jsoup.connect("http://computer.cnu.ac.kr/index.php?mid=notice").get();
			String title = doc.title();  //웹페이지 타이틀을 저장
			System.out.println("크롤링한 페이지 : "+title);  //웹페이지 타이틀 출력
			System.out.println();
			Elements topic = doc.select("tbody>tr");   //공지들 한줄 한줄을 선택하여 Elements타입의 topic변수에 저장
			Elements urladdress = url.select(".title>a");
			int index =0;  
			for(Element topics : topic){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("number", index);
				jsonObject.put("text", topics.text());
				informationarray.add(jsonObject);
				index++;
			}
			int i=0;
			for (Element urladdresses : urladdress) {
				urllist[i] = urladdresses.attr("href");   //url을 저장하는 배열 생성
				i++;
			}
			int j =0;
			while(urllist[j] !=null) {
				logincrawling(urllist[j]);
				j++;
			}
			String jsoninfo = informationarray.toJSONString();  //제이슨파일 내용 jsoninfo변수에 저장
			FileWriter file = new FileWriter("information1.json");
			file.write(jsoninfo.toString());
			file.flush();
			file.close();
			String contentjsoninfo = contentarray.toJSONString();  //항목 내용에 관한 제이슨파일 내용을 contentjsoninfo변수에 저장
			FileWriter file1 = new FileWriter("information1content.json");
			file1.write(contentjsoninfo.toString());
			file1.flush();
			file1.close();
		}
		catch(IOException e){  //예외 처리
			e.printStackTrace();
		}
	}


	public static void logincrawling(String crawlingurl) throws IOException {
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";

		Connection.Response loginPageResponse = Jsoup.connect("https://computer.cnu.ac.kr/index.php?act=dispMemberLoginForm")
				.timeout(3000)
				.header("Origin", "http://computer.cnu.ac.kr/")
				.header("Referer", "https://computer.cnu.ac.kr/index.php?act=dispMemberLoginForm")
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Accept-Encoding", "gzip, deflate, br")
				.header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
				.method(Connection.Method.GET)
				.execute();

		//로그인 페이지에서 얻은 쿠키
		Map<String, String> loginTryCookie = loginPageResponse.cookies();

		//로그인 페이지에서 로그인에 함께 전송하는 토큰 얻어내기
		Document loginPageDocument = loginPageResponse.parse();

		Map<String,String> log = new HashMap<>();
		log.put("user_id","u201302362");
		log.put("password","dhtns3709");
		log.put("ruleset","@login");
		log.put("act","procMemberLogin");
		log.put("error_return_url", "/index.php?act=dispMemberLoginForm");
		log.put("mid", "smain");
		log.put("vid", "");
		log.put("success_return_url", "https://computer.cnu.ac.kr/index.php?act=procMemberLogin");
		log.put("xe_validator_id", "modules/member/skins");
		log.put("keep_signed", "Y");

		Connection.Response response = Jsoup.connect("https://computer.cnu.ac.kr/index.php?act=dispMemberLoginForm")
				.userAgent(userAgent)
				.timeout(3000)
				.header("Origin", "http://computer.cnu.ac.kr/")
				.header("Referer", "https://computer.cnu.ac.kr/index.php?act=dispMemberLoginForm")
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Accept-Encoding", "gzip, deflate, br")
				.header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
				.cookies(loginTryCookie)
				.data(log)
				.method(Connection.Method.POST)
				.execute();
		Map<String, String> loginCookie = response.cookies();

		Document adminPageDocument = Jsoup.connect(crawlingurl)   //해당 항목의 내용을 크롤링
				.userAgent(userAgent)
				.header("Referer", "http://computer.cnu.ac.kr")
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Accept-Encoding", "gzip, deflate, sdch")
				.header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
				.cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
				.get();

		//select 내의 option 태그 요소들
		Elements str = adminPageDocument.select("div.rd_hd.clear");
		String contenturl = str.html();  //항목 내용을 나타내는 html

	
		JSONObject contentjsonObject = new JSONObject();  //제이슨 객체 생성
		contentjsonObject.put("number", contentindex);
		contentjsonObject.put("text", contenturl);
		contentarray.add(contentjsonObject);
		contentindex++;
	}
}
