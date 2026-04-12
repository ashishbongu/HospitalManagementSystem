package notification;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SmsNotifier {
	private static final String ACCOUNT_SID_ENV = "TWILIO_ACCOUNT_SID";
	private static final String AUTH_TOKEN_ENV = "TWILIO_AUTH_TOKEN";
	private static final String FROM_NUMBER_ENV = "TWILIO_FROM_NUMBER";

	private final HttpClient httpClient = HttpClient.newHttpClient();

	public boolean isConfigured() {
		return hasText(System.getenv(ACCOUNT_SID_ENV)) && hasText(System.getenv(AUTH_TOKEN_ENV)) && hasText(System.getenv(FROM_NUMBER_ENV));
	}

	public void sendAppointmentDecision(String patientName, String patientPhone, String doctorName, String date, String time, String decision) {
		if (!isConfigured()) {
			System.err.println("SMS not sent. Configure " + ACCOUNT_SID_ENV + ", " + AUTH_TOKEN_ENV + ", and " + FROM_NUMBER_ENV + ".");
			return;
		}

		String message = "Hello " + clean(patientName) + ", your appointment with " + clean(doctorName)
				+ " on " + clean(date) + " at " + clean(time) + " is " + clean(decision) + ".";
		send(patientPhone, message);
	}

	private void send(String toNumber, String message) {
		String accountSid = System.getenv(ACCOUNT_SID_ENV).trim();
		String authToken = System.getenv(AUTH_TOKEN_ENV).trim();
		String fromNumber = System.getenv(FROM_NUMBER_ENV).trim();
		String body = "To=" + encode(toNumber) + "&From=" + encode(fromNumber) + "&Body=" + encode(message);
		String auth = Base64.getEncoder().encodeToString((accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json"))
				.header("Authorization", "Basic " + auth)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();

		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() < 200 || response.statusCode() >= 300) {
				System.err.println("SMS failed with status " + response.statusCode() + ": " + response.body());
			}
		} catch (IOException e) {
			System.err.println("SMS failed: " + e.getMessage());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("SMS interrupted: " + e.getMessage());
		}
	}

	private static String encode(String value) {
		return URLEncoder.encode(value == null ? "" : value.trim(), StandardCharsets.UTF_8);
	}

	private static boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}

	private static String clean(String value) {
		return value == null ? "" : value.trim();
	}
}
