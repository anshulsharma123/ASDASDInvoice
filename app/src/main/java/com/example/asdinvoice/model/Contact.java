package com.example.asdinvoice.model;

public class Contact {
    private int id;
    private String name;
    private String rupees;

    public Contact(int id, String name, String rupees) {
        this.id = id;
        this.name = name;
        this.rupees = rupees;
    }
    public Contact( String name, String rupees) {
        this.name = name;
        this.rupees = rupees;
    }
    public Contact() {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRupees() {
        return rupees;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRupees(String rupees) {
        this.rupees = rupees;
    }
}

