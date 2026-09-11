package uga.csx370.mydbimpl;

import java.util.ArrayList;
import java.util.List;

import uga.csx370.mydb.Cell;
import uga.csx370.mydb.Predicate;
import uga.csx370.mydb.RA;
import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;

public class RAImpl implements RA {

    @Override
    public Relation select(Relation rel, Predicate p) {
        int num_rows = rel.getSize();
        
        // Creating new relation
        Relation new_rel = new RelationBuilder()
                .attributeNames(rel.getAttrs())
                .attributeTypes(rel.getTypes())
                .build();

        // Inserts row only if it fulfills the predicate
        for (int i = 0; i < num_rows; i++) {
            if (p.check(rel.getRow(i)) == true) {
                new_rel.insert(rel.getRow(i));
            }
        }

        return new_rel;
    }

    @Override
    public Relation project(Relation rel, List<String> attrs) {
        List<Type> relTypes = rel.getTypes();
        List<Integer> AttrIndex = new ArrayList<>();
        
        // Checks if all attributes exist in the relation, if not throws IllegalArgumentException
        // Adds index of each attribute into AttrIndex list
        for (String attr : attrs) {
            if (rel.hasAttr(attr)) {
                int i = rel.getAttrIndex(attr);
                AttrIndex.add(i);
            } else {
                throw new IllegalArgumentException("Attribute does not exist: " + attr);
            }
        }
        
        int n = rel.getAttrs().size();
        
        // Removes types based on the given attrs
        for (int i = n - 1; i >= 0; i--) {
            boolean isAttr = false;
            // Check if i is in AttrIndex 
            for (int j = 0; j < AttrIndex.size(); j++) {
                if (i == AttrIndex.get(j)) {
                    isAttr = true;
                    break;
                }
            }
            if (isAttr == false) {
                relTypes.remove(i);
            }
            isAttr = false;
        }

        // Builds new relation based on the given attrs list
        Relation newRel = new RelationBuilder()
                .attributeNames(attrs)
                .attributeTypes(relTypes)
                .build();
        
        
        // Inserts rows from old relation into new one
        for (int i = 0; i < rel.getSize(); i++) {
            List<Cell> modified_row = rel.getRow(i);
            // Removes attributes that were not given in the attrs
            for (int j = n - 1; j >= 0; j--) {
                boolean isAttr = false;
                // Check if j is in AttrIndex 
                for (int k = 0; k < AttrIndex.size(); k++) {
                    if (j == AttrIndex.get(k)) {
                        isAttr = true;
                        break;
                    }
                }
                if (isAttr == false) {
                    modified_row.remove(j);
                }
                isAttr = false;
            }
            newRel.insert(modified_row);
        }

        return newRel;
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
        // origAttr and renamedAttr must line up one-to-one
        if (origAttr.size() != renamedAttr.size()) {
            throw new IllegalArgumentException("origAttr and renamedAttr must have the same number of attributes.");
        }

        // Start from the current attribute names, then swap in the new names
        List<String> newAttrs = new ArrayList<>(rel.getAttrs());
        for (int i = 0; i < origAttr.size(); i++) {
            if (!rel.hasAttr(origAttr.get(i))) {
                throw new IllegalArgumentException("Attribute does not exist: " + origAttr.get(i));
            }
            newAttrs.set(rel.getAttrIndex(origAttr.get(i)), renamedAttr.get(i));
        }

        // Same types, same rows, only the attribute names change
        Relation newRel = new RelationBuilder()
                .attributeNames(newAttrs)
                .attributeTypes(rel.getTypes())
                .build();

        for (int i = 0; i < rel.getSize(); i++) {
            newRel.insert(rel.getRow(i));
        }

        return newRel;
    }

    @Override
    public Relation cartesianProduct(Relation rel1, Relation rel2) {
        // No shared attribute names are allowed
        for (String attr : rel1.getAttrs()) {
            if (rel2.hasAttr(attr)) {
                throw new IllegalArgumentException("Relations have common attribute: " + attr);
            }
        }

        // Result schema: rel1 attributes/types followed by rel2 attributes/types
        List<String> attrs = new ArrayList<>(rel1.getAttrs());
        attrs.addAll(rel2.getAttrs());
        List<Type> types = new ArrayList<>(rel1.getTypes());
        types.addAll(rel2.getTypes());

        Relation result = new RelationBuilder()
                .attributeNames(attrs)
                .attributeTypes(types)
                .build();

        // Pair every row of rel1 with every row of rel2
        for (int i = 0; i < rel1.getSize(); i++) {
            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> row = new ArrayList<>(rel1.getRow(i));
                row.addAll(rel2.getRow(j));
                result.insert(row);
            }
        }

        return result;
    }

    @Override
    public Relation join(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'join'");
    }

    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        for (String attr : rel1.getAttrs()) {
            if (rel2.hasAttr(attr)) {
                throw new IllegalArgumentException("Relations have common attribute: " + attr);
            }
        }
        List<String> unionAttrs = new ArrayList<>(rel1.getAttrs());
        unionAttrs.addAll(rel2.getAttrs());
        List<Type> types = new ArrayList<>(rel1.getTypes());
        types.addAll(rel2.getTypes());
        Relation result = new RelationBuilder()
            .attributeNames(unionAttrs)
            .attributeTypes(types)
            .build();
        for (int i = 0; i < rel1.getSize(); i++) {
            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> row = new ArrayList<>(rel1.getRow(i));
                row.addAll(rel2.getRow(j));
                if (p.check(row)) {
                    result.insert(row);
                }
            }
        }
        return result;
    }

}
