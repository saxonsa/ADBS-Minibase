package ed.inf.adbs.minibase.operator.db;

import ed.inf.adbs.minibase.base.Constant;
import ed.inf.adbs.minibase.base.Term;

import java.util.List;

public class Schema {
    private final String relationName;

    /**
     * The type of attribute are stored in Class format
     * int: ed.inf.adbs.minibase.base.IntegerConstant
     * string: ed.inf.adbs.minibase.StringConstant
     */
    private final List<Class<? extends Constant>> attributeTypes;

    public Schema(String relationName, List<Class<? extends Constant>> attributesTypes) {
        this.relationName = relationName;
        this.attributeTypes = attributesTypes;
    }

    public String getRelationName() {
        return relationName;
    }

    public List<Class<? extends Constant>> getAttributeTypes() {
        return attributeTypes;
    }
}
