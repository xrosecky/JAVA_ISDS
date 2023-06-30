package cz.abclinuxu.datoveschranky.ws.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import cz.abclinuxu.datoveschranky.ws.dm.TDelivery;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GetDeliveryInfoResponse", namespace="http://isds.czechpoint.cz/v20")
public class GetDeliveryInfoResponse {

	@XmlElement(name="dmDelivery", namespace="http://isds.czechpoint.cz/v20", required = true)
	protected TDelivery dmDelivery;

	public TDelivery getDmDelivery() {
		return dmDelivery;
	}

	public void setDmDelivery(TDelivery dmDelivery) {
		this.dmDelivery = dmDelivery;
	}

}
