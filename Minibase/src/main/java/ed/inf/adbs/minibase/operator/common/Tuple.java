package ed.inf.adbs.minibase.operator.common;

import java.util.ArrayList;
import java.util.List;

public class Tuple {
    private final ArrayList<Object> attributes;;
    private final Schema schema;

    /** initialize tuple
     * use schema to indicate the type of values
     * ep. values: 1, "test", 2
     * ep. this.attributes: [1, "test", 2]
     */
    public Tuple(Schema schema, String values) {
        this.schema = schema;
        attributes = new ArrayList<>();
        String valuesTrim = values.trim();
        String[] value = valuesTrim.split(", ");
        List<String> attributeTypes = schema.getAttributeTypes();
        for (int i = 0; i < attributeTypes.size(); i++) {
            if (attributeTypes.get(i).equals("int")) {
                this.attributes.add(Integer.parseInt(value[i]));
            } else { // "String"
                this.attributes.add(value[i]);
            }
        }
    }

    public ArrayList<Object> getAttributes() {
        return this.attributes;
    }

    public Schema getSchema() {
        return this.schema;
    }
}
