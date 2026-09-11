package uga.csx370.mydbimpl;

import java.util.ArrayList;
import java.util.List;

import uga.csx370.mydb.Cell;
import uga.csx370.mydb.RA;
import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;

/**
 * Quick manual test harness for RAImpl. Run with:
 *   java -cp target/classes uga.csx370.mydbimpl.RATest
 */
public class RATest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        RA ra = new RAImpl();

        // --- Build a small "student" relation ---
        Relation students = new RelationBuilder()
                .attributeNames(List.of("sid", "name"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING))
                .build();
        students.insert(row(Cell.val(1), Cell.val("Ann")));
        students.insert(row(Cell.val(2), Cell.val("Bob")));

        // --- Build a small "course" relation (no shared attr names) ---
        Relation courses = new RelationBuilder()
                .attributeNames(List.of("cid", "title"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING))
                .build();
        courses.insert(row(Cell.val(10), Cell.val("DB")));
        courses.insert(row(Cell.val(20), Cell.val("OS")));
        courses.insert(row(Cell.val(30), Cell.val("AI")));

        // ================= rename =================
        Relation renamed = ra.rename(students, List.of("sid"), List.of("studentId"));
        check("rename: attr changed",
                renamed.getAttrs().equals(List.of("studentId", "name")));
        check("rename: types unchanged", renamed.getTypes().equals(students.getTypes()));
        check("rename: rows unchanged", renamed.getSize() == 2
                && renamed.getRow(0).equals(students.getRow(0)));
        check("rename: original untouched",
                students.getAttrs().equals(List.of("sid", "name")));
        checkThrows("rename: missing attr throws",
                () -> ra.rename(students, List.of("nope"), List.of("x")));
        checkThrows("rename: count mismatch throws",
                () -> ra.rename(students, List.of("sid", "name"), List.of("x")));

        // ============ cartesianProduct ============
        Relation cp = ra.cartesianProduct(students, courses);
        check("cartesianProduct: schema order",
                cp.getAttrs().equals(List.of("sid", "name", "cid", "title")));
        check("cartesianProduct: size = 2 * 3", cp.getSize() == 6);
        check("cartesianProduct: first pair is s0 + c0",
                cp.getRow(0).equals(concat(students.getRow(0), courses.getRow(0))));
        check("cartesianProduct: last pair is s1 + c2",
                cp.getRow(5).equals(concat(students.getRow(1), courses.getRow(2))));
        check("cartesianProduct: inputs untouched",
                students.getSize() == 2 && courses.getSize() == 3);
        checkThrows("cartesianProduct: common attr throws",
                () -> ra.cartesianProduct(students, students));

        System.out.println();
        cp.print();

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    static List<Cell> row(Cell... cells) {
        return new ArrayList<>(List.of(cells));
    }

    static List<Cell> concat(List<Cell> a, List<Cell> b) {
        List<Cell> r = new ArrayList<>(a);
        r.addAll(b);
        return r;
    }

    static void check(String label, boolean cond) {
        if (cond) {
            passed++;
            System.out.println("PASS  " + label);
        } else {
            failed++;
            System.out.println("FAIL  " + label);
        }
    }

    static void checkThrows(String label, Runnable r) {
        try {
            r.run();
            failed++;
            System.out.println("FAIL  " + label + " (no exception)");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("PASS  " + label);
        }
    }
}
