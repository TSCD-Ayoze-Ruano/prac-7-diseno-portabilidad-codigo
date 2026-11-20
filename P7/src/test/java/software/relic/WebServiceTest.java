package software.relic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.relic.adapters.fake.FakeDatalake;
import software.relic.domain.WebService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WebServiceTest {

	private FakeDatalake datalake;
	private WebService webService;

	private final String testId = "12345678";
	private final String testContent = "12345678-sale-001\t2026-11-01\tSALE\tamount=100";

	@BeforeEach
	void setUp() {
		datalake = new FakeDatalake();
		datalake.put(testId, testContent);

		webService = new WebService(datalake, 0);
	}

	@AfterEach
	void tearDown() {
		if (webService != null) {
			webService.close();
		}
	}

	@Test
	void testGetFromFakeDatalakeAndWebService() throws Exception {
		// 1. Llamada get directa al FakeDatalake definido en test
		String directData = datalake.get(testId);
		assertNotNull(directData);
		assertEquals(testContent, directData);

		// 2. Llamada HTTP al WebService que utiliza el FakeDatalake por debajo
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + webService.port() + "/sales/" + testId))
				.GET()
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(200, response.statusCode());
		assertEquals(testContent, response.body());
	}
}
