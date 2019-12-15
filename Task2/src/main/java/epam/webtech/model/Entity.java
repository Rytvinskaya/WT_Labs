package epam.webtech.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Entity implements Serializable {

    @JacksonXmlProperty(isAttribute = true)
    private int id;

}
