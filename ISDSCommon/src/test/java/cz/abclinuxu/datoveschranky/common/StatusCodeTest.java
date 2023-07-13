package cz.abclinuxu.datoveschranky.common;

import cz.abclinuxu.datoveschranky.common.entities.StatusCode;
import junit.framework.Assert;
import junit.framework.TestCase;

public class StatusCodeTest extends TestCase {

	public void test() {
		Assert.assertEquals(StatusCode.WS_OR_XML_ERRORS, StatusCode.byCode("2000"));
		Assert.assertEquals(StatusCode.WS_OR_XML_ERRORS, StatusCode.byCode("2500"));
		Assert.assertEquals(StatusCode.WS_OR_XML_ERRORS, StatusCode.byCode("2999"));
		Assert.assertEquals(StatusCode.SPECIAL_ERRORS, StatusCode.byCode("3000"));
		Assert.assertEquals(StatusCode.SPECIAL_ERRORS, StatusCode.byCode("3999"));
		Assert.assertEquals(StatusCode.FUNCTIONAL_FAILURE, StatusCode.byCode("9999"));
		Assert.assertEquals(StatusCode.SENDING_INITIATORY_PDZ_WITHOUT_CONTRACT_NOT_ALLOWED, StatusCode.byCode("1242"));
		Assert.assertEquals(StatusCode.REQUEST_WAS_REGISTERED, StatusCode.byCode("0001"));
		Assert.assertEquals(StatusCode.OK, StatusCode.byCode("0000"));
		Assert.assertEquals(StatusCode.UNKNOWN_ERROR, StatusCode.byCode("TEST"));
	}

}
