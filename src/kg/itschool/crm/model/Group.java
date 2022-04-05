package kg.itschool.crm.model;

import java.time.LocalTime;

public final class Group extends BaseEntity {

    private String name;
    private LocalTime groupTime;
    private Course course;
    private Mentor mentor;

    public Group() {
        super();
    }

    public Group(Long id, String name, LocalTime groupTime, Course course, Mentor mentor) {
        super(id);
        this.name = name;
        this.groupTime = groupTime;
        this.course = course;
        this.mentor = mentor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getGroupTime() {
        return groupTime;
    }

    public void setGroupTime(LocalTime groupTime) {
        this.groupTime = groupTime;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", dateCreated=" + dateCreated +
                ", name='" + name + '\'' +
                ", groupTime=" + groupTime +
                ", course=" + course +
                '}';
    }
}
