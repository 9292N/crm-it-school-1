package kg.itschool.crm.dao.impl;

import kg.itschool.crm.dao.CourseFormatDao;
import kg.itschool.crm.dao.daoutil.Log;
import kg.itschool.crm.model.CourseFormat;

import java.sql.*;
import java.time.LocalTime;
import java.util.List;

public class CourseFormatDaoImpl implements CourseFormatDao {

    public CourseFormatDaoImpl() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), "Establishing connection");
            connection = getConnection();
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), "Connection established");

            String ddlQuery = "CREATE TABLE IF NOT EXISTS tb_course_format(" +
                    "id BIGSERIAL, " +
                    "course_format VARCHAR(50) NOT NULL, " +
                    "course_duration_weeks INT NOT NULL, " +
                    "lesson_duration TIME NOT NULL, " +
                    "lessons_per_week INT NOT NULL, " +
                    "is_online BOOLEAN NOT NULL, " +
                    "date_created TIMESTAMP NOT NULL DEFAULT NOW(), " +
                    "" +
                    "CONSTRAINT pk_course_format_id PRIMARY KEY(id), " +
                    "CONSTRAINT course_duration_weeks_negative_or_zero CHECK (course_duration_weeks > 0), " +
                    "CONSTRAINT lesson_per_week_negative_or_zero CHECK (lessons_per_week > 0)" +
                    ");";

            Log.info(this.getClass().getSimpleName(), PreparedStatement.class.getSimpleName(), "Creating preparedStatement");
            preparedStatement = connection.prepareStatement(ddlQuery);

            preparedStatement.execute();
        } catch (SQLException e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public CourseFormat save(CourseFormat courseFormat) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        CourseFormat savedCourseFormat = null;

        try {
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " connecting to database...");
            connection = getConnection();
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " connection succeeded.");

            String createQuery = "INSERT INTO tb_course_format(" +
                    "course_format, course_duration_weeks, lesson_duration, lessons_per_week, is_online, date_created ) " +

                    "VALUES(?, ?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(createQuery);
            preparedStatement.setString(1, courseFormat.getFormat());
            preparedStatement.setInt(2, courseFormat.getCourseDurationWeeks());
            preparedStatement.setTime(3, Time.valueOf((courseFormat.getLessonDuration())));
            preparedStatement.setInt(4, courseFormat.getLessonsPerWeek());
            preparedStatement.setBoolean(5, courseFormat.isOnline());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(courseFormat.getDateCreated()));

            preparedStatement.execute();
            close(preparedStatement);

            String readQuery = "SELECT * FROM tb_course_format ORDER BY id DESC LIMIT 1";

            preparedStatement = connection.prepareStatement(readQuery);

            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            savedCourseFormat = new CourseFormat();

            savedCourseFormat.setId(resultSet.getLong("id"));
            savedCourseFormat.setFormat(resultSet.getString("course_format"));
            savedCourseFormat.setCourseDurationWeeks(resultSet.getInt("course_duration_weeks"));
            savedCourseFormat.setLessonDuration(LocalTime.parse(resultSet.getString("lesson_duration")));
            savedCourseFormat.setLessonsPerWeek(resultSet.getInt("lessons_per_week"));
            savedCourseFormat.setOnline(resultSet.getBoolean("is_online"));
            savedCourseFormat.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());

        } catch (SQLException e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return savedCourseFormat;
    }

    @Override
    public CourseFormat findById(Long id) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        CourseFormat courseFormat = null;

        try {
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " connecting to database...");
            connection = getConnection();
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " connection succeeded.");

            String readQuery = "SELECT * FROM tb_course_format WHERE id = ?";

            preparedStatement = connection.prepareStatement(readQuery);
            preparedStatement.setLong(1, id);

            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            courseFormat = new CourseFormat();

            courseFormat.setId(resultSet.getLong("id"));
            courseFormat.setFormat(resultSet.getString("course_format"));
            courseFormat.setCourseDurationWeeks(resultSet.getInt("course_duration_weeks"));
            courseFormat.setLessonDuration(LocalTime.parse(resultSet.getString("lesson_duration")));
            courseFormat.setLessonsPerWeek(resultSet.getInt("lessons_per_week"));
            courseFormat.setOnline(resultSet.getBoolean("is_online"));
            courseFormat.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());

        } catch (SQLException e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return courseFormat;

    }

    @Override
    public List<CourseFormat> findAll() {
        return null;
    }
}
