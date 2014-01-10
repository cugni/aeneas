/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent.controllers;

import es.bsc.aeneas.nodeagent.repositories.AETestsRepository;
import es.bsc.aeneas.nodeagent.model.AETest;
import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author ccugnasc
 */
@Controller()
@RequestMapping("q/tests")
public class TestsController {

    @Inject
    AETestsRepository AErep;

   
    @RequestMapping(method = RequestMethod.GET, value = "/")
    public @ResponseBody
    List<AETest> getTests(@RequestParam(value = "context", required = false) String context,
            @RequestParam(value = "from", required = false) String from) throws Exception {
        return AErep.getTests(context, from);
    }
}
