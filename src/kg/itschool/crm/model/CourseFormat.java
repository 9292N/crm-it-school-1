package kg.itschool.crm.model;

import java.time.LocalTime;

public final class CourseFormat extends BaseEntity {

    private String format; // BOOTCAMP || MEETUP || ORDINARY || ONLINE MEETUP
    private int courseDurationWeeks;
    private LocalTime lessonDuration;
    private int lessonsPerWeek;
    private boolean isOnline;

    public CourseFormat() {
        super();
    }

    public CourseFormat(Long id, String format, int courseDurationWeeks, LocalTime lessonDuration, int lessonsPerWeek, boolean isOnline) {
        super(id);
        this.format = format;
        this.courseDurationWeeks = courseDurationWeeks;
        this.lessonDuration = lessonDuration;
        this.lessonsPerWeek = lessonsPerWeek;
        this.isOnline = isOnline;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getCourseDurationWeeks() {
        return courseDurationWeeks;
    }

    public void setCourseDurationWeeks(int courseDurationWeeks) {
        this.courseDurationWeeks = courseDurationWeeks;
    }

    public LocalTime getLessonDuration() {
        return lessonDuration;
    }

    public void setLessonDuration(LocalTime lessonDuration) {
        this.lessonDuration = lessonDuration;
    }

    public int getLessonsPerWeek() {
        return lessonsPerWeek;
    }

    public void setLessonsPerWeek(int lessonsPerWeek) {
        this.lessonsPerWeek = lessonsPerWeek;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public String toString() {
        return "CourseFormat{" +
                "id=" + id +
                ", dateCreated=" + dateCreated +
                ", format='" + format + '\'' +
                ", courseDurationWeeks=" + courseDurationWeeks +
                ", lessonDuration=" + lessonDuration +
                ", lessonsPerWeek=" + lessonsPerWeek +
                ", isOnline=" + isOnline +
                '}';
    }
}
