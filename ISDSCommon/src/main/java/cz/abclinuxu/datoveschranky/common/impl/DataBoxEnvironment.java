/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.abclinuxu.datoveschranky.common.impl;

/**
 * 
 * @author xrosecky
 */
public enum DataBoxEnvironment {

	TEST("ws1.czebox.cz", "ws1c.czebox.cz", "ws2.czebox.cz", "ws2c.czebox.cz"), PRODUCTION(
			"ws1.mojedatovaschranka.cz", "ws1c.mojedatovaschranka.cz",
			"ws2.mojedatovaschranka.cz", "ws2c.mojedatovaschranka.cz");

	private String basicURL;
	private String clientCertURL;
	private String basicURLVODZ;
	private String clientCertURLVODZ;

	DataBoxEnvironment(String basicURL, String clientCertURL,
			String basicURLVODZ, String clientCertURLVODZ) {
		this.basicURL = basicURL;
		this.clientCertURL = clientCertURL;
		this.basicURLVODZ = basicURLVODZ;
		this.clientCertURLVODZ = clientCertURLVODZ;
	}

	public String basicURL() {
		return basicURL;
	}

	public String clientCertURL() {
		return clientCertURL;
	}

	public String basicURL(boolean isVODZ) {
		return (isVODZ) ? basicURLVODZ : basicURL;
	}

	public String clientCertURL(boolean isVODZ) {
		return (isVODZ) ? clientCertURLVODZ : clientCertURL;
	}

}
