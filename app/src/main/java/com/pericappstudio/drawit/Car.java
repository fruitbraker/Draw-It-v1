package com.pericappstudio.drawit;

import com.cloudmine.api.db.LocallySavableCMObject;

import java.util.List;

/**
 * Created by Eric P on 5/23/2015.
 */
public class Car extends LocallySavableCMObject {

    private String name;
    private int year;
    private List<String> someRandomStuff;

    public Car() {
        super();
    }

    public Car(String name, int year, List<String> lol) {
        this.name = name;
        this.year = year;
        this.someRandomStuff = lol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSomeRandomStuff() {
        return someRandomStuff;
    }

    public void setSomeRandomStuff(List<String> someRandomStuff) {
        this.someRandomStuff = someRandomStuff;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void addRandomString(String random) {
        someRandomStuff.add(random);
    }
}
