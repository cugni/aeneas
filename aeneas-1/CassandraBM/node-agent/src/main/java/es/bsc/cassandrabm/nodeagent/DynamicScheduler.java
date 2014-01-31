/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.nodeagent;

import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * 
 * This class is a dynamic scheduler in other to could
 * change the sampling interval.
 * @author ccugnasc
 */
public class DynamicScheduler extends AbstractScheduledService.CustomScheduler {

    private AtomicLong interval = new AtomicLong(10);
    private AtomicReference<TimeUnit> timeunit = new AtomicReference(TimeUnit.SECONDS);
    /**
     * This method is called each time to determine when execute the next action.
     * @return the next scheduled time
     * @throws Exception 
     */
    @Override
    protected synchronized AbstractScheduledService.CustomScheduler.Schedule getNextSchedule() throws Exception {
        return new AbstractScheduledService.CustomScheduler.Schedule(interval.get(), timeunit.get());
    }
    /**
     * It returns the actual interval time. 
     * The method is atomic.
     * @return 
     */
    public long getInterval() {
        return interval.get();
    }
    /**
     * This method change the interval between two sampling. 
     * This method is atom. 
     * @param interval 
     */
    public void setInterval(long interval) {
        this.interval.set(interval);
    }

    /**
     * It returns the used {@link TimeUnit}
     * It is a synchronized method
     * @return 
     */
    public  TimeUnit getTimeunit() {
        return timeunit.get();
    }

    /**
     * It change the {@link TimeUnit}
     * It is a synchronized method
     * @param timeunit 
     */
    public  void setTimeunit(TimeUnit timeunit) {
        this.timeunit.set(timeunit);
    }
}
