package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.operator.db.Catalog;
import org.junit.Before;

public class JoinOperatorTest {
    private final Catalog catalog = Catalog.getCatalog();

    @Before
    public void setupCatalog() {
        catalog.init("data/evaluation/db");
    }

}
