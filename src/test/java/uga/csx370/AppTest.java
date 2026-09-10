package uga.csx370;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


import uga.csx370.mydb.RA;
import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;
import uga.csx370.mydbimpl.RAImpl;
import uga.csx370.mydb.Predicate;

import java.util.List;


/**
 * Unit test for simple App.
 */
public class AppTest {

    // path to MYSQL_FILES folder (do not include any .csv files)
    private String path = "C:/Users/leann/Desktop/Database Management/mysql-files/";

    /**
     * Tests SELECT method
     */
    @Test
    public void select_test() {
        boolean isSuccess = true;
        // Instr
        Relation instructor = new RelationBuilder()
                .attributeNames(List.of("id", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        instructor.loadData(path + "instructor_export.csv");

        // Student
        Relation student = new RelationBuilder()
                .attributeNames(List.of("stu_id", "name", "dept_name", "total_credits"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        student.loadData("C:/Users/leann/Desktop/Database Management/mysql-files/student_export.csv");
        
        RA ra_test = new RAImpl();
        
        // Selects rows where an instructor's dept_name is "Accounting"
        Predicate isAccounting = row -> row.get(2).getAsString().equals("Accounting");
        Relation r1 = ra_test.select(instructor, isAccounting);
        r1.print();
        if (r1.getSize() != 4) {
            System.out.println("isAccounting: FAIL");
            isSuccess = false;
        } else {
            System.out.println("isAccounting: SUCCESS");
        }

        // Selects rows where an instructor's salary is > 80000
        Predicate salary_gt_80K = row -> row.get(3).getAsDouble() > 80000;
        Relation r2 = ra_test.select(instructor, salary_gt_80K);
        r2.print();
        if (r2.getSize() != 25) {
            System.out.println("salary_gt_80K: FAIL");
            isSuccess = false;
        } else {
            System.out.println("salary_gt_80K: SUCCESS");
        }
        

        // Selects rows where a student's total_credits > 100 and dept_name is "Civil Eng."
        Predicate credits_civil_eng = row -> row.get(3).getAsInt() > 100 && row.get(2).getAsString().equals("Civil Eng.");
        Relation r3 = ra_test.select(student, credits_civil_eng);
        r3.print();
        if (r3.getSize() != 23) {
            System.out.println("credits_civil_eng: FAIL");
            isSuccess = false;
        } else {
            System.out.println("credits_civil_eng: SUCCESS");
        }
        assertTrue(isSuccess);
    }
    
    /**
     * Tests PROJECT method.
     */
    @Test 
    public void project() {
        // Instr
        Relation instructor = new RelationBuilder()
                .attributeNames(List.of("id", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        instructor.loadData(path + "instructor_export.csv");
        
        RA ra_test = new RAImpl();
        Relation r4 = ra_test.project(instructor, List.of("id", "name"));
        if (r4.hasAttr("id") && r4.hasAttr("name") && !r4.hasAttr("dept_name") && !r4.hasAttr("salary")
         && r4.getSize() == 50) {
            System.out.println("PROJECT: SUCCESS");
            assertTrue(true);
        } else {
            System.out.println("PROJECT: FAIL");
            assertTrue(false);
        }    


    }

    /**
     * Tests if project throws an exception when an attribute does not exist.
     */
    @Test
    public void project_throws_exception() {
        Relation department = new RelationBuilder()
                .attributeNames(List.of("dept_name", "dept_building", "budget"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        department.loadData(path + "department_export.csv");

        RA ra_test = new RAImpl();
        try {
            Relation r5 = ra_test.project(department, List.of("dept_name", "hubububu"));
            System.out.println("PROJECT_EXCEPTION: FAIL");
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("PROJECT_EXCEPTION: SUCCESS");
            assertTrue(true);
        }
    }

    /**
     * Example Test.
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }
}

