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

public class Driver {
    
    public static void main(String[] args) {
        // Following is an example of how to use the relation class.
        // This creates a table with three columns with below mentioned
        // column names and data types.
        // After creating the table, data is loaded from a CSV file.
        // Path should be replaced with a correct file path for a compatible
        // CSV file.
        Relation rel1 = new RelationBuilder()
                .attributeNames(List.of("Col01_Name", "Col02_Name", "Col03_Name"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.DOUBLE))
                .build();
        rel1.loadData("/path/to/exported/csv_file");
        rel1.print();

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
        //Query #2 (Leen)
        System.out.println(
            "\nQuery #2 (Leen): Get the course ID, course title, semester, and year "
            + "for courses that have prerequisites and were offered in 2008.\n"
        );

        //load course relation
        Relation course = new RelationBuilder()
                .attributeNames(List.of(
                        "course_id",
                        "title",
                        "dept_name",
                        "credits"))
                .attributeTypes(List.of(
                        Type.STRING,
                        Type.STRING,
                        Type.STRING,
                        Type.INTEGER))
                .build();

        course.loadData(
                "D:/activity02_exports/mysql-files/course_export.csv"
        );

        //load prereq relation
        Relation prereq = new RelationBuilder()
                .attributeNames(List.of(
                        "course_id",
                        "prereq_id"))
                .attributeTypes(List.of(
                        Type.STRING,
                        Type.STRING))
                .build();

        prereq.loadData(
                "D:/activity02_exports/mysql-files/prereq_export.csv"
        );

        //load section relation
        Relation section = new RelationBuilder()
                .attributeNames(List.of(
                        "course_id",
                        "sec_id",
                        "semester",
                        "year",
                        "building",
                        "room_number",
                        "time_slot_id"))
                .attributeTypes(List.of(
                        Type.STRING,
                        Type.STRING,
                        Type.STRING,
                        Type.INTEGER,
                        Type.STRING,
                        Type.STRING,
                        Type.STRING))
                .build();

        section.loadData(
                "D:/activity02_exports/mysql-files/section_export.csv"
        );

        //course join prereq
        Relation coursePrereq = engine.join(course, prereq);

        //(course join prereq) join section
        Relation coursePrereqSection = engine.join(coursePrereq, section);

        //select rows where year = 2008
        Relation courses2008 = engine.select(
                coursePrereqSection,
                row -> row.get(
                        coursePrereqSection.getAttrIndex("year")
                ).getAsInt() == 2008
        );

        //project the requested attributes
        Relation query2Result = engine.project(
                courses2008,
                List.of("course_id", "title", "semester", "year")
        );

        query2Result.print();
    }

}
