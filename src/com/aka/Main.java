package com.aka;

import com.aka.models.People;
import com.aka.models.Specials;
import com.aka.storage.Serializer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Main {

    public static void main(String[] args)
            throws IOException, IllegalAccessException, TransformerException, ParserConfigurationException {
        People people1 = new People("Крис Кельвин", 33, 330000, new Specials(), null);
        People people2 = new People("Хари Кельвин", 33, 330000, new Specials(), null);
        people1.setSpouse(people2);
        people2.setSpouse(people1);

        Serializer.serialize(people1, System.out);
    }
}
