package tech.rsqn.cdsl.parser;

import com.thoughtworks.xstream.XStream;
import org.springframework.core.io.ClassPathResource;
import tech.rsqn.cdsl.model.DocumentDefinition;

import java.io.IOException;

public class XStreamParser {

    public DocumentDefinition loadCdslDefinition(String resource) {
        XStream xstream = new XStream();
        xstream.processAnnotations(DocumentDefinition.class);

        try {
            Object o = xstream.fromXML(new ClassPathResource(resource).getInputStream());
            DocumentDefinition doc = (DocumentDefinition) o;
            return doc;
        } catch (IOException e) {
            throw new RuntimeException("Unable to load resource " + resource, e);
        }


    }
}
