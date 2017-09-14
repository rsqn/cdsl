package tech.rsqn.cdsl.registry;

import tech.rsqn.cdsl.model.definition.ElementDefinition;
import tech.rsqn.reflectionhelpers.AttributeDescriptor;
import tech.rsqn.reflectionhelpers.ReflectionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DslModelBuilder {
    public <T> T buildModel(ElementDefinition dslElement, Class modelClass) {
        try {
            T ret = (T) modelClass.newInstance();
            recurseElementAndPopulateModel(dslElement, ret);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("Error creating model for " + dslElement.getName() + " model class " + (modelClass != null ? modelClass.getName() : null), e);
        }
    }

    private void recurseElementAndPopulateModel(ElementDefinition elem, Object o) throws Exception {
        // Set attributes


        Map<String, AttributeDescriptor> attrs = ReflectionHelper.collectAttributeMetaDataAsMapWithPossibleNames(o);

        for (String k : elem.getAttrs().keySet()) {
            String v = elem.getAttrs().get(k);

            AttributeDescriptor attr = attrs.get(k);
            if (attr == null) {
                throw new RuntimeException("Unable to find field " + k + " in " + o.getClass().getName());
            }

            attr.executeSetter(o, v);
        }

        // Set elements
        for (ElementDefinition child : elem.getChildren()) {
            String k = child.getName();
            AttributeDescriptor attr = attrs.get(k);
            if (attr == null) {
                throw new RuntimeException("Unable to find field " + k + " in " + o.getClass().getName());
            }

            if (isConvertableType(attr.getType())) {
                attr.executeSetter(o, child.getTextValue());
            } else if (isList(attr.getType())) {
                recurseElementAndPopulateList(child, attr.getName(), o);
            } else if (isMap(attr.getType())) {
                recurseElementAndPopulateMap(child, attr.getName(), o);
            } else {
                Object fv = attr.getType().newInstance();
                recurseElementAndPopulateModel(child, fv);
            }
        }
    }

    /**
     * Supports lists of strings in the format
     * <list>
     * <entry>value</entry>
     * <entry>value</entry>
     * </list>
     *
     * @param elem
     * @param f
     * @param o
     * @throws Exception
     */
    private void recurseElementAndPopulateList(ElementDefinition elem, String fieldName, Object o) throws Exception {
        List l = new ArrayList();

        for (ElementDefinition child : elem.getChildren()) {
            l.add(child.getTextValue());
        }

        ReflectionHelper.putAttribute(o, fieldName, l);
    }

    /**
     * Supports maps of strings in the format
     * <list>
     * <entry key="keyA" val="val1"/>
     * <entry key="keyB" val="val2"/>
     * </list>
     *
     * @param elem
     * @param f
     * @param o
     * @throws Exception
     */
    private void recurseElementAndPopulateMap(ElementDefinition elem, String fieldName, Object o) throws Exception {
        Map<String, String> m = new HashMap();

        for (ElementDefinition child : elem.getChildren()) {
            m.put(child.getAttrs().get("key"), child.getAttrs().get("val"));
        }
        ReflectionHelper.putAttribute(o, fieldName, m);
    }


    private boolean isConvertableType(Class c) {
        if (String.class.isAssignableFrom(c)) {
            return true;
        }
        if (Number.class.isAssignableFrom(c)) {
            return true;
        }
        if (Boolean.class.isAssignableFrom(c)) {
            return true;
        }
        return false;
    }

    private boolean isList(Class c) {
        if (List.class.isAssignableFrom(c)) {
            return true;
        }
        return false;
    }

    private boolean isMap(Class c) {
        if (Map.class.isAssignableFrom(c)) {
            return true;
        }
        return false;
    }
}
