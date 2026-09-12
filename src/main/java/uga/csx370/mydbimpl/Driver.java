/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydbimpl;

import java.util.List;

import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;
import uga.csx370.mydb.RA;
import uga.csx370.mydb.Predicate;

public class Driver {
    
    public static void main(String[] args) {
        // Following is an example of how to use the relation class.
        // This creates a table with three columns with below mentioned
        // column names and data types.
        // After creating the table, data is loaded from a CSV file.
        // Path should be replaced with a correct file path for a compatible
        // CSV file.

        // PATH TO MYSQL-FILES
        String path = "C:/Users/leann/Desktop/Database Management/mysql-files/";

        /**********
         * TABLES *
         **********/

        Relation instructor = new RelationBuilder()
                .attributeNames(List.of("instr_ID", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        instructor.loadData(path + "instructor_export.csv");

        Relation teaches = new RelationBuilder()
                .attributeNames(List.of("instructor_ID", "c_id", "sec_id", "semester", "year"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        teaches.loadData(path + "teaches_export.csv");

        Relation course = new RelationBuilder()
                .attributeNames(List.of("course_id", "title", "dept_name", "credits"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        course.loadData(path + "course_export.csv");

        Relation student = new RelationBuilder()
                .attributeNames(List.of("student_id", "name", "dept_name", "tot_credits"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        student.loadData(path + "student_export.csv");

        Relation advisor = new RelationBuilder()
                .attributeNames(List.of("s_ID", "i_ID"))
                .attributeTypes(List.of(Type.STRING, Type.STRING))
                .build();
        advisor.loadData(path + "advisor_export.csv");


        /***********
         * QUERIES *
         ***********/

        // Note: Sach's query does not work on my computer. Mine (Leanne's) is query #2

        // Query #1: Find the names and IDs of instructors who taught in the Fall 2004 semester AND who advise students in the Cybernetics department.
        RAImpl engine = new RAImpl();

        System.out.println("Query #1 (Sach): Find the names and IDs of instructors who taught in the Fall 2004 semester AND who advise students in the Cybernetics department.\n");

        // who taught Fall 2004: SELECT [semester="Fall" AND year=2004] (teaches)
        Relation t_f25 = engine.select(teaches, row -> row.get(3).getAsString().equals("Fall") && row.get(4).getAsInt() == 2004);

        // joining with instructor to get access to name: JOIN [instr.ID = t_f25.ID] (instructor)
        Relation i_t = engine.join(instructor, t_f25, row -> row.get(0).getAsString().equals(row.get(4).getAsString()));

        // projecting ID and name of instructors who taught in Fall 2004: PROJECT [instr_ID, name] (i_t)
        Relation instr_f25 = engine.project(i_t, List.of("instr_ID", "name"));

        // students in the Cybernetics department: SELECT [dept_name="Cybernetics"] (student)
        Relation s_cyb = engine.select(student, row -> row.get(2).getAsString().equals("Cybernetics"));

        // joining with advisor to get their instructor IDs: JOIN [s_cyb.ID=advisor.s_ID] (advisor)
        Relation cyb_adv = engine.join(advisor, s_cyb, row -> row.get(0).getAsString().equals(row.get(2).getAsString()));

        // projecting the instructor IDs: PROJECT [i_ID] (cyb_adv)
        Relation cyb_instr = engine.project(cyb_adv, List.of("i_ID"));

        // joining with instructor to get access to name: JOIN [cyb_instr.i_ID=instructor.ID] (instructor)
        Relation instr_cyb = engine.join(instructor, cyb_instr, row -> row.get(0).getAsString().equals(row.get(4).getAsString()));

        // projecting the ID and names of instructors who advise students in the Cybernetics department: PROJECT [ID, name] (instr_cyb)
        Relation instr_cyb_info = engine.project(instr_cyb, List.of("instr_ID", "name"));

        // intersecting instructors who taught in Fall 2004 & instructors who advise students in the Cybernetics department: instr_cyb_info INTERSECT instr_f25
        Relation instr_combined = engine.intersect(instr_f25, instr_cyb_info);

        instr_combined.print();




        // Query #2: Find the course ID and title of any Computer Science course that has been taught by an instructor, along with the ID and names of those instructors.
        
        
        System.out.println(
        "\nQuery #3 (Leanne): Find the course ID and title of any Comp. Sci. course that has been taught by an instructor, "
        + "along with the ID and names of those instructors.\n");
        
        RA q3 = new RAImpl();
        
        // SELECT rows where " course.dept_name = 'Comp. Sci.' "
        Predicate isCompSci = row -> row.get(2).getAsString().equals("Comp. Sci.");
        Relation selectCompSciCourses = q3.select(course, isCompSci);

        // JOIN [teaches] and [course] where " course.course_id = teaches.course_id "
        // Some CS courses listed in the [course] table are not in the [teaches] table (course was never taught by an insturctor)
        // so those will not be displayed.
        Predicate teachesCourse = row -> row.get(0).getAsString().equals(row.get(5).getAsString());
        Relation joinCompSciTeaches = q3.join(selectCompSciCourses, teaches, teachesCourse);

        // PROJECT course_id, course.title, teaches.instructor_id for each course
        Relation teachesCompSci = q3.project(joinCompSciTeaches, List.of("course_id", "title", "instructor_ID"));
        
        // JOIN with [instructor] where " teaches.instructor_ID = instructor.instr_ID ", in order to get instructor.name
        Predicate isCourseInstr = row -> row.get(2).getAsString().equals(row.get(3).getAsString());
        Relation instrCompSci = q3.join(teachesCompSci, instructor, isCourseInstr);
        
        // PROJECT course_id, course.title, instructor.instr_id, instructor.name
        Relation course_instr_info = q3.project(instrCompSci, List.of("course_id", "title", "instr_ID", "name"));
        course_instr_info.print();

    }


}
