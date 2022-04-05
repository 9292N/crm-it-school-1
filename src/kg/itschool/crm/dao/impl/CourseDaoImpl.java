package kg.itschool.crm.dao.impl;

import kg.itschool.crm.dao.CourseDao;
import kg.itschool.crm.dao.daoutil.Log;
import kg.itschool.crm.model.Course;
import kg.itschool.crm.model.CourseFormat;
import kg.itschool.crm.model.Manager;
import kg.itschool.crm.model.Mentor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDaoImpl implements CourseDao {

    public CourseDaoImpl() {
        Connection connection = null;
        Statement statement = null;

        try {
            System.out.println("Connecting to database...");
            connection = getConnection();
            System.out.println("Connection succeeded.");

            String ddlQuery = "CREATE TABLE IF NOT EXISTS tb_courses(" +
                    "id           BIGSERIAL, " +
                    "name   VARCHAR(50)  NOT NULL, " +
                    "price       MONEY        NOT NULL, " +
                    "date_created TIMESTAMP    NOT NULL DEFAULT NOW(), " +
                    "course_format_id BIGINT NOT NULL, " +
                    "" +
                    "CONSTRAINT pk_course_id PRIMARY KEY(id)," +
                    "CONSTRAINT fk_course_format_id FOREIGN KEY (course_format_id) " +
                    "   REFERENCES tb_course_format(id)" +
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
    public Course save(Course course) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Course savedCourse = null;

        try {
            System.out.println("Connecting to database...");
            connection = getConnection();
            System.out.println("Connection succeeded.");

            String createQuery = "INSERT INTO tb_courses(" +
                    "name, price, date_created, course_format_id) " +

                    "VALUES(?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(createQuery);
            preparedStatement.setString(1, course.getName());
            preparedStatement.setString(2, String.valueOf(course.getPrice()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(course.getDateCreated()));
            preparedStatement.setLong(4, course.getId());

            preparedStatement.execute();
            close(preparedStatement);

            String readQuery = "SELECT c.id, c.name, c.price, c.date_created, f.* " +
                    "FROM tb_course AS c " +
                    "JOIN tb_course_format AS f " +
                    "ON c.course_format_id = f.id " +
                    "ORDER BY c.id DESC " +
                    "LIMIT 1";

            preparedStatement = connection.prepareStatement(readQuery);
            resultSet = preparedStatement.executeQuery();

            resultSet.next();

            CourseFormat courseFormat = new CourseFormat();
            courseFormat.setId(resultSet.getLong("f.id"));
            courseFormat.setFormat(resultSet.getString("course_format"));
            courseFormat.setOnline(resultSet.getBoolean("is_online"));
            courseFormat.setLessonDuration(resultSet.getTime("lesson_duration").toLocalTime());
            courseFormat.setCourseDurationWeeks(resultSet.getInt("course_duration_weeks"));
            courseFormat.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());
            courseFormat.setLessonsPerWeek(resultSet.getInt("lessons_per_week"));

            savedCourse = new Course();
            savedCourse.setId(resultSet.getLong("id"));
            savedCourse.setName(resultSet.getString("name"));
            savedCourse.setPrice(Double.parseDouble(resultSet.getString("price")));
            savedCourse.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());
            savedCourse.setCourseFormat(courseFormat);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return savedCourse;
    }

    @Override
    public Course findById(Long id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Course course = null;

        try {
            connection = getConnection();

            String readQuery = "SELECT c.id, c.name, c.price, c.date_created, f.* " +
                    "FROM tb_course AS c " +
                    "JOIN tb_course_format AS f " +
                    "ON c.course_format_id = f.id " +
                    "WHERE c.id = ?;";

            preparedStatement = connection.prepareStatement(readQuery);
            preparedStatement.setLong(1, id);

            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            course = new Course();
            course.setId(resultSet.getLong("id"));
            course.setName(resultSet.getString("name"));
            course.setPrice(Double.parseDouble(resultSet.getString("price")));
            course.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());
            CourseFormat courseFormat = new CourseFormat();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return course;
    }

    @Override
    public List<Course> findAll() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List <Course> courses = new ArrayList<>();
        List <CourseFormat> courseFormats = new ArrayList<>();
        List<Manager> managers = new ArrayList<>();
        List <Mentor> mentors = new ArrayList<>();
        try {
            Log.info(this.getClass().getSimpleName() + " findAll()", Connection.class.getSimpleName(), "Establishing connection");
            connection = getConnection();

            String readQuery = "SELECT * FROM tb_mentors;";

            preparedStatement = connection.prepareStatement(readQuery);

            resultSet = preparedStatement.executeQuery();

            for (int i = 0; i <= courses.size() && resultSet.next(); i++) {
                Course course = new Course();
                course.setId(resultSet.getLong("id"));
                course.setName(resultSet.getString("first_name"));
                course.setPrice(Double.parseDouble(resultSet.getString("salary").replaceAll("[^\\d.]", "")));
                courses.add(course);
            }
            return courses;
        } catch (Exception e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClassName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }

        return null;


    }
}
