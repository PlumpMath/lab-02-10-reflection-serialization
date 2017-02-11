package com.aka.storage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;

public class Serializer {
    private final static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static void serialize(Object o, OutputStream outputStream)
            throws ParserConfigurationException, TransformerException, IllegalAccessException {
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
