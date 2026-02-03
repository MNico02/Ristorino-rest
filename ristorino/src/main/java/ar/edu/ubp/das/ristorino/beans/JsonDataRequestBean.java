package ar.edu.ubp.das.ristorino.beans;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "jsonRequest")
public class JsonDataRequestBean {
    private String jsonRequest;
}