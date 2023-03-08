package ed.inf.adbs.minibase.operator.db;

import ed.inf.adbs.minibase.base.Constant;
import ed.inf.adbs.minibase.base.StringConstant;
import ed.inf.adbs.minibase.base.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Tuple {
    private final List<Constant> attributes;;

    public Tuple(List<Constant> attributes) {
        this.attributes = attributes;
    }

    public List<Constant> getAttributes() {
        return attributes;
    }

    /**
     * Transform tuples to String using to output to CSV file
     * @return format string with ", " deliminator expected to align with format of given output csv
     */
    @Override
    public String toString() {
        return this.attributes.stream().map(Constant::toString).collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple tuple = (Tuple) o;
        return attributes.equals(tuple.getAttributes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes);
    }
}
