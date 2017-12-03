package mobispring;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import exception.StationNotFoundException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Geofox {

	private String GEOFOX_API_BASE_URL;
	private String GEOFOX_API_USER;
	private String GEOFOX_API_PASSWORD;

	public Geofox() {
		Map<String, String> env = System.getenv();
		GEOFOX_API_USER = env.get("GEOFOX_API_USER");
		GEOFOX_API_PASSWORD = env.get("GEOFOX_API_PASSWORD");
		GEOFOX_API_BASE_URL = env.get("GEOFOX_API_URL");

		try {
			System.out.println(this.checkName("Saarlandstrasse", "STATION").toString(4));
			System.out.println(this.departureListNow("Saarlandstrasse").toString(4));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (StationNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JSONObject departureListNow(String station) throws JSONException, StationNotFoundException {

		// Validate otherwise throw Exception
		JSONObject response = this.checkName(station, "STATION");
		if (!response.getString("returnCode").equals("OK")) {
			throw new StationNotFoundException("Die Station '" + station + "' could not be found.");
		}

		String stationId = response.getJSONArray("results").getJSONObject(0).getString("id");
		String geofox_url = GEOFOX_API_BASE_URL + "departureList";
		System.out.println(stationId);

		JSONObject requestBody = new JSONObject();

		// Create TheName object
		JSONObject theName = new JSONObject();
		theName.put("name", station);
		theName.put("type", "STATION");
		theName.put("id", stationId);

		// Create GTITime object.
		DateFormat dateFormatDate = new SimpleDateFormat("dd.MM.yyyy");
		DateFormat dateFormatTime = new SimpleDateFormat("HH:mm");
		Date date = new Date();

		JSONObject time = new JSONObject();
		time.put("date", dateFormatDate.format(date));
		time.put("time", dateFormatTime.format(date));

		// Create RequestBody object.
		requestBody.put("time", time);
		requestBody.put("station", theName);
		requestBody.put("maxList", 32);
		requestBody.put("useRealtime", "true");
		Request request = buildRequest(geofox_url, requestBody);
		return sendRequest(request);
	}

	public JSONObject checkName(String station, String type) throws JSONException {
		String geofox_url = GEOFOX_API_BASE_URL + "checkName";

		// the name
		JSONObject theName = new JSONObject();
		theName.put("name", station);
		theName.put("type", type);

		// request json
		JSONObject requestJson = new JSONObject();
		requestJson.put("coordinateType", "EPSG_4326");
		requestJson.put("maxList", 1);
		requestJson.put("version", 31);
		requestJson.put("theName", theName);

		Request request = buildRequest(geofox_url, requestJson);

		return sendRequest(request);
	}

	public void getRouteTest() throws JSONException {
		String geofox_url = GEOFOX_API_BASE_URL + "getRoute";

		JSONObject requestJson = new JSONObject();
		requestJson.put("language", "de");
		requestJson.put("version", 27);
		requestJson.put("start", createHauptbahnhofJSON());
		requestJson.put("dest", createBarmbekJSON());
		requestJson.put("time", createTimeJSON());
		requestJson.put("timeIsDeparture", true);
		requestJson.put("numberOfSchedules", 1);

		Request request = buildRequest(geofox_url, requestJson);
		sendRequest(request);
	}

	public static String makeSignature(String password, byte[] requestBody) {
		final Charset passwordEncoding = Charset.forName("UTF-8");
		final String algorithm = "HmacSHA1";

		byte[] key = password.getBytes(passwordEncoding);
		SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
		Mac mac = null;
		try {
			mac = Mac.getInstance(algorithm);
			mac.init(keySpec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		byte[] signature = mac.doFinal(requestBody);

		return DatatypeConverter.printBase64Binary(signature);
	}

	public Request buildRequest(String api_endpoint, JSONObject requestJson) {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, requestJson.toString());

		// passwort hash
		String userSignature = makeSignature(GEOFOX_API_PASSWORD, requestJson.toString().getBytes());

		return new Request.Builder().url(api_endpoint).post(body).addHeader("Accept", "application/json")
				.addHeader("geofox-auth-type", "HmacSha1").addHeader("Content-Type", "application/json")
				.addHeader("Geofox-Auth-User", GEOFOX_API_USER).addHeader("geofox-auth-signature", userSignature)
				.build();
	}

	public JSONObject sendRequest(Request request) throws JSONException {
		JSONObject outjson = null;
		try {
			OkHttpClient client = new OkHttpClient();
			Response response;
			response = client.newCall(request).execute();
			JSONObject responseJson = new JSONObject(response.body().string());
			outjson = responseJson;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return outjson;
	}

	public JSONObject createHauptbahnhofJSON() throws JSONException {
		return new JSONObject().put("id", "Master:9910910").put("type", "STATION");
	}

	public JSONObject createBarmbekJSON() throws JSONException {
		return new JSONObject().put("id", "Master:70950").put("type", "STATION");
	}

	public JSONObject createTimeJSON() throws JSONException {
		return new JSONObject().put("date", "23.11.2017").put("time", "15:00");
	}
}