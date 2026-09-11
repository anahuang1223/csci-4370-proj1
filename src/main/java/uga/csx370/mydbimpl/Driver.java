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

        // joining with instructor to get access to name: JOIN [cyb_instr.i_ID=instructor.ID]
        Relation instr_cyb = engine.join(instructor, cyb_instr, row -> row.get(0).getAsString().equals(row.get(4).getAsString()));

        // projecting the ID and names of instructors who advise students in the Cybernetics department: PROJECT [ID, name]
        Relation instr_cyb_info = engine.project(instr_cyb, List.of("instr_ID", "name"));

        // intersecting instructors who taught in Fall 2004 & instructors who advise students in the Cybernetics department
        Relation instr_combined = engine.intersect(instr_f25, instr_cyb_info);

        instr_combined.print();
    }

}
