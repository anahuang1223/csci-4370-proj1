package uga.csx370.mydbimpl;

import java.util.List;

import uga.csx370.mydb.Predicate;
import uga.csx370.mydb.RA;
import uga.csx370.mydb.Relation;

public class RAImpl implements RA {

    @Override
    public Relation select(Relation rel, Predicate p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'select'");
    }

    @Override
    public Relation project(Relation rel, List<String> attrs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'project'");
    }

    @Override
    public Relation union(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        int r1size = rel1.getSize();
        int r2size = rel2.getSize();
        if (!rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations must have the same number of attributes and of the same type.");
        } 
        Relation combined = new RelationBuilder() 
                .attributeNames(rel1.getAttrs())
                .attributeTypes(rel1.getTypes())
                .build();
        for (int i=0; i<r1size; i++) {
            combined.insert(rel1.getRow(i));
        }
        for (int i=0; i<r2size; i++) {
            boolean dupe = false;
            List<Cell> cur = rel2.getRow(i);
            for (int j=0; j<r1size; j++) {
                if (rel1.getRow(j).equals(cur)) {
                    dupe = true;
                }
            }
            if (!dupe) {
                combined.insert(rel2.getRow(i));
            }
        }
        return combined;
    }

    @Override
    public Relation intersect(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        int r1size = rel1.getSize();
        int r2size = rel2.getSize();
        if (!rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations must have the same number of attributes and of the same type.");
        } 
        Relation combined = new RelationBuilder() 
                .attributeNames(rel1.getAttrs())
                .attributeTypes(rel1.getTypes())
                .build();
        for (int i=0; i<r2size; i++) {
            boolean dupe = false;
            List<Cell> cur = rel2.getRow(i);
            for (int j=0; j<r1size; j++) {
                if (rel1.getRow(j).equals(cur)) {
                    dupe = true;
                }
            }
            if (dupe) {
                combined.insert(rel2.getRow(i));
            }
        }
        return combined;
    }

    @Override
    public Relation diff(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        int r1size = rel1.getSize();
        int r2size = rel2.getSize();
        if (!rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations must have the same number of attributes and of the same type.");
        } 
        Relation combined = new RelationBuilder() 
                .attributeNames(rel1.getAttrs())
                .attributeTypes(rel1.getTypes())
                .build();
        for (int i=0; i<r1size; i++) {
            boolean dupe = false;
            List<Cell> cur = rel1.getRow(i);
            for (int j=0; j<r2size; j++) {
                if (rel2.getRow(j).equals(cur)) {
                    dupe = true;
                }
            }
            if (!dupe) {
                combined.insert(rel1.getRow(i));
            }
        }
        return combined;
    }
    
    @Override
    public Relation rename(Relation rel, List<String> origAttr, List<String> renamedAttr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rename'");
    }

    @Override
    public Relation cartesianProduct(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cartesianProduct'");
    }

    @Override
    public Relation join(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'join'");
    }

    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'join'");
    }

}
