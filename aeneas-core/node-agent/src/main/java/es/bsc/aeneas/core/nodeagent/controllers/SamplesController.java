/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent.controllers;

import es.bsc.aeneas.core.nodeagent.repositories.SamplesRepository;
import es.bsc.aeneas.core.nodeagent.model.Samples;
import javax.inject.Inject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author ccugnasc
 */
@Controller()
@RequestMapping("q/samples")
public class SamplesController {

    @Inject
    private SamplesRepository samplesR;

    @RequestMapping(method = RequestMethod.GET, value = "/{testname}/{context}.{metric}/{node}")
    public @ResponseBody
    Samples getSamples(
            @PathVariable String test,
            @PathVariable String context,
            @PathVariable String metric,
            @PathVariable String node,
            @RequestParam(value = "from", required = false) Long from,
            @RequestParam(value = "nresult", required = false) Integer nresult) throws Exception {
        if (from == null) {
            return samplesR.getSamples(test, context, metric, node);
        } else {
            return samplesR.getSamples(test, context, metric, node, from, nresult);
        }
    }
   
    @RequestMapping(method = RequestMethod.GET, value = "/{testname}/{context}.{metric}/{node}/first")
    public @ResponseBody
    Samples getFirstSamples(
            @PathVariable String test,
            @PathVariable String context,
            @PathVariable String metric,
            @PathVariable String node,
            @RequestParam(value = "from", required = false) Long from) throws Exception {
        if (from == null) {
            return samplesR.getFistSamples(test, context, metric, node);
        } else {
            return samplesR.getFistSamples(test, context, metric, node, from);
        }
    }
      @RequestMapping(method = RequestMethod.GET, value = "/{testname}/{context}.{metric}/{node}/last")
    public @ResponseBody
    Samples getLastSamples(
            @PathVariable String test,
            @PathVariable String context,
            @PathVariable String metric,
            @PathVariable String node,
            @RequestParam(value = "from", required = false) Long from) throws Exception {
        if (from == null) {
            return samplesR.getLastSamples(test, context, metric, node);
        } else {
            return samplesR.getLastSamples(test, context, metric, node, from);
        }
    }
}
