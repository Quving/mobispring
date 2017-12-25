package requestbody;

public class DepartureTime {
	private String station;
	private String hhMMyyyy;
	private String HHmm;
	private int maxList;

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getHhMMyyyy() {
		return hhMMyyyy;
	}

	public void setHhMMyyy(String hhMMyyy) {
		this.hhMMyyyy = hhMMyyy;
	}

	public String getHHmm() {
		return HHmm;
	}

	public void setHHmm(String hHmm) {
		HHmm = hHmm;
	}

	public int getMaxList() {
		return maxList;
	}

	public void setMaxList(int maxList) {
		this.maxList = maxList;
	}

}
