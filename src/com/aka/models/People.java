package com.aka.models;

public class People {
    private String name;
    private Integer age;
    private double salary;
    private Specials specials;
    private People spouse;

    public People() {
        specials = new Specials(4, 5, 4, 5, 7, 5, 5);
    }

    public People(String name, Integer age, double salary, Specials specials, People spouse) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.specials = specials;
        this.spouse = spouse;
    }

    public People getSpouse() {
        return spouse;
    }

    public void setSpouse(People spouse) {
        this.spouse = spouse;
    }

    public Specials getSpecials() {
        return specials;
    }

    private void paySalary() {
        System.out.println("I have salary: " + salary);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
