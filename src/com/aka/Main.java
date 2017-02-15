package com.aka;

import com.aka.loader.JarClassLoader;
import com.aka.models.People;
import com.aka.models.Specials;
import com.aka.storage.Serializer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;

public class Main {

    public static void main(String[] args) {
        People people1 = new People("Крис Кельвин", 33, 330000, new Specials(), null);
        People people2 = new People("Хари Кельвин", 33, 330000, new Specials(), null);
        People people3;
        people1.setSpouse(people2);
        people2.setSpouse(people1);

        try (FileOutputStream fos = new FileOutputStream("data.xml")) {
            Serializer.serialize(people1, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try (FileInputStream fis = new FileInputStream("data.xml")) {
            people3 = (People) Serializer.deserialize(fis);
            Serializer.serialize(people3, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JarClassLoader loader;
        try {
            loader = new JarClassLoader();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        Class aClass;
        Object myAnimal;

        try {
            aClass = loader.loadClass("Animal");
        } catch (ClassNotFoundException e) {
            System.err.println("Pet not found");
            return;
        }

        try {
            myAnimal = aClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Sory, it's a angry animal");
            return;
        }

        try {
            Serializer.serialize(myAnimal, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
