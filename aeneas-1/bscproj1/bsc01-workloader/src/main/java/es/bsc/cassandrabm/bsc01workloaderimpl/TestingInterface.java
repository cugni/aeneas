/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.bsc01workloaderimpl;

import java.util.Map;

/**
 *
 * @author ccugnasc
 */
public interface TestingInterface extends QueryInterfaceImpl{

     public Map getPoints(Integer frameFrom,Integer frameTo,Integer atomFrom,Integer  atomTo ,String guagesname);
    
}
