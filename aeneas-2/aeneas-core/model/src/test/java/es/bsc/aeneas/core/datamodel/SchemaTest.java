/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.datamodel;

import es.bsc.aeneas.core.model.gen.ClusterType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.validation.SchemaFactory;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author ccugnasc
 */
public class SchemaTest {

    //@Test
    public void SchemaTest() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("es.bsc.aeneas.core.model.gen");
//            checkArgument(queryModel.exists(), "queryModel not found. {0}", queryModel.getAbsolutePath());
        Unmarshaller um = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(
                javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        um.setSchema(sf.newSchema(this.getClass().getResource("/MatchingModel.xsd")));
//        um.setProperty( XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,false);
//        um.setProperty( XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,false);
        @SuppressWarnings("unchecked")
        JAXBElement< ClusterType> jk =
                (JAXBElement<ClusterType>) um.unmarshal(this.getClass().getResource("/TestXML.xml"));
        jk.getValue();


    }
}