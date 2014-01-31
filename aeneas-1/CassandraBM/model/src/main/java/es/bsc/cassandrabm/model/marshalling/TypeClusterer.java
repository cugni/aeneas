/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.model.marshalling;

import java.util.List;

/**
 *
 * @author cesare
 */
public abstract class TypeClusterer<T> {
     public void setGrouping(String from,String to,Integer intervals){
         T f=parseFromString(from);
         T t=parseFromString(to);
         setGrouping(f,t,intervals);
     }
    public abstract void setGrouping(T from,T to,Integer intervals);
    public abstract T getGroup(T value);
     public  T getGroupGeneric(Object value){
         return getGroup((T) value);
     }
    public abstract List<T> getGroupsInterval(T from,T to);
     public List<T> getGroupsIntervalGeneric(Object from,Object to){
         return getGroupsInterval((T)from,(T)to);
     }
    
    public abstract T parseFromString(String s);
    
}
