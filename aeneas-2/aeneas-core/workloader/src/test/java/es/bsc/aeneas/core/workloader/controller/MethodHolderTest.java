/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.workloader.controller;

import org.junit.Test;

import java.io.File;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author ccugnasc
 */
public class MethodHolderTest {

    public MethodHolderTest() {
    }

    @Test
    public void testCallBeforeRun() throws Exception {
        Random random = new Random();
        String fname = "/tmp/prova" + random.nextInt();
        MethodHolder.execProcess("touch " + fname);
        assertTrue(new File(fname).exists());
    }
}