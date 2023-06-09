package ru.schedule.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.schedule.models.Classes;
import ru.schedule.models.Classrooms;
import ru.schedule.models.Courses;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class ClassroomsDAOTest {

    @Autowired
    private ClassroomsDAO classroomsDAO;
    @Autowired
    private ClassesDAO classesDAO;
    @Autowired
    private CoursesDAO coursesDAO;
    @Autowired
    private SessionFactory sessionFactory;


    @Test
    void testFree() {
        List<Classrooms> free = classroomsDAO.get_free((short) 1, (short) 5);
        assertEquals(3, free.size());
    }

    @Test
    void testSchedule() {
        List<Classes> schedule3 = classroomsDAO.get_schedule( (long) 527, (short) 1, (short) 5);
        assertEquals(2, schedule3.size());
        assertEquals(3L, schedule3.get(1).getId());
    }
    @Test
    void testAdd() {
        List<Classrooms> free1 = classroomsDAO.get_free( (short) 1, (short) 5);
        classroomsDAO.add(new Classrooms(4L, 614L, 20));
        List<Classrooms> free2 = classroomsDAO.get_free( (short) 1, (short) 5);
        assertEquals(1, free2.size() - free1.size());
    }
    @BeforeEach
    void beforeEach() {
        List<Courses> courseList = new ArrayList<>();
        courseList.add(new Courses(1L, "matan", "stream", (short) 3, (short) 1));
        courseList.add(new Courses(2L, "prak", "group", (short) 1, (short) 3));
        courseList.add(new Courses(3L, "linal", "special", (short) 2, (short) 2));
        courseList.add(new Courses(4L, "oki", "stream", (short) 3, null));
        coursesDAO.saveCollection(courseList);

        List<Classrooms> classroomList = new ArrayList<>();
        classroomList.add(new Classrooms(1L, 106L, 150));
        classroomList.add(new Classrooms(2L, 666L, 30));
        classroomList.add(new Classrooms(3L, 527L, 50));
        classroomsDAO.saveCollection(classroomList);

        List<Classes> classesList = new ArrayList<>();
        classesList.add(new Classes(123L, 1L, 1L, "16:20:00", (short) 1));
        classesList.add(new Classes(null, 2L, 2L, "08:45:00", (short) 2));
        classesList.add(new Classes(1L, 3L, 3L, "14:35:00", (short) 3));
        classesList.add(new Classes(124L, 1L, 3L, "10:30:00", (short) 4));
        classesDAO.saveCollection(classesList);
    }

    @BeforeAll
    @AfterEach
    void annihilation() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createSQLQuery("TRUNCATE classes RESTART IDENTITY CASCADE;");
            session.createSQLQuery("TRUNCATE courses RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("TRUNCATE classrooms RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("ALTER SEQUENCE courses_id_seq RESTART WITH 1;").executeUpdate();
            session.createSQLQuery("ALTER SEQUENCE classes_id_seq RESTART WITH 1;").executeUpdate();
            session.createSQLQuery("ALTER SEQUENCE classrooms_id_seq RESTART WITH 1;").executeUpdate();
            session.getTransaction().commit();
        }
    }
}
