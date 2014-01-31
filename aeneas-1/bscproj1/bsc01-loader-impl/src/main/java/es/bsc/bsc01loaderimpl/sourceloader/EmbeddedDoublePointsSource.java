package es.bsc.bsc01loaderimpl.sourceloader;

import static com.google.common.base.Preconditions.*;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import es.bsc.cassandrabm.loader.DBSetter;
import es.bsc.cassandrabm.loader.SourceReader;
import es.bsc.cassandrabm.loader.exceptions.InvalidAddRequest;
import es.bsc.cassandrabm.loader.exceptions.InvalidPutRequest;
import es.bsc.cassandrabm.loader.exceptions.PatternException;
import es.bsc.cassandrabm.loader.exceptions.SourceReadingException;
import es.bsc.cassandrabm.model.marshalling.BoxType;
import es.bsc.cassandrabm.model.marshalling.PointType;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * This source reader is equal to the EmbeddedSource one with the only exception 
 * it doesn't use PointType.
 * The Reference Model used is named compositeReferenceModel.xml
 */

public class EmbeddedDoublePointsSource extends SourceReader {

    private static Pattern frame1 = Pattern.compile("(\\S+) frame (\\d+):");
    private static Pattern frame2 = Pattern.compile("natoms= +(\\d+) +step= +(\\d+) +time=([\\d\\.e\\+\\-]+) +prec= +(\\d+)");
    private static Pattern matrix = Pattern.compile("\\s+(\\S+) +\\((\\d+)x(\\d+)\\):");
    private static Pattern row = Pattern.compile("\\s+(\\S+)\\[ *(\\d+)\\]=\\{ *([\\+\\-0-9.eE]+), *([\\+\\-0-9.eE]+), *([\\+\\-0-9.eE]+)\\}");
    InputStreamReader sr = null;
    DBSetter db = null;
    private AtomicInteger lineRead = new AtomicInteger(0);
    SourceReadingException err = null;
    public EmbeddedDoublePointsSource(){
        
        Metrics.newGauge(EmbeddedDoublePointsSource.class, "LineRead", new Gauge<Integer>() {

            @Override
            public Integer value() {
               return lineRead.get();
            }
        });
    }
    @Override
    public void open(InputStreamReader sr) {
        this.sr = sr;
     
    }

    private void enterFrame(BufferedReader bir) throws IOException, InvalidPutRequest, InvalidAddRequest, PatternException {
        String line;
        while ((line = checkNotNull(bir,"Open the source reader before use it").readLine()) != null) {
            lineRead.incrementAndGet();
            Matcher m = frame1.matcher(line);
            if (!m.find()) {
                throw new PatternException("Pattern doesn't match: " + m.toString() + "|" + line);
            }
            int frame = Integer.parseInt(m.group(2));
            line = bir.readLine();
            lineRead.incrementAndGet();
            m = frame2.matcher(line);
            if (!m.find()) {
                throw new PatternException("Pattern doesn't match: " + m.toString());
            }
            int natoms = Integer.parseInt(m.group(1));
            int step = Integer.parseInt(m.group(2));
            Long time = Double.doubleToLongBits(Double.parseDouble(m.group(3)));
            int prec = Integer.parseInt(m.group(4));
            db.put(natoms, frame, "natoms");
            db.put(step, frame, "step");
            db.put(time, frame, "time");
            db.put(prec, frame, "prec");
            //box
            line = bir.readLine();
            lineRead.incrementAndGet();
            m = matrix.matcher(line);
            if (!m.find()) {
                throw new PatternException("Pattern doesn't match: " + m.toString());
            }
            int rows = Integer.parseInt(m.group(2));
            BoxType b = new BoxType();
            for (int i = 0; i < rows; i++) {
                line = bir.readLine();
                lineRead.incrementAndGet();
                m = row.matcher(line);
                if (!m.find()) {
                    throw new PatternException("Pattern doesn't match: " + m.toString() + "|" + line);
                }
                PointType p = new PointType();
                p.setX(Double.parseDouble(m.group(3)));
                p.setY(Double.parseDouble(m.group(4)));
                p.setZ(Double.parseDouble(m.group(5)));
                b.getPoints().add(p);
            }
            db.put(b, frame, "box");
            //points
            line = bir.readLine();
            lineRead.incrementAndGet();
            m = matrix.matcher(line);
            if (!m.find()) {
                throw new PatternException("Pattern doesn't match: " + m.toString());
            }
            rows = Integer.parseInt(m.group(2));
            for (int i = 0; i < rows; i++) {
                line = bir.readLine();
                lineRead.incrementAndGet();
                m = row.matcher(line);
                if (!m.find()) {
                    throw new PatternException("Pattern doesn't match: " + m.toString() + "|" + line);
                }
                 db.put(Double.parseDouble(m.group(5)) //z
                         , frame, "points", i,
                        Double.parseDouble(m.group(3)),//x
                        Double.parseDouble(m.group(4)));//y
                        
            }
        }


    }

    @Override
    public void setDBSetter(DBSetter db) {
        this.db = db;

    }

    @Override
    public int lineRead() {
        return lineRead.get();
    }

    @Override
    public Boolean call() throws Exception {
        if (sr == null || db == null) {
            throw new SourceReadingException("Source reader non configured");
        }
        try {
            BufferedReader   bir=new BufferedReader(sr);;
            enterFrame(bir);
            db.close();
            return true;
        } catch (FileNotFoundException e) {
            throw new SourceReadingException("Source reader file non Readible", e);
        } catch (IOException e) {
            throw new SourceReadingException("IOEXception", e);
        } catch (InvalidPutRequest e) {
            throw new SourceReadingException("InvalidPutRequest", e);
        } catch (InvalidAddRequest e) {
            throw new SourceReadingException("InvalidAddRequest", e);
        } catch (PatternException e) {
            throw new SourceReadingException("PatternException", e);
        } catch (Exception e) {
            throw new SourceReadingException("GenericException", e);
        }
    }
}
