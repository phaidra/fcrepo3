
package org.fcrepo.server.types.mtom.gen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.fcrepo.server.types.gen.Datastream;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="datastream" type="{http://www.fedora.info/definitions/1/0/types/}Datastream" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "datastream"
})
@XmlRootElement(name = "getDatastreamsResponse")
public class GetDatastreamsResponse {

    protected List<Datastream> datastream;

    /**
     * Gets the value of the datastream property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datastream property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatastream().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Datastream }
     * 
     * 
     */
    public List<Datastream> getDatastream() {
        if (datastream == null) {
            datastream = new ArrayList<Datastream>();
        }
        return this.datastream;
    }

}
