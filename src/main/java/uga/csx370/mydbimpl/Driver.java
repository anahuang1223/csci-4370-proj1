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

        Relation instructor = new RelationBuilder()
                .attributeNames(List.of("instructor_id", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        instructor.loadData("C:/Users/leann/Desktop/Database Management/mysql-files/" + "instructor_export.csv");

        Relation teaches = new RelationBuilder()
                .attributeNames(List.of("instr_id", "c_id", "sec_id", "semester", "year"))
                .attributeTypes(List.of(Type.INTEGER, Type.INTEGER, Type.INTEGER, Type.STRING, Type.INTEGER))
                .build();
        teaches.loadData("C:/Users/leann/Desktop/Database Management/mysql-files/" + "teaches_export.csv");

        Relation course = new RelationBuilder()
                .attributeNames(List.of("course_id", "title", "dept_name", "credits"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        course.loadData("C:/Users/leann/Desktop/Database Management/mysql-files/" + "course_export.csv");


        // Query #3: Find the course ID and title of any Computer Science course that has been taught by an instructor, along with the ID and names of those instructors.
                // Some Comp. Sci. courses listed in the course table were never taught by any instructor, so those will not be shown.
        System.out.println(
        "\nQuery #3 (Leanne): Find the course ID and title of any Computer Science course that has been taught by an instructor, along with the ID and names of those instructors.\n");
        
        RA q3 = new RAImpl();
        
        // Select rows where course.dept_name = Comp. Sci.
        Predicate isCompSci = row -> row.get(2).getAsString().equals("Comp. Sci.");
        Relation selectCompSciCourses = q3.select(course, isCompSci);

        // Select rows where course.course_id = teaches.course_id
        Predicate teachesCourse = row -> row.get(0).getAsInt() == row.get(5).getAsInt();
        Relation joinCompSciTeaches = q3.join(selectCompSciCourses, teaches, teachesCourse);

        // Show course_id, title, instr_id for each course
        Relation teachesCompSci = q3.project(joinCompSciTeaches, List.of("course_id", "title", "instr_id"));

        // Join with instructor to get instructor name
        Predicate isCourseInstr = row -> row.get(2).getAsInt() == row.get(3).getAsInt();
        Relation instrCompSci = q3.join(teachesCompSci, instructor, isCourseInstr);
        
        // Show course_id, title, instr_id, instructor.name
        Relation course_instr_info = q3.project(instrCompSci, List.of("course_id", "title", "instr_id", "name"));
        course_instr_info.print();

    }


}
