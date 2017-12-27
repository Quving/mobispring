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

import org.apache.commons.lang3.time.DateUtils;
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

	}

	public static void main(String[] args) {
		Geofox geofox = new Geofox();

		try {
			System.out.println(geofox.departureListNow("Farmsen", 10, 10).toString(4));
		} catch (StationNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Welcome JSON.
	 * @return
	 */
	public JSONObject welcome() {
		JSONObject welcome = new JSONObject();
		welcome.put("status", "online");
		welcome.put("name", "Mobispring");
		return welcome;
	}
	/**
	 * Returns the ID of a given station if the station exists.
	 * 
	 * @param station
	 * @return
	 * @throws StationNotFoundException
	 */
	private String getStationId(String station) throws StationNotFoundException {

		// Validate otherwise throw Exception
		JSONObject response = this.checkName(station, "STATION");
		if (!response.getString("returnCode").equals("OK"))
			throw new StationNotFoundException("Die Station '" + station + "' could not be found.");
		else {
			String stationId = response.getJSONArray("results").getJSONObject(0).getString("id");
			return stationId;
		}
	}

	/**
	 * Returns a departure-list of a given station for current time and timeOffset
	 * (minutes)
	 * 
	 * @param station
	 * @param timeOffset
	 * @param maxList
	 * @return
	 * @throws StationNotFoundException
	 */
	public JSONObject departureListNow(String station, int timeOffset, int maxList) throws StationNotFoundException {
		// Add timeOffset to current time. Create GTITime object.
		DateFormat dateFormatDate = new SimpleDateFormat("dd.MM.yyyy");
		DateFormat dateFormatTime = new SimpleDateFormat("HH:mm");
		Date targetTime = new Date(); // now
		targetTime = DateUtils.addMinutes(targetTime, timeOffset);

		return this.departureList(station, dateFormatDate.format(targetTime), dateFormatTime.format(targetTime),
				maxList);
	}

	/**
	 * Returns a departureList of a given station.
	 * 
	 * @param station
	 * @param ddMMyyyy
	 * @param HHmm
	 * @param maxList
	 * @return
	 * @throws StationNotFoundException
	 */
	public JSONObject departureList(String station, String ddMMyyyy, String HHmm, int maxList) {
		String geofox_url = GEOFOX_API_BASE_URL + "departureList";
		String stationId = "";
		try {
			stationId = getStationId(station);
		} catch (StationNotFoundException e) {
			e.printStackTrace();
		}

		// Create TheName object
		JSONObject theName = new JSONObject();
		theName.put("name", station);
		theName.put("type", "STATION");
		theName.put("id", stationId);

		JSONObject time = new JSONObject();
		time.put("date", ddMMyyyy);
		time.put("time", HHmm);
		// Create RequestBody object.
		JSONObject requestBody = new JSONObject();
		requestBody.put("time", time);
		requestBody.put("station", theName);
		requestBody.put("maxList", 32);
		requestBody.put("maxTimeOffset", 60);
		requestBody.put("useRealtime", "true");
		Request request = buildRequest(geofox_url, requestBody);
		return sendRequest(request);
	}

	private JSONObject checkName(String station, String type) throws JSONException {
		String geofox_url = GEOFOX_API_BASE_URL + "checkName";

		// the name
		JSONObject theName = new JSONObject();
		theName.put("name", station);
		theName.put("type", type);

		// Request Json
		JSONObject requestJson = new JSONObject();
		requestJson.put("coordinateType", "EPSG_4326");
		requestJson.put("maxList", 1);
		requestJson.put("version", 31);
		requestJson.put("theName", theName);

		Request request = buildRequest(geofox_url, requestJson);

		return sendRequest(request);
	}

	public String makeSignature(String password, byte[] requestBody) {
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

	/**
	 * Returns a request-object.
	 * 
	 * @param apiEndpoint
	 * @param requestJson
	 * @return
	 */
	public Request buildRequest(String apiEndpoint, JSONObject requestJson) {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, requestJson.toString());

		// Password hash
		String userSignature = makeSignature(GEOFOX_API_PASSWORD, requestJson.toString().getBytes());

		return new Request.Builder().url(apiEndpoint).post(body).addHeader("Accept", "application/json")
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
}