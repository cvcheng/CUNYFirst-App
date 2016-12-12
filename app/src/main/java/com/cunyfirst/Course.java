package com.cunyfirst;

public class Course {
    private String title;
    private String section;
    private String time;
    private String room;
    private String professor;
    private String credits;
    private int color;

    public Course(String title, String section, String time, String room, String professor, String credits, int color) {
        this.title = title;
        this.section = section;
        this.time = time;
        this.room = room;
        this.professor = professor;
        this.credits = credits;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }
    public String getSection() {
        return section;
    }
    public String getTime() {
        return time;
    }
    public String getProf() {
        return professor;
    }
    public String getRoom() {
        return room;
    }
    public String getCredits() { return credits; }
    public int getColor() {
        return color;
    }
}
