package com.aka.models;

public class Specials {
    private int strength;
    private int perception;
    private int endurance;
    private int charisma;
    private int intelligence;
    private int agility;
    private int luck;

    public Specials() {
        this.strength = 5;
        this.perception = 5;
        this.endurance = 5;
        this.charisma = 5;
        this.intelligence = 5;
        this.agility = 5;
        this.luck = 5;
    }

    public Specials(int strength, int perception, int endurance, int charisma, int intelligence,
                    int agility, int luck) {
        this.strength = strength;
        this.perception = perception;
        this.endurance = endurance;
        this.charisma = charisma;
        this.intelligence = intelligence;
        this.agility = agility;
        this.luck = luck;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getPerception() {
        return perception;
    }

    public void setPerception(int perception) {
        this.perception = perception;
    }

    public int getEndurance() {
        return endurance;
    }

    public void setEndurance(int endurance) {
        this.endurance = endurance;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }
}
