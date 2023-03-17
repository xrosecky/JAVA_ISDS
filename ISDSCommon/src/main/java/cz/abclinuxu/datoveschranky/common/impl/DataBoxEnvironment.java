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

    TEST ("ws2.czebox.cz", "ws2c.czebox.cz"),
    PRODUCTION ("ws2.mojedatovaschranka.cz", "ws2c.mojedatovaschranka.cz");

    private String basicURL;
    private String clientCertURL;

    DataBoxEnvironment(String basicURL, String clientCertURL) {
        this.basicURL = basicURL;
        this.clientCertURL = clientCertURL;
    }

    public String basicURL() {
        return basicURL;
    }

    public String clientCertURL() {
        return clientCertURL;
    }

}
