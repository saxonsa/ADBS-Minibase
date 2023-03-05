package ed.inf.adbs.minibase.operator.db;

import ed.inf.adbs.minibase.base.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tuple {
    private final List<Constant> attributes;;

    public Tuple(List<Constant> attributes) {
        this.attributes = attributes;
    }

    public List<Constant> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return this.attributes.stream().map(Constant::toString).collect(Collectors.joining(", "));
    }
}