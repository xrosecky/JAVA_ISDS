package cz.abclinuxu.datoveschranky.common.entities;

import java.util.GregorianCalendar;

/**
 *
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class DeliveryEvent {

    protected GregorianCalendar time;
    protected String description;

    public DeliveryEvent(GregorianCalendar time, String description) {
        this.time = time;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GregorianCalendar getTime() {
        return time;
    }

    public void setTime(GregorianCalendar time) {
        this.time = time;
    }  

}
