package ed.inf.adbs.minibase.operator.common;

import ed.inf.adbs.minibase.base.Term;

import java.util.List;

public class Schema {
    private final List<Term> attributeNames;
    private final List<String> attributeTypes;

    public Schema(List<Term> attributeNames, List<String> attributesTypes) {
        this.attributeNames = attributeNames;
        this.attributeTypes = attributesTypes;
    }

    public List<Term> getAttributeNames() {
        return this.attributeNames;
    }

    public List<String> getAttributeTypes() {
        return this.attributeTypes;
    }
}
