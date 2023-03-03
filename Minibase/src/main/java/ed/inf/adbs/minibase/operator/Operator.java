package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.operator.common.Tuple;

/**
 * interface of Operator
 */

public abstract class Operator {
    public abstract Tuple getNextTuple();
    public abstract void reset();
    public abstract void dump();
}
