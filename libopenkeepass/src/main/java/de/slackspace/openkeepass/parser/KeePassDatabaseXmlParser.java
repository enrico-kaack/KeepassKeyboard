package de.slackspace.openkeepass.parser;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXB;

import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.History;
import de.slackspace.openkeepass.domain.KeePassFile;

public class KeePassDatabaseXmlParser {

    public KeePassFile fromXml(InputStream inputStream) {
        Serializer serializer = new Persister();
        try {
            KeePassFile keePassFile = serializer.read(KeePassFile.class, inputStream);
            return keePassFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }



    }

    public ByteArrayOutputStream toXml(KeePassFile keePassFile) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JAXB.marshal(keePassFile, outputStream);

        return outputStream;
    }
}
