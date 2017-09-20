package tech.rsqn.cdsl.definitionsource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

@Component
public class XmlDomDefinitionSource {
    private static final Logger logger = LoggerFactory.getLogger(XmlDomDefinitionSource.class);

    public DocumentDefinition loadCdslDefinition(String resource) {
        ClassPathResource cpr = new ClassPathResource(resource);
        Reader reader = null;
        try {
            reader = new InputStreamReader(cpr.getInputStream());
            String xmlContent = IOUtils.toString(reader);
            return parse(xmlContent);
        } catch (Exception ex) {
            throw new RuntimeException("Error loading cdsl definition " + resource, ex);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private DocumentDefinition parse(String xml) throws Exception {
        ByteArrayInputStream is = null;
        Document doc = null;
        try {
            is = new ByteArrayInputStream(xml.getBytes("utf-8"));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
        } finally {
            IOUtils.closeQuietly(is);
        }

        DocumentDefinition ret = new DocumentDefinition();
        NodeList flows = doc.getElementsByTagName("flow");
        for (int i = 0; i < flows.getLength(); i++) {
            Node n = flows.item(i);
            FlowDefinition flow = loadFlowDefinition(n);
            ret.getFlows().add(flow);
        }
        return ret;
    }

    private String getAttr(Node node, String name) {
        if (node.getAttributes() != null) {
            Node attr = node.getAttributes().getNamedItem(name);
            if (attr != null) {
                return attr.getNodeValue();
            }
        }
        return null;
    }

    private FlowDefinition loadFlowDefinition(Node node) {
        FlowDefinition ret = new FlowDefinition();
        ret.setId(getAttr(node, "id"));
        ret.setDefaultStep(getAttr(node, "defaultStep"));
        ret.setErrorStep(getAttr(node, "errorStep"));

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ( child.getNodeType() == Node.ELEMENT_NODE) {
                ElementDefinition def = loadElementDefinition(child, 0, ElementDefinition.Classifier.FlowStep);
                ret.getElements().add(def);
            }
        }

        return ret;
    }

    private ElementDefinition loadElementDefinition(Node node, int depth, ElementDefinition.Classifier classifier) {
        String id =  getAttr(node, "id");
        String name = node.getNodeName();

        ElementDefinition ret = new ElementDefinition();
        ret.setId(id);
        ret.setName(name);
        ret.setClassifier(classifier);

        String text = node.getTextContent();
        if (StringUtils.isNotEmpty(text)) {
            ret.setTextValue(text.trim());
        }

        if (node.getAttributes() != null) {
            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                Node attrNode = node.getAttributes().item(i);
                String attrName = attrNode.getNodeName();
                String attrVal = attrNode.getNodeValue();
                ret.getAttrs().put(attrName, attrVal);
            }
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ( child.getNodeType() == Node.ELEMENT_NODE) {
                ret.getChildren().add(loadElementDefinition(child, depth++, ElementDefinition.Classifier.DslElement));
            } else if ( child.getNodeType() == Node.TEXT_NODE) {
                ret.setTextValue(child.getTextContent().trim());
            }
        }

        return ret;
    }


}
