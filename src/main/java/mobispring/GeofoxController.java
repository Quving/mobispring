package mobispring;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import requestbody.DepartureTime;

@RestController
public class GeofoxController {

	private final AtomicLong counter = new AtomicLong();

	@RequestMapping(method = RequestMethod.POST, value = "/api/geofox/departuretime")
	public String departureTime(@RequestBody DepartureTime input) {
		Geofox geofox = new Geofox();
		String response = geofox.departureList(input.getStation(), input.getHhMMyyyy(), input.getHHmm(),
				input.getMaxList()).toString(4);
		return response;
	}
}