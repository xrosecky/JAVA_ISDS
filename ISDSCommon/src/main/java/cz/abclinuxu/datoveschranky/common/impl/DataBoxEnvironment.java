/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.abclinuxu.datoveschranky.impl;

/**
 *
 * @author xrosecky
 */
public enum DataBoxEnvironment {

    TEST ("ws1.czebox.cz", "ws1c.czebox.cz"),
    PRODUCTION ("ws1.mojedatovaschranka.cz", "ws1c.mojedatovaschranka.cz");

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
