/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent.model;

import javax.annotation.Resource;

/**
 *
 * @author ccugnasc
 */
@Resource
public class Workspace {
    private String name;
    private String desc;
    private Long modificationTime;
    private Long creationTime;
}