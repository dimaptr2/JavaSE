package ru.velkomfood.mysap.service.agent;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class Binder {

    private boolean success = true;
    private SAPManager sapManager = SAPManager.getInstance();
    private DBEngine dbEngine = DBEngine.getInstance();

    public boolean isSuccess() {
        return success;
    }

    public void send() {

    }

    public String receive() {
        StringBuilder sb = new StringBuilder(0);
        return sb.toString();
    }


    public void pushInvoice(long invoiceId) {

    }

    public void pushMaterials() {

    }

    public void pushCustomers() {

    }

}
