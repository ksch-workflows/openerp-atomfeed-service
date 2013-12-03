package org.bahmni.feed.openerp.worker;

import org.bahmni.feed.openerp.client.OpenMRSWebClient;
import org.bahmni.openerp.web.client.OpenERPClient;
import org.ict4h.atomfeed.client.service.EventWorker;

public class WorkerFactory {
    private OpenMRSWebClient webClient;

    public WorkerFactory(OpenMRSWebClient webClient){
        this.webClient = webClient;
    }

    public EventWorker getWorker(String jobName, String feedUrl, OpenERPClient openERPClient,
                                 String urlPrefix) {
        if (jobName.equals("openerp.customer.service"))
            return new OpenERPCustomerServiceEventWorker(feedUrl, openERPClient, webClient, urlPrefix);

        if (jobName.equals("openerp.saleorder.service"))
            return new OpenERPSaleOrderEventWorker(feedUrl, openERPClient, webClient, urlPrefix);

        throw new RuntimeException(String.format("No worker for %s", jobName));
    }
}
