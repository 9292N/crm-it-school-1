package dao.impl;

import dao.ManagerDao;
import dao.daoutil.Log;
import model.Manager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManagerDaoImpl implements ManagerDao {

    public ManagerDaoImpl() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {  // api:driver://host:port/database_name
            Log.info(this.getClass().getSimpleName() + " ManagerDaoImpl()", Connection.class.getSimpleName(), "Establishing connection");
            connection = getConnection();

            String ddlQuery = "CREATE TABLE IF NOT EXISTS tb_managers(" +
                    "id           BIGSERIAL, " +
                    "first_name   VARCHAR(50)  NOT NULL, " +
                    "last_name    VARCHAR(50)  NOT NULL, " +
                    "email        VARCHAR(100) NOT NULL UNIQUE, " +
                    "phone_number CHAR(13)     NOT NULL, " +
                    "salary       MONEY        NOT NULL, " +
                    "dob          DATE         NOT NULL CHECK(dob < NOW()), " +
                    "date_created TIMESTAMP    NOT NULL DEFAULT NOW(), " +
                    "" +
                    "CONSTRAINT pk_manager_id PRIMARY KEY(id), " +
                    "CONSTRAINT chk_manager_salary CHECK (salary > MONEY(0))," +
                    "CONSTRAINT chk_manager_first_name CHECK(LENGTH(first_name) > 2));";


            Log.info(this.getClass().getSimpleName() + " ManagerDaoImpl()", PreparedStatement.class.getSimpleName(), "Creating preparedStatement");
            preparedStatement = connection.prepareStatement(ddlQuery);

            preparedStatement.execute();
        } catch (SQLException e) {
            Log.info(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public Optional<Manager> save(Manager manager) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Manager savedManager = null;

        try {
            Log.info(this.getClass().getSimpleName() + " save()", Connection.class.getSimpleName(), "Establishing connection");
            connection = getConnection();

            String createQuery = "INSERT INTO tb_managers(" +
                    "last_name, first_name, phone_number, salary, date_created, dob, email) " +

                    "VALUES(?, ?, ?, MONEY(?), ?, ?, ?)";

            preparedStatement = connection.prepareStatement(createQuery);
            preparedStatement.setString(1, manager.getLastName());
            preparedStatement.setString(2, manager.getFirstName());
            preparedStatement.setString(3, manager.getPhoneNumber());
            preparedStatement.setString(4, (manager.getSalary() / 100 + "").replace(".", ","));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(manager.getDateCreated()));
            preparedStatement.setDate(6, Date.valueOf(manager.getDob()));
            preparedStatement.setString(7, manager.getEmail());

            preparedStatement.execute();
            close(preparedStatement);

            String readQuery = "SELECT * FROM tb_managers ORDER BY id DESC LIMIT 1";

            preparedStatement = connection.prepareStatement(readQuery);
            resultSet = preparedStatement.executeQuery();

            resultSet.next();

            savedManager = new Manager();
            savedManager.setId(resultSet.getLong("id"));
            savedManager.setFirstName(resultSet.getString("first_name"));
            savedManager.setLastName(resultSet.getString("last_name"));
            savedManager.setEmail(resultSet.getString("email"));
            savedManager.setPhoneNumber(resultSet.getString("phone_number"));
            savedManager.setSalary(Double.valueOf(resultSet.getString("salary").replaceAll("[^\\d\\.]+", "")));
            savedManager.setDob(resultSet.getDate("dob").toLocalDate());
            savedManager.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());

            return Optional.of(savedManager);
        } catch (Exception e) {
            Log.error(this.getClass().getSimpleName() + " ManagerDaoImpl()", e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return Optional.empty();
    }


    @Override
    public Optional <Manager> findById(Long id) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Manager manager = null;

        try {
            Log.info(this.getClass().getSimpleName() + " findById(" + id + ")", Connection.class.getSimpleName(), "Establishing connection");
            connection = getConnection();

            String readQuery = "SELECT * FROM tb_managers WHERE id = ?";

            preparedStatement = connection.prepareStatement(readQuery);
            preparedStatement.setLong(1, id);

            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            manager = new Manager();
            manager.setId(resultSet.getLong("id"));
            manager.setFirstName(resultSet.getString("first_name"));
            manager.setLastName(resultSet.getString("last_name"));
            manager.setEmail(resultSet.getString("email"));
            manager.setPhoneNumber(resultSet.getString("phone_number"));
            manager.setSalary(Double.valueOf(resultSet.getString("salary").replaceAll("[^\\d\\.]", "")));
            manager.setDob(resultSet.getDate("dob").toLocalDate());
            manager.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());

            return Optional.of(manager);
        } catch (Exception e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return Optional.empty(); //manager
    }

    @Override
    public List<Manager> findAll() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Manager> managers = new ArrayList<>();

        try {
            Log.info(this.getClass().getSimpleName() + " findAll()", Connection.class.getSimpleName(), "Establishing connection");
            connection = getConnection();

            String readQuery = "SELECT * FROM tb_managers;";

            preparedStatement = connection.prepareStatement(readQuery);

            resultSet = preparedStatement.executeQuery();

            for (int i = 0; i <= managers.size() && resultSet.next(); i++) {
                Manager manager = new Manager();
                manager.setId(resultSet.getLong("id"));
                manager.setFirstName(resultSet.getString("first_name"));
                manager.setLastName(resultSet.getString("last_name"));
                manager.setEmail(resultSet.getString("email"));
                manager.setPhoneNumber(resultSet.getString("phone_number"));
                manager.setSalary(Double.valueOf(resultSet.getString("salary").replaceAll("[^\\d\\.]", "      ")));
                manager.setDob(resultSet.getDate("dob").toLocalDate());
                manager.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());
                managers.add(manager);
            }
            return managers;
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

    @Override
    public List <Manager> saveAll(List<Manager> managers){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            Log.info(this.getClass().getSimpleName() , Connection.class.getSimpleName() , "Establishig connection");
            connection = getConnection();


            String insertQuery ="INSERT INTO tb_students(" +
                    "first_name , last_name , email , phone_number , salary, dob , date_created ) " +
                    "VALUES (? , ? , ? , ? , ? , ?, ?)";

            for (int i = 0; i < managers.size(); i++) {


                preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, String.valueOf(managers.get(0)));
                preparedStatement.setString(2, String.valueOf(managers.get(1)));
                preparedStatement.setString(3, String.valueOf(managers.get(2)));
                preparedStatement.setString(4, String.valueOf(managers.get(3)));
                preparedStatement.setString(5,String.valueOf(managers.get(4)));
                preparedStatement.setDate(6, Date.valueOf(String.valueOf(managers.get(5))));
                preparedStatement.setTimestamp(7, Timestamp.valueOf(String.valueOf(managers.get(6))));

                preparedStatement.execute();

            }
            close(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection);
        }

        return null;
    }
}
