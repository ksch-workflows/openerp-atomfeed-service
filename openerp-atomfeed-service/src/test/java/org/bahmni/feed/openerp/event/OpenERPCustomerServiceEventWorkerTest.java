package org.bahmni.feed.openerp.event;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import org.bahmni.feed.openerp.client.OpenMRSWebClient;
import org.bahmni.feed.openerp.worker.OpenERPCustomerServiceEventWorker;
import org.bahmni.openerp.web.client.OpenERPClient;
import org.bahmni.openerp.web.request.OpenERPRequest;
import org.bahmni.openerp.web.request.builder.Parameter;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

public class OpenERPCustomerServiceEventWorkerTest {
    private OpenERPClient openERPClient;
    private OpenMRSWebClient mockWebClient;
    private String MRSURLPrefix;

    @Before
    public void setUp() throws Exception {
        openERPClient = mock(OpenERPClient.class);
        mockWebClient = mock(OpenMRSWebClient.class);
    }

    @Test
    public void shouldCallOpenERPClientWithRightParameters() throws FileNotFoundException {
        MRSURLPrefix = "urlPrefixTest";
        OpenERPCustomerServiceEventWorker customerServiceEventWorker =
                new OpenERPCustomerServiceEventWorker("www.openmrs.com", openERPClient, mockWebClient, MRSURLPrefix);

        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("patientResource.xml");
        String patientResource = new Scanner(resourceAsStream).useDelimiter("\\Z").next();

        when(mockWebClient.get(any(URI.class))).thenReturn(patientResource);

        Event event = new Event(createEntry(),"www.openmrs.com");
        customerServiceEventWorker.process(event);

        verify(openERPClient).execute(createOpenERPRequest(event));

    }

    private OpenERPRequest createOpenERPRequest(Event event) {
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(createParameter("name","mareez naam","string"));
        parameters.add(createParameter("ref","GAN200066","string"));
        parameters.add(createParameter("village", "Ganiyari", "string"));
        parameters.add(createParameter("category", "create.customer", "string"));

        parameters.add(createParameter("feed_uri", "www.openmrs.com", "string"));
        parameters.add(createParameter("last_read_entry_id", event.getId(), "string"));
        parameters.add(createParameter("feed_uri_for_last_read_entry", event.getFeedUri(), "string"));


        return new OpenERPRequest("atom.event.worker","process_event",parameters);
    }

    private Entry createEntry() throws FileNotFoundException {
        Entry entry = new Entry();
        ArrayList<Content> contents = new ArrayList<Content>();
        Content content = new Content();
        content.setValue(String.format("%s%s%s", "<![CDATA[", getMRSURI(), "]]>"));
        contents.add(content);
        entry.setContents(contents);

        return entry;
    }

    private String getMRSURI() {
        return "/openmrs/ws/rest/v1/patient/d6729333-bc31-4886-a864-0a6e7ae570a9?v=full";
    }

    private Parameter createParameter(String name, String value, String type) {
        return new Parameter(name, value, type);
    }

}