package com.aka.storage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.ValidationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Serializer {
    private final static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private static final Map<String, Class<?>> namePrimitiveMap = new HashMap<String, Class<?>>();

    static {
        namePrimitiveMap.put("boolean", Boolean.class);
        namePrimitiveMap.put("byte", Byte.class);
        namePrimitiveMap.put("char", Character.class);
        namePrimitiveMap.put("short", Short.class);
        namePrimitiveMap.put("int", Integer.class);
        namePrimitiveMap.put("long", Long.class);
        namePrimitiveMap.put("double", Double.class);
        namePrimitiveMap.put("float", Float.class);
    }

    public static void serialize(Object o, OutputStream outputStream) throws Exception {
        DocumentBuilder db = dbf.newDocumentBuilder();
        final Document doc = db.newDocument();
        final Element root = doc.createElement("object");
        doc.appendChild(root);

        final Class<?> c = o.getClass();
        root.setAttribute("type", c.getName());

        HashSet<Object> serList = new HashSet<>();
        serList.add(o);
        for (Field field : c.getDeclaredFields()) {
            appendField(doc, root, o, field, serList);
        }

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(outputStream);
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(source, result);
    }

    public static Object deserialize(final InputStream inputStream) throws Exception {
        DocumentBuilder db = dbf.newDocumentBuilder();
        final Document doc = db.parse(inputStream);

        if (!"object".equals(doc.getDocumentElement().getTagName()))
            throw new ValidationException("Document element must be 'object'");

        return loadNode(doc.getDocumentElement());
    }

    private static Object loadNode(Element node) throws Exception {

        String typeName = node.getAttribute("type");
        if (typeName.isEmpty())
            throw new ValidationException("Object type missing");

        Class<?> nodeClass = namePrimitiveMap.getOrDefault(typeName, null);
        if (nodeClass == null)
            nodeClass = Class.forName(typeName);

        if (isPrimitiveWrapper(nodeClass)) {
            if (!node.hasAttribute("value")) {
                throw new ValidationException("Primitive fields must have 'value'");
            }

            String nodeVal = node.getAttribute("value");

            if (nodeClass != String.class && nodeVal.isEmpty()) {
                throw new ValidationException("Primitive fields must have non-empty 'value'");
            }

            if (node.hasAttribute("value")) {
                if (nodeClass == Character.class) {
                    if (nodeVal.length() != 1)
                        throw new ValidationException("Character length must be 1");
                    return nodeVal.charAt(0);
                }

                if ((nodeVal.length() > 0) || (nodeClass == String.class)) {
                    return nodeClass.getConstructor(String.class).newInstance(nodeVal);
                }
            }
            return null;
        }

        if (node.hasAttribute("value"))
            throw new ValidationException("Value attribute not allowed for non-primitive types");

        Constructor<?> constructor = null;
        for (Constructor<?> ctr : nodeClass.getDeclaredConstructors()) {
            if (ctr.getParameterCount() == 0 && Modifier.isPublic(ctr.getModifiers())) {
                constructor = ctr;
                break;
            }
        }

        if (constructor == null) {
            throw new NoSuchMethodException("No-arg constructor req for " + nodeClass.getName());
        }

        Object obj = constructor.newInstance();

        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (!(node.getChildNodes().item(i) instanceof Element))
                continue;

            Element fieldNode = (Element) node.getChildNodes().item(i);

            if (!"field".equals(fieldNode.getTagName()))
                throw new ValidationException("Field elements must be 'field'");

            if (fieldNode.getAttribute("id").equals(""))
                throw new ValidationException("Field elements must have 'id'");

            Field field = nodeClass.getDeclaredField(fieldNode.getAttribute("id"));
            int modifiers = field.getModifiers();
            if (Modifier.isPrivate(modifiers) || Modifier.isProtected(modifiers)) {
                field.setAccessible(true);
            }
            field.set(obj, loadNode(fieldNode));
        }

        return obj;
    }

    private static void appendField(final Document doc, final Element root, Object obj,
                                    Field field, HashSet<Object> serList)
            throws IllegalAccessException {
        final Class<?> c = field.getType();
        Element node = doc.createElement("field");

        node.setAttribute("type", c.getName());
        node.setAttribute("id", field.getName());
        root.appendChild(node);

        int modifiers = field.getModifiers();
        if (Modifier.isPrivate(modifiers) || Modifier.isProtected(modifiers)) {
            field.setAccessible(true);
        }

        Object fieldObj = field.get(obj);
        if (fieldObj == null || serList.contains(fieldObj)) {
            root.removeChild(node);
            return;
        }

        if (!isPrimitiveOrWrapper(c)) {
            serList.add(fieldObj);
            for (Field fld : fieldObj.getClass().getDeclaredFields()) {
                appendField(doc, node, fieldObj, fld, serList);
            }
            serList.remove(fieldObj);
        } else {
            node.setAttribute("value", fieldObj.toString());
        }
    }

    private static boolean isPrimitiveOrWrapper(final Class<?> type) {
        if (type == null) {
            return false;
        }

        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    private static boolean isPrimitiveWrapper(final Class<?> type) {
        return (type == Boolean.class) ||
                (type == Byte.class) ||
                (type == Character.class) ||
                (type == Short.class) ||
                (type == Integer.class) ||
                (type == Long.class) ||
                (type == Double.class) ||
                (type == Float.class) ||
                (type == String.class);
    }
}
