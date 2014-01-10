package es.bsc.aeneas.cassandra.mapping;

import es.bsc.aeneas.cassandra.mapping.CF;
import es.bsc.aeneas.cassandra.mapping.ColumnSort;
import es.bsc.aeneas.model.gen.*;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author cesare
 */
public class CFTest {

    private static final Logger LOG = Logger.getLogger(CFTest.class.getName());

    private ColumnFamilyType getCFT() {
        ColumnFamilyType cft = new ColumnFamilyType();

        SuperType st = new SuperType();
        Type type = rt(StandardType.LONG_TYPE);
        return cft;
    }
    
//    @Test
    public void testSimple() {

        ColumnFamilyType cft = getCFT();

        ColumnType ct = new ColumnType();
        ct.setRefPath(getRef());

        Type type = new Type();
        type.setStandardType(StandardType.LONG_TYPE);


        cft.getColumn().add(ct);
        //1 column
        assertEquals(ColumnSort.SINGLE,
                new CF(cft).columnSort);
    }

//    @Test
    public void testMultiSinle() {
        ColumnFamilyType cft = getCFT();
        {
            ColumnType ct = new ColumnType();
            ct.setRefPath(getRef());
            Type type = rt(StandardType.LONG_TYPE);


            cft.getColumn().add(ct);
        }
        //second
        {
            ColumnType ct2 = new ColumnType();
            ct2.setRefPath(getRef());


            cft.getColumn().add(ct2);
        }

        //2 column euals
        assertEquals(ColumnSort.SINGLE, new CF(cft).columnSort);
    }

//    @Test
    public void testMultiDynamic() {
        ColumnFamilyType cft = getCFT();
        {
            ColumnType ct = new ColumnType();
            ct.setRefPath(getRef());

            cft.getColumn().add(ct);
        }
        //second
        {
            ColumnType ct2 = new ColumnType();
            ct2.setRefPath(getRef());

            cft.getColumn().add(ct2);
        }
        {
            ColumnType ct3 = new ColumnType();
            ct3.setRefPath(getRef());
            Type type3 = rt(StandardType.LONG_TYPE);

            cft.getColumn().add(ct3);
        }
        //3 column not equals

        assertEquals(ColumnSort.DYNAMIC, new CF(cft).columnSort);
    }

    private Type rt(StandardType s) {
        Type t = new Type();
        t.setStandardType(s);
        return t;
    }

    private RefPathType getRef() {
        RefPathType rp = new RefPathType();
        LevelType l = new LevelType();
        l.setValue("frame");
        KeyDest kd = new KeyDest();
        kd.setPosition(0);
        l.getKeyDest().add(kd);
        rp.getLevel().add(l);
        l = new LevelType();
        l.setValue("natoms");
        rp.getLevel().add(l);
        ColumnDest com = new ColumnDest();
        com.setPosition(0);
        l.getColumnDest().add(com);
        ValueDest vom = new ValueDest();
        vom.setPosition(0);
        l.getValueDest().add(vom);

        return rp;
    }
}
