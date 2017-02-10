package com.aka;

import com.aka.models.People;
import com.aka.models.Specials;
import com.aka.storage.Serializer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class Main {

    public static void main(String[] args)
            throws NoSuchMethodException, ParserConfigurationException, TransformerException {
        People people1 = new People("Kris Kelvin", 33, 330000, new Specials(), null);
        People people2 = new People("Harey Kelvin", 33, 330000, new Specials(), null);
        people1.setSpouse(people2);
        people2.setSpouse(people1);

        try {
            Serializer.Serialize(people1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
