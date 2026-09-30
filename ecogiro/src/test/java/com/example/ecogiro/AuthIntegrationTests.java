package com.example.ecogiro;
import java.net.*;
import java.net.http.*;
import java.util.regex.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTests {
 @Value("${local.server.port}") int port;
 HttpClient client=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();
 URI uri(String path){return URI.create("http://localhost:"+port+path);}
 HttpResponse<String> get(String path) throws Exception{return client.send(HttpRequest.newBuilder(uri(path)).GET().build(),HttpResponse.BodyHandlers.ofString());}
 HttpResponse<String> post(String path,String body,String type,boolean csrf) throws Exception {
  var request=HttpRequest.newBuilder(uri(path)).header("Content-Type",type);
  if(csrf){var matcher=Pattern.compile("\\\"token\\\":\\\"([^\\\"]+)\\\"").matcher(get("/api/auth/csrf").body());assertTrue(matcher.find());request.header("X-CSRF-TOKEN",matcher.group(1));}
  return client.send(request.POST(HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
 }
 @Test void registrationLoginAndLogout() throws Exception {
  assertEquals(401,get("/api/auth/me").statusCode());
  String registration="{\"name\":\"Arthur\",\"email\":\"arthur@example.com\",\"password\":\"Senha12345\"}";
  assertEquals(403,post("/api/auth/register",registration,"application/json",false).statusCode());
  assertEquals(201,post("/api/auth/register",registration,"application/json",true).statusCode());
  assertEquals(409,post("/api/auth/register",registration,"application/json",true).statusCode());
  assertEquals(401,post("/api/auth/login","username=arthur%40example.com&password=errada","application/x-www-form-urlencoded",true).statusCode());
  assertEquals(200,post("/api/auth/login","username=arthur%40example.com&password=Senha12345","application/x-www-form-urlencoded",true).statusCode());
  var me=get("/api/auth/me");assertEquals(200,me.statusCode());assertTrue(me.body().contains("Arthur"));assertFalse(me.body().contains("password"));
  assertEquals(204,post("/api/auth/logout","","application/x-www-form-urlencoded",true).statusCode());
  assertEquals(401,get("/api/auth/me").statusCode());
 }
}
