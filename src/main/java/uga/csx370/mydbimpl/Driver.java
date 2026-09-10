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


        // Query #1: Get the titles and course ids of all Mech. Eng. (Mechanical Engineering) courses and the ids and names of all the instructors that teach it.
        System.out.println("Get the list of all Mech. Eng. (Mechanical Engineering) courses and the ids and names of all the instructors that teach it.");
       
        Relation instructor = new RelationBuilder()
                .attributeNames(List.of("instructor_id", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        instructor.loadData("C:/Users/leann/Desktop/Database Management/mysql-files/" + "instructor_export.csv");

        Relation teaches = new RelationBuilder()
                .attributeNames(List.of("instr_id", "course_id", "sec_id", "semester", "year"))
                .attributeTypes(List.of(Type.INTEGER, Type.INTEGER, Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        instructor.loadData("C:/Users/leann/Desktop/Database Management/mysql-files/" + "teaches_export.csv");

        Relation course = new RelationBuilder()
                .attributeNames(List.of("c_id", "title", "dept_name", "credits"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        instructor.loadData("C:/Users/leann/Desktop/Database Management/mysql-files/" + "course_export.csv");

        RA ra_test = new RAImpl();
        
        Predicate isMechEngCourse = row -> row.get(2).getAsString() == "Mech. Eng.";

        Relation mechEngCourses = ra_test.join(course, teaches, isMechEngCourse);
        //Relation rel1 = ra_test.select(course, mechEngCourses);
        Relation teachesMechEng = ra_test.project(mechEngCourses, List.of("course_id", "title", "instr_id"));

        Relation instrMechEng = ra_test.join(teachesMechEng, instructor);
        Relation course_instr_info = ra_test.project(instrMechEng, List.of("course_id", "title", "instr_id", "name"));
        course_instr_info.print();



    }



}
