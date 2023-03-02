package ed.inf.adbs.minibase.base.operator;

import ed.inf.adbs.minibase.base.Tuple;

/**
 * interface of Operator
 */

public abstract class Operator {
    public abstract Tuple getNextTuple();
    public abstract void reset();
    public abstract void dump();
}
