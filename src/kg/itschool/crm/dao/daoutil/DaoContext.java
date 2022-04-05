package kg.itschool.crm.dao.daoutil;

import kg.itschool.crm.dao.*;
import kg.itschool.crm.dao.impl.*;

public abstract class DaoContext {
    static {
        try {
        System.out.println("Loading driver...");
        Class.forName("org.postgresql.Driver");
        System.out.println("Driver loaded.");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver loading failed");
            e.printStackTrace();
        }
    }

    private static ManagerDao managerDao;
    private static MentorDao mentorDao;

    public static CrudDao<?> autowired(String qualifier, String scope) {
        if (!scope.equals("singleton") && !scope.equals("prototype")) {
            throw new RuntimeException("Invalid scope of bean " + scope);
        }
        switch (qualifier) {
            case "ManagerDao":
                return getManagerDaoSQL(scope);
            case "MentorDao":
                return getMentorDaoSQL(scope);
            default:
                throw new RuntimeException("Can not find bean for autowiring: " + qualifier);
        }
    }

    private static ManagerDao getManagerDaoSQL(String scope) {
        if (scope.equals("prototype")) {
            return new ManagerDaoImpl();
        }
        if (managerDao == null) {
            managerDao = new ManagerDaoImpl();
        }
        return managerDao;
    }

    private static MentorDao getMentorDaoSQL(String scope) {
        if (scope.equals("prototype")) {
            return new MentorDaoImpl();
        }
        if (mentorDao == null) {
            mentorDao = new MentorDaoImpl();
        }
        return mentorDao;
    }

    private static StudentDao getStudentDaoSQL() {
        return new StudentDaoImpl();
    }

    private static CourseDao getCourseDaoSQL() {
        return new CourseDaoImpl();
    }

    private static GroupDao getGroupDaoSQL() {
        return new GroupDaoImpl();
    }

//    private static AddressDao getAddressDaoSQL() {
//        return new AddressDaoImpl();
    }

