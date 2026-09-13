package uga.csx370;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import java.util.List;
import uga.csx370.mydb.Cell;
import uga.csx370.mydb.Predicate;
import uga.csx370.mydb.RA;
import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;
import uga.csx370.mydbimpl.RAImpl;


/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Example Test.
     */
    @Test
    public void testJoinPredicate() {
        Relation rel1 = new RelationBuilder()
                .attributeNames(List.of("ID", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        rel1.loadData("/var/lib/mysql-files/instructor_export.csv");
        Relation rel2 = new RelationBuilder()
                .attributeNames(List.of("stud_ID", "stud_name", "stud_dept", "tot_cred"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        rel2.loadData("/var/lib/mysql-files/student_export.csv");

        RA test = new RAImpl();
        Predicate same_dept = row -> row.get(2).getAsString()
                .equals(row.get(6).getAsString());
        Relation result = test.join(rel1, rel2, same_dept);
        for (int i = 0; i < Math.min(10, result.getSize()); i++) {
            System.out.println(result.getRow(i));
        }
    }
}
