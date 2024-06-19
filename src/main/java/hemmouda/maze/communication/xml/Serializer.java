package hemmouda.maze.communication.xml;

import de.fhac.mazenet.server.generated.LoginMessageData;
import de.fhac.mazenet.server.generated.MazeCom;

import javax.xml.bind.*;
import java.io.StringReader;
import java.io.StringWriter;

public class Serializer {

    private JAXBContext jc;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public Serializer() {
        try {
            this.jc = JAXBContext.newInstance(MazeCom.class);
            this.marshaller = jc.createMarshaller();
            this.unmarshaller = jc.createUnmarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialisiert das Objekt in die XML-Repraesentation.
     *
     * @param message Object das serialisiert werden soll
     * @return die XML-Repraesentation des Objekts als String
     * @see StringWriter, Marshaller.marshall()
     */
    public String messageToXMLString(MazeCom message) throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(message, stringWriter);
        return stringWriter.toString();
    }


    /**
     * Deserialisiert von einem XML-String ein Objekt aus dem JAXBContext
     *
     * @param xml XML-Repraesentation eines EchoMessage-Objekts
     * @return EchoMessage-Object
     * @throws JAXBException
     * @see StringReader, Unmarshaller.unmarshall()
     */
    public MazeCom xmlStringToMessage(String xml) throws JAXBException {
        StringReader stringReader = new StringReader(xml);
        return (MazeCom) unmarshaller.unmarshal(stringReader);
    }



}

