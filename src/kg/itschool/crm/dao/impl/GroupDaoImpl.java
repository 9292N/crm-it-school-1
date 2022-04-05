package kg.itschool.crm.dao.impl;

import kg.itschool.crm.dao.GroupDao;
import kg.itschool.crm.dao.daoutil.Log;
import kg.itschool.crm.model.Course;
import kg.itschool.crm.model.CourseFormat;
import kg.itschool.crm.model.Group;
import kg.itschool.crm.model.Mentor;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class GroupDaoImpl implements GroupDao {

    public GroupDaoImpl() {
        Connection connection = null;
        Statement statement = null;

        try {
            System.out.println("Connecting to database...");
            connection = getConnection();
            System.out.println("Connection succeeded.");

            String ddlQuery = "CREATE TABLE IF NOT EXISTS tb_groups(" +
                    "id           BIGSERIAL, " +
                    "name       VARCHAR(50)  NOT NULL, " +
                    "group_time  TIMESTAMP   NOT NULL, " +
                    "date_created TIMESTAMP    NOT NULL DEFAULT NOW(), " +
                    "course_id BIGINT NOT NULL, " +
                    "mentor_id BIGINT NOT NULL, " +
                    "" +
                    "CONSTRAINT pk_group_id PRIMARY KEY(id)," +
                    "CONSTRAINT fk_course_id FOREIGN KEY (course_id) " +
                    "   REFERENCES tb_courses (id), " +
                    "CONSTRAINT fk_mentor_id FOREIGN KEY (mentor_id) " +
                    "   REFERENCES tb_mentor (id)" +
                    ");";

            System.out.println("Creating statement...");
            statement = connection.createStatement();
            System.out.println("Executing create table statement...");
            statement.execute(ddlQuery);
            System.out.println(ddlQuery);

        } catch (SQLException e) {
            System.out.println("Some error occurred");
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
    }

    @Override
    public Group save(Group group) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Group savedGroup = null;

        try {
            System.out.println("Connecting to database...");
            connection = getConnection();
            System.out.println("Connection succeeded.");

            String createQuery = "INSERT INTO tb_groups(" +
                    "name, group_time, date_created, mentor_id, course_id) " +

                    "VALUES(?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(createQuery);
            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, String.valueOf(group.getGroupTime()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(group.getDateCreated()));
            preparedStatement.setLong(4, group.getMentor().getId());
            preparedStatement.setLong(5, group.getCourse().getId());

            preparedStatement.execute();
            close(preparedStatement);

            String subQuery = "SELECT c.id AS course_id, c.name, c.price, c.date_created, " +
                    "f.id AS format_id, f.course_format, f.course_duration_weeks, f.lesson_duration, " +
                    "f.lessons_per_week, f.is_online, f.date_created AS format_dc " +
                    "FROM tb_course AS c " +
                    "JOIN tb_course_format AS f " +
                    "ON c.course_format_id = f.id";

            String readQuery = "" +
                    "SELECT g.id AS group_id, g.name, g.group_time, " +
                    "g.date_created AS group_dc, g.course_id, g.mentor_id " +
                    "FROM tb_groups AS g " +
                    "JOIN (" + subQuery + " WHERE course_id = g.course_id) AS c " +
                    "ON g.course_id = c.id " +
                    "JOIN tb_mentor AS m " +
                    "ON g.mentor_id = m.id " +
                    "ORDER BY g.id DESC " +
                    "LIMIT 1";

            preparedStatement = connection.prepareStatement(readQuery);
            resultSet = preparedStatement.executeQuery();

            resultSet.next();

            savedGroup = new Group();
            savedGroup.setId(resultSet.getLong("id"));
            savedGroup.setName(resultSet.getString("name"));
            savedGroup.setGroupTime(LocalTime.parse(resultSet.getString("group_time")));
            savedGroup.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return savedGroup;
    }

    @Override
    public Group findById(Long id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Group group = null;

        try {
            connection = getConnection();

            String readQuery = "SELECT * FROM tb_groups WHERE id = ?";

            preparedStatement = connection.prepareStatement(readQuery);
            preparedStatement.setLong(1, id);

            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            group = new Group();
            group.setId(resultSet.getLong("id"));
            group.setName(resultSet.getString("name"));
            group.setGroupTime(LocalTime.parse(resultSet.getString("group_time")));
            group.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return group;
    }

    @Override
    public List<Group> findAll() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Group> groups = null;
        try {
            connection = getConnection();

            String readQuery =
                    "SELECT  g.id AS group_id, g.name AS group_name, g.group_time, " +
                            "    g.date_created AS group_dc, " +
                            "        g.course_id, c.name AS course_name, c.price, course_dc, " +
                            "    format_id, course_format, course_duration_weeks, lesson_duration, " +
                            "    lessons_per_week, is_online, format_dc, " +
                            "        g.mentor_id, m.first_name, m.last_name, m.email, m.phone_number, m.salary, m.dob, m.date_created AS mentor_dc " +
                            "    FROM tb_groups AS g " +
                            "" +
                            "    JOIN (SELECT c.id AS course_id, c.name, c.price, c.date_created AS course_dc, " +
                            "    f.id AS format_id, f.course_format, f.course_duration_weeks, f.lesson_duration, " +
                            "    f.lessons_per_week, f.is_online, f.date_created AS format_dc " +
                            "    FROM tb_courses AS c " +
                            "    JOIN tb_course_format AS f " +
                            "    ON c.course_format_id = f.id) AS c " +
                            "    ON g.course_id = c.course_id " +
                            "" +
                            "    JOIN tb_mentors AS m " +
                            "    ON g.mentor_id = m.id " +
                            ";";
            preparedStatement = connection.prepareStatement(readQuery);

            resultSet = preparedStatement.executeQuery();

            groups = new ArrayList<>();

            while (resultSet.next()) {
                CourseFormat courseFormat = new CourseFormat();
                courseFormat.setId(resultSet.getLong("format_id"));
                courseFormat.setFormat(resultSet.getString("course_format"));
                courseFormat.setCourseDurationWeeks(resultSet.getInt("course_duration_weeks"));
                courseFormat.setLessonDuration(resultSet.getTime("lesson_duration").toLocalTime());
                courseFormat.setLessonsPerWeek(resultSet.getInt("lessons_per_week"));
                courseFormat.setOnline(resultSet.getBoolean("is_online"));
                courseFormat.setDateCreated(resultSet.getTimestamp("format_dc").toLocalDateTime());

                Course course = new Course();
                course.setId(resultSet.getLong("course_id"));
                course.setName(resultSet.getString("course_name"));
                course.setPrice(Double.parseDouble(resultSet.getString("price").replaceAll("[^\\d\\.]+", "")) / 100);
                course.setCourseFormat(courseFormat);
                course.setDateCreated(resultSet.getTimestamp("course_dc").toLocalDateTime());

                Mentor mentor = new Mentor();
                mentor.setId(resultSet.getLong("mentor_id"));
                mentor.setFirstName(resultSet.getString("first_name"));
                mentor.setLastName(resultSet.getString("last_name"));
                mentor.setEmail(resultSet.getString("email"));
                mentor.setPhoneNumber(resultSet.getString("phone_number"));
                mentor.setSalary(Double.parseDouble(resultSet.getString("salary").replaceAll("[^\\d\\.]", "")) / 100);
                mentor.setDob(resultSet.getDate("dob").toLocalDate());
                mentor.setDateCreated(resultSet.getTimestamp("mentor_dc").toLocalDateTime());

                Group group = new Group();
                group.setId(resultSet.getLong("group_id"));
                group.setName(resultSet.getString("group_name"));
                group.setGroupTime(LocalTime.parse(resultSet.getString("group_time")));
                group.setDateCreated(resultSet.getTimestamp("group_dc").toLocalDateTime());
                group.setCourse(course);
                group.setMentor(mentor);

                groups.add(group);
            }

        } catch (Exception e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClassName(), e.getMessage());
            e.printStackTrace();
        }
        return groups;
    }
}
