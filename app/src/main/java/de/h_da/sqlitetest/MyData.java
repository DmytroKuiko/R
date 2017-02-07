package de.h_da.sqlitetest;

/**
 * Created by User on 29.12.2016.
 */
public class MyData {
    private int id;
    private String day;
    private String month;
    private String year;
    private Double sum;
    private String description;

    public MyData(int id, String day, String month, String year, Double sum, String description) {
        this.id = id;
        this.day = day;
        this.month = month;
        this.year = year;
        this.sum = sum;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public Double getSum() {
        return sum;
    }

    public String getDescription() {
        return description;
    }
}
