/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydbimpl;

import java.util.List;
import uga.csx370.mydb.Predicate;
import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;
import uga.csx370.mydb.RA;

public class Driver {
    
    public static void main(String[] args) {
        // Following is an example of how to use the relation class.
        // This creates a table with three columns with below mentioned
        // column names and data types.
        // After creating the table, data is loaded from a CSV file.
        // Path should be replaced with a correct file path for a compatible
        // CSV file.
        Relation rel1 = new RelationBuilder()
                .attributeNames(List.of("ID", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        rel1.loadData("/var/lib/mysql-files/instructor_export.csv");
        //rel1.print();

        Relation rel2 = new RelationBuilder()
                .attributeNames(List.of("ID", "name", "dept_name", "tot_cred"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        rel2.loadData("/var/lib/mysql-files/student_export.csv");
        //rel2.print();

        Relation course = new RelationBuilder()
            .attributeNames(List.of("course_id", "course_title", "course_dept_name", "course_credits"))
            .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.INTEGER))
            .build();
        course.loadData("/var/lib/mysql-files/course_export.csv");

        Relation teaches = new RelationBuilder()
            .attributeNames(List.of("teaches_ID", "teaches_course_id", "teaches_sec_id", "teaches_semester", "teaches_year"))
            .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.STRING, Type.INTEGER))
            .build();
        teaches.loadData("/var/lib/mysql-files/teaches_export.csv");


        System.out.println("Find the course ID and title of courses taught by instructors with a salary greater than 100,000.");
        RA ra = new RAImpl();
        
        int instrIdIdx = rel1.getAttrIndex("ID");
        int teachesIdIdx = rel1.getAttrs().size() + teaches.getAttrIndex("teaches_ID");
        Predicate instrTeachesPred = row -> row.get(instrIdIdx).equals(row.get(teachesIdIdx));
        // theta join
        Relation instrTeaches = ra.join(rel1, teaches, instrTeachesPred);
        int teachesCourseIdx = instrTeaches.getAttrIndex("teaches_course_id");
        int courseIdIdx = instrTeaches.getAttrs().size() + course.getAttrIndex("course_id");
        Predicate teachesCoursePred = row -> row.get(teachesCourseIdx).equals(row.get(courseIdIdx));
        // theta join
        Relation instrTeachesCourse = ra.join(instrTeaches, course, teachesCoursePred);
        int salaryIdx = instrTeachesCourse.getAttrIndex("salary");
        Predicate highSalary = row -> row.get(salaryIdx).getAsDouble() > 100000;
        // select
        Relation filtered = ra.select(instrTeachesCourse, highSalary);
        // project
        Relation answer = ra.project(filtered, List.of("course_id", "course_title"));
        answer.print();
    }

}
