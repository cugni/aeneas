 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.workloader.controller;

 import es.bsc.cassandrabm.codegenerator.query.annotations.Input;
 import es.bsc.cassandrabm.commons.CUtils;
 import es.bsc.cassandrabm.workloader.controller.Client.Distribution;
 import es.bsc.cassandrabm.workloader.distributions.*;

 import java.util.ArrayList;
 import java.util.logging.Level;
 import java.util.logging.Logger;

/**
 *
 * @author cesare
 */
public class MethodInput {

    private static final Logger log = Logger.getLogger(MethodInput.class.getSimpleName());
    private final Generator generator;
    private final RangeGenerator rangeGen;
    private final String name;
 

    MethodInput(Input iv, Distribution distribution) {
        this.name = iv.name();
        log.log(Level.INFO, "Creating input value {0}", name);
        boolean uniformrange = CUtils.getBoolean("uniformrange", true);
        Generator tmp;
        Number wide;
        if (uniformrange && iv.interval()) {
            generator = null;

            if (iv.type().equals(Integer.class)) {
                tmp = new UniformIntegerGenerator(Integer.parseInt(iv.from()), Integer.parseInt(iv.to()));
                wide = iv.wideMax().isEmpty() ? null : Integer.parseInt(iv.wideMax());
            } else if (iv.type().equals(Long.class)) {
                wide = iv.wideMax().isEmpty() ? null : Long.parseLong(iv.wideMax());
                tmp = new UniformLongGenerator(Long.parseLong(iv.from()), Long.parseLong(iv.to()));

            } else {
                throw new IllegalArgumentException("Unsupported Type:" + iv.type().getName());
            }
            rangeGen = new NumberRangeGenerator((NumberGenerator) tmp, wide);

        } else {

            if ((distribution.equals(Distribution.CONSTANT) || distribution.equals(Distribution.SEQUENTIAL))
                    && iv.interval()) {
                generator = null;
                if (distribution.equals(Distribution.CONSTANT)) {
                    Number f;
                    Number t;
                    if (iv.type().equals(Integer.class)) {
                        f = Integer.parseInt(iv.from());
                        t = Integer.parseInt(iv.to());
                        wide = iv.wideMax().isEmpty() ? null : Integer.parseInt(iv.wideMax());
                    } else if (iv.type().equals(Long.class)) {
                        f = Long.parseLong(iv.from());
                        t = Long.parseLong(iv.to());
                        wide = iv.wideMax().isEmpty() ? null : Long.parseLong(iv.wideMax());
                    } else if (iv.type().equals(Double.class)) {
                        f = Double.parseDouble(iv.from());
                        t = Double.parseDouble(iv.to());
                        wide = iv.wideMax().isEmpty() ? null : Double.parseDouble(iv.wideMax());

                    } else {
                        throw new IllegalArgumentException("Unsupported Type:" + iv.type().getName());
                    }

                    rangeGen = new CostantRangeNumberGenerator(f, t);
                    log.log(Level.INFO, "Using ConstantRangeNumberGenerator");
                } else {
                    Number f;
                    Number t;

                    Number step = null;
                    if (iv.type().equals(Integer.class)) {
                        f = Integer.parseInt(iv.from());
                        t = Integer.parseInt(iv.to());
                        wide = iv.wideMax().isEmpty() ? null : Integer.parseInt(iv.wideMax());
                    } else if (iv.type().equals(Long.class)) {
                        f = Long.parseLong(iv.from());
                        t = Long.parseLong(iv.to());
                        wide = iv.wideMax().isEmpty() ? null : Long.parseLong(iv.wideMax());
                    } else if (iv.type().equals(Double.class)) {
                        f = Double.parseDouble(iv.from());
                        t = Double.parseDouble(iv.to());
                        step = Double.parseDouble(iv.step());
                        wide = iv.wideMax().isEmpty() ? null : Double.parseDouble(iv.wideMax());
                    } else {
                        throw new IllegalArgumentException("Unsupported Type:" + iv.type().getName());
                    }

                    rangeGen = new SequenceRangeNumberGenerator(f, t, step);
                    log.log(Level.INFO, "Using SequenceRangeNumberGenerator");
                }

            } else {
                if (iv.type().equals(Integer.class)) {
                    wide = iv.wideMax().isEmpty() ? null : Integer.parseInt(iv.wideMax());
                    switch (distribution) {
                        case CONSTANT:
                            tmp = new ConstantIntegerGenerator(Integer.parseInt(iv.from()));
                            break;
                        case SEQUENTIAL:
                            tmp = new SequenceIntegerGenerator(Integer.parseInt(iv.from()),
                                    Integer.parseInt(iv.to()));

                            break;
                        case UNIFORM:
                            tmp = new UniformIntegerGenerator(Integer.parseInt(iv.from()),
                                    Integer.parseInt(iv.to()));
                            break;
                        case ZIPFIAN:
                            tmp = new ZipfianIntegerGenerator(Integer.parseInt(iv.from()),
                                    Integer.parseInt(iv.to()));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown constant value");
                    }

                } else if (iv.type().equals(Long.class)) {

                    wide = iv.wideMax().isEmpty() ? null : Long.parseLong(iv.wideMax());
                    switch (distribution) {
                        case CONSTANT:
                            tmp = new ConstantLongGenerator(Long.parseLong(iv.from()));
                            break;
                        case SEQUENTIAL:
                            tmp = new SequenceLongGenerator(Long.parseLong(iv.from()),
                                    Long.parseLong(iv.to()));

                            break;
                        case UNIFORM:
                            tmp = new UniformLongGenerator(Long.parseLong(iv.from()),
                                    Long.parseLong(iv.to()));
                            break;
                        case ZIPFIAN:
                            tmp = new ZipfianLongGenerator(Long.parseLong(iv.from()),
                                    Long.parseLong(iv.to()));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown constant value");
                    }
                } else if (iv.type().equals(Double.class)) {

                    wide = iv.wideMax().isEmpty() ? null : Double.parseDouble(iv.wideMax());
                    switch (distribution) {
                        case CONSTANT:
                            tmp = new ConstantDoubleGenerator(Double.parseDouble(iv.from()));
                            break;
                        case SEQUENTIAL:
                            tmp = new SequenceDoubleGenerator(Double.parseDouble(iv.from()),
                                    Double.parseDouble(iv.to()), Double.parseDouble(iv.step()));

                            break;
                        case UNIFORM:
                            tmp = new UniformDoubleGenerator(Double.parseDouble(iv.from()),
                                    Double.parseDouble(iv.to()));
                            break;
                        case ZIPFIAN:
                            tmp = new ZipfianDoubleGenerator(Double.parseDouble(iv.from()),
                                    Double.parseDouble(iv.to()), Double.parseDouble(iv.step()));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown constant value");
                    }
                } else {
                    throw new UnsupportedOperationException("Not yet implemented. Only Integer or Long parameters");
                }

                if (iv.interval()) {
                    if (Number.class.isAssignableFrom(iv.type())) {
                        generator = null;
                        rangeGen = new NumberRangeGenerator((NumberGenerator) tmp, wide);

                    } else {
                        throw new UnsupportedOperationException("Not yet implemented. Only Integer or Long ranges");

                    }
                    log.log(Level.INFO, "Using interval generator {0}", rangeGen.getClass().getSimpleName());
                } else {
                    generator = tmp;
                    rangeGen = null;
                    log.log(Level.INFO, "Using generator {0}", generator.getClass().getSimpleName());
                }
            }
        }
    }

    public boolean isRange() {
        return generator == null;
    }

    void setInput(ArrayList inputsValue) {
        if (isRange()) {
            Range nextRange = rangeGen.getNextRange();
            inputsValue.add(nextRange.getFrom());
            inputsValue.add(nextRange.getTo());

        } else {
            inputsValue.add(generator.next());
        }
    }
}
