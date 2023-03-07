package ed.inf.adbs.minibase.base;

public class Term {
    /**
     * Compare Two terms are equal
     * @param object another Term to be compared with
     * @return equal or not
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        return object != null && getClass() == object.getClass();
    }
}
