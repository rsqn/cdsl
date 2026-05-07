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
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Component
public class XmlDomDefinitionSource {
    private static final Logger logger = LoggerFactory.getLogger(XmlDomDefinitionSource.class);

    public DocumentDefinition loadCdslDefinition(String resource) {
        return loadCdslDefinition(resource, null, new HashSet<>());
    }

    private DocumentDefinition loadCdslDefinition(String resource, String namespace, Set<String> includeStack) {
        if (StringUtils.isBlank(resource)) {
            throw new RuntimeException("CDSL resource cannot be blank");
        }
        if (!resource.startsWith("/")) {
            // Match existing usage in tests and Spring contexts (classpath absolute)
            resource = "/" + resource;
        }

        if (includeStack.contains(resource)) {
            throw new RuntimeException("Include cycle detected: " + includeStack + " -> " + resource);
        }
        includeStack.add(resource);

        ClassPathResource cpr = new ClassPathResource(resource);
        Reader reader = null;
        try {
            reader = new InputStreamReader(cpr.getInputStream());
            String xmlContent = IOUtils.toString(reader);
            DocumentDefinition parsed = parse(resource, xmlContent, includeStack);
            if (StringUtils.isNotBlank(namespace)) {
                applyNamespace(namespace, parsed);
            }
            return parsed;
        } catch (Exception ex) {
            throw new RuntimeException("Error loading cdsl definition " + resource, ex);
        } finally {
            IOUtils.closeQuietly(reader);
            includeStack.remove(resource);
        }
    }

    private DocumentDefinition parse(String resource, String xml, Set<String> includeStack) throws Exception {
        ByteArrayInputStream is = null;
        Document doc = null;
        try {
            is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
        } finally {
            IOUtils.closeQuietly(is);
        }

        DocumentDefinition ret = new DocumentDefinition();
        Node root = doc.getDocumentElement();
        if (root == null || !"cdsl".equals(root.getNodeName())) {
            throw new RuntimeException("Invalid CDSL document root in " + resource + " (expected <cdsl>)");
        }

        NodeList topLevel = root.getChildNodes();
        for (int i = 0; i < topLevel.getLength(); i++) {
            Node child = topLevel.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String name = child.getNodeName();
            if ("flow".equals(name)) {
                FlowDefinition flow = loadFlowDefinition(child);
                ret.getFlows().add(flow);
            } else if ("include".equals(name)) {
                String includeResource = getAttr(child, "resource");
                if (StringUtils.isBlank(includeResource)) {
                    throw new RuntimeException("Include resource cannot be blank in " + resource);
                }
                String namespace = getAttr(child, "namespace");
                if (StringUtils.isNotBlank(namespace) && namespace.contains("-")) {
                    throw new RuntimeException("Include namespace must not contain '-' (resource " + includeResource + " in " + resource + ")");
                }
                DocumentDefinition inc = loadCdslDefinition(includeResource, namespace, includeStack);
                ret.getFlows().addAll(inc.getFlows());
            }
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

    private void applyNamespace(String namespace, DocumentDefinition def) {
        if (def == null || def.getFlows() == null) {
            return;
        }
        for (FlowDefinition flow : def.getFlows()) {
            applyNamespace(namespace, flow);
        }
    }

    private void applyNamespace(String namespace, FlowDefinition flow) {
        if (flow == null) {
            return;
        }
        // Rewrite defaultStep/errorStep (step ids)
        if (StringUtils.isNotBlank(flow.getDefaultStep())) {
            flow.setDefaultStep(namespace + "-" + flow.getDefaultStep());
        }
        if (StringUtils.isNotBlank(flow.getErrorStep())) {
            flow.setErrorStep(namespace + "-" + flow.getErrorStep());
        }
        // Rewrite step ids and any step references inside the step bodies
        for (ElementDefinition step : flow.getElements()) {
            if (StringUtils.isNotBlank(step.getId())) {
                String newId = namespace + "-" + step.getId();
                step.setId(newId);
                if (step.getAttrs() != null) {
                    step.getAttrs().put("id", newId);
                }
            }
            rewriteStepReferences(namespace, step);
        }
    }

    private void rewriteStepReferences(String namespace, ElementDefinition element) {
        if (element == null) {
            return;
        }
        // Built-in step references: <routeTo target="..."/> and <await at="..."/>
        if ("routeTo".equals(element.getName())) {
            String target = element.getAttrs() != null ? element.getAttrs().get("target") : null;
            if (StringUtils.isNotBlank(target)) {
                element.getAttrs().put("target", namespace + "-" + target);
            }
        } else if ("await".equals(element.getName())) {
            String at = element.getAttrs() != null ? element.getAttrs().get("at") : null;
            if (StringUtils.isNotBlank(at)) {
                element.getAttrs().put("at", namespace + "-" + at);
            }
        } else if ("whiteList".equals(element.getName())) {
            String to = element.getAttrs() != null ? element.getAttrs().get("to") : null;
            if (StringUtils.isNotBlank(to)) {
                element.getAttrs().put("to", namespace + "-" + to);
            }
        }

        if (element.getChildren() != null) {
            for (ElementDefinition child : element.getChildren()) {
                rewriteStepReferences(namespace, child);
            }
        }
    }

}
