package cz.abclinuxu.datoveschranky.common.entities;

import java.util.GregorianCalendar;

/**
 *
 * Reprezentuje jednu událost doručenky.
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

    /**
     * Popis události
     */ 
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Čas, kdy tato událost nastala.
     * 
     */
    public GregorianCalendar getTime() {
        return time;
    }

    public void setTime(GregorianCalendar time) {
        this.time = time;
    }  

}
