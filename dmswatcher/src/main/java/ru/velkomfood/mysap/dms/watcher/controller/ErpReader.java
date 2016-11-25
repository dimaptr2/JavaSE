package ru.velkomfood.mysap.dms.watcher.controller;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.mysap.dms.watcher.model.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by dpetrov on 23.06.16.
 */

// RFC FM in SAP "Z_FM_DMS_READ_STATUS"

public class ErpReader {

    static final String DEST_NAME = "DEST1";
    private static ErpReader instance;
    private List<DMSDocument> documents;
    private List<DMSDetails> details;

    private JCoDestination jCoDestination;

    static {

        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "XXXXXX");
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "XX");
        connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, "XXX");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "XXX");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, "XXXXXXXX");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "XXXXXXXX");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "XX");
        createDestinationDataFile(DEST_NAME, connectProperties);

    }

    static void createDestinationDataFile(String destName, Properties props) {

        File destCfg = new File(destName + ".jcoDestination");

        if (!destCfg.isDirectory() && destCfg.exists()) {

            return;

        } else {

            try {

                FileOutputStream fos = new FileOutputStream(destCfg, false);
                props.store(fos, "PRD system");
                fos.close();

            } catch (Exception e) {

                throw new RuntimeException("Unable to create the destination file", e);

            }
        }

    }

    // For singleton's creation
    private ErpReader() {
        documents = new ArrayList<>();
        details = new ArrayList<>();
    }

    // Singleton
    public static ErpReader getInstance() {

        if (instance == null) {
            instance = new ErpReader();
        }

        return instance;
    }

    public boolean isAlive() {

        if (instance == null)
            return false;
        else
            return true;

    }

    public void initJcoDestionation() throws JCoException {
        jCoDestination = JCoDestinationManager.getDestination(DEST_NAME);
    }

    public void readSapData() throws JCoException {

        JCoFunction dmsLock, dmsUnlock, bapiDmsList;
        JCoTable itabDocList;

        if (jCoDestination != null && jCoDestination.isValid()) {

            try {

                JCoContext.begin(jCoDestination);

                dmsLock = jCoDestination.getRepository().getFunction("BAPI_DOCUMENT_ENQUEUE");
                if (dmsLock == null)
                    throw new RuntimeException("Cannot find the function BAPI_DOCUMENT_DEQUEUE");

                dmsUnlock = jCoDestination.getRepository().getFunction("BAPI_DOCUMENT_DEQUEUE");
                if (dmsUnlock == null)
                    throw new RuntimeException("Cannot find the function BAPI_DOCUMENT_DEQUEUE");

                bapiDmsList = jCoDestination.getRepository().getFunction("BAPI_DOCUMENT_GETLIST");
                if (bapiDmsList == null)
                    throw new RuntimeException("Cannot find the function BAPI_DOCUMENT_GETLIST");

                bapiDmsList.getImportParameterList().setValue("DOCUMENTTYPE", "ZDO");
                bapiDmsList.getImportParameterList().setValue("LANGUAGE", "RU");
                bapiDmsList.getImportParameterList().setValue("STATUSINTERN", "DB");
                bapiDmsList.execute(jCoDestination);

                itabDocList = bapiDmsList.getTableParameterList().getTable("DOCUMENTLIST");

                if (!documents.isEmpty()) documents.clear();

                for (int j = 0; j < itabDocList.getNumRows(); j++) {
                    itabDocList.setRow(j);

                    dmsLock.getImportParameterList().
                            setValue("DOCUMENTTYPE", itabDocList.getString("DOCUMENTTYPE"));
                    dmsLock.getImportParameterList().
                            setValue("DOCUMENTNUMBER", itabDocList.getString("DOCUMENTNUMBER"));
                    dmsLock.getImportParameterList().setValue("DOCUMENTPART",
                            itabDocList.getString("DOCUMENTPART"));
                    dmsLock.getImportParameterList().setValue("DOCUMENTVERSION",
                            itabDocList.getString("DOCUMENTVERSION"));
                    dmsLock.execute(jCoDestination);

                    DMSDocument dmsDocument = new DMSDocument();
                    dmsDocument.setDocumentType(itabDocList.getString("DOCUMENTTYPE"));
                    dmsDocument.setDocumentNumber(itabDocList.getString("DOCUMENTNUMBER"));
                    dmsDocument.setDocumentPart(itabDocList.getString("DOCUMENTPART"));
                    dmsDocument.setDocumentVersion(itabDocList.getString("DOCUMENTVERSION"));
                    documents.add(dmsDocument);

                    dmsUnlock.getImportParameterList().
                            setValue("DOCUMENTTYPE", itabDocList.getString("DOCUMENTTYPE"));
                    dmsUnlock.getImportParameterList().
                            setValue("DOCUMENTNUMBER", itabDocList.getString("DOCUMENTNUMBER"));
                    dmsUnlock.getImportParameterList().setValue("DOCUMENTPART",
                            itabDocList.getString("DOCUMENTPART"));
                    dmsUnlock.getImportParameterList().setValue("DOCUMENTVERSION",
                            itabDocList.getString("DOCUMENTVERSION"));
                    dmsUnlock.execute(jCoDestination);
                }

            } finally {
                JCoContext.end(jCoDestination);
            } // calls the group of BAPIs

            }else{

                System.out.println("Cannot connect to SAP");

            } // Jco destination is valid

    }  // read SAP data

    public void readDmsDetails() throws JCoException {

        if (!documents.isEmpty()) {

            if (!details.isEmpty()) details.clear();

            documents.forEach(doc -> {
                try {

                    JCoFunction rfcDoc = jCoDestination.getRepository().getFunction("Z_FM_DMS_SINGLE_READ");
                    rfcDoc.getImportParameterList().setValue("I_DOKAR", doc.getDocumentType());
                    rfcDoc.getImportParameterList().setValue("I_DOKNR", doc.getDocumentNumber());
                    rfcDoc.getTableParameterList().setActive("T_EMAILS", false);
                    rfcDoc.getTableParameterList().setActive("T_DEADLINE", true);
                    rfcDoc.execute(jCoDestination);
                    JCoTable itab = rfcDoc.getTableParameterList().getTable("T_DEADLINE");
                    if (itab.getNumRows() > 0) {
                        itab.firstRow();
                        do {
                            boolean flag = false;
                            String deadline = "";
                            if (itab.getString("STATUS01").equals("")
                                    && !itab.getString("DEADL01").equals("0000-00-00")) {
                                flag = true;
                                deadline = itab.getString("DEADL01");
                            }
                            if (itab.getString("STATUS02").equals("")
                                    && !itab.getString("DEADL02").equals("0000-00-00")) {
                                flag = true;
                                deadline = itab.getString("DEADL02");
                            }
                            if (itab.getString("STATUS03").equals("")
                                    && !itab.getString("DEADL03").equals("0000-00-00")) {
                                flag = true;
                                deadline = itab.getString("DEADL03");
                            }
                            if (itab.getString("STATUS04").equals("")
                                    && !itab.getString("DEADL04").equals("0000-00-00")) {
                                flag = true;
                                deadline = itab.getString("DEADL04");
                            }
                            if (itab.getString("STATUS05").equals("")
                                    && !itab.getString("DEADL05").equals("0000-00-00")) {
                                flag = true;
                                deadline = itab.getString("DEADL05");
                            }
                            if (flag) {
                                DMSDetails dmsDetails = new DMSDetails();
                                dmsDetails.setDocumentNumber(itab.getString("DOKNR"));
                                dmsDetails.setDescription(itab.getString("DESCRIPTION"));
                                dmsDetails.setUserName(itab.getString("USERNAME"));
                                dmsDetails.setDeadline(deadline);
                                dmsDetails.setVendor(itab.getString("LIFNR"));
                                dmsDetails.setVendorName(itab.getString("LNAME"));
                                dmsDetails.setContract(itab.getString("CONTRACT"));
                                details.add(dmsDetails);
                            }
                        } while (itab.nextRow());
                    }
                } catch (JCoException jcoe) {
                    jcoe.printStackTrace();
                }
            });

        }

    }

    public void clearDmsList() {
        if (!documents.isEmpty()) documents.clear();
    }
    // setters and getters

    public List<DMSDocument> getDocuments() {
        return documents;
    }

    public List<DMSDetails> getDetails() {
        return details;
    }

    // Send emails
    public void sendEmail() {

        String from = "johndoe@yahoo.com";
//        String to = "dpetrov@velkomfood.ru";
        String host = "srv-mail.eatmeat.ru";
        String email = buildEmail();

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            List<String> rp = readRecipients();
            for (String to: rp) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }
            message.setSubject("Истёк срок исполнения обязательств", "UTF-8");
            message.setContentLanguage(new String[] {"RU", "EN"});
            message.setHeader("Content-Type", "text/html; charset=UTF-8");
//            message.setText(email, "UTF-8");
            message.setContent(email, "text/html; charset=UTF-8");
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException me) {
            me.printStackTrace();
        }

    }

    // Build an email with the contracts
    private String buildEmail() {

        Date now = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder letter = new StringBuilder(0);
        Iterator<DMSDetails> it = details.iterator();

        try {

            now = fmt.parse(fmt.format(now));

            letter.append("<table border=\"1\">\r\n");
            letter.append("<tr>\r\n");
            letter.append("<th>№ ДМС</th>\r\n");
            letter.append("<th>Описание</th>\r\n");
            letter.append("<th>Срок исполнения</th>\r\n");
            letter.append("<th>Поставщик</th>\r\n");
            letter.append("<th>Наименование поставщика</th>\r\n");
            letter.append("<th>Контракт</th>\r\n");
            letter.append("</tr>\r\n");
            while (it.hasNext()) {
                DMSDetails row = it.next();
                Date deadline = fmt.parse(row.getDeadline());
                if (deadline.before(now)) {
                    letter.append("<tr>\r\n");
                    letter.append("<td>\r\n");
                    letter.append(row.getDocumentNumber().replaceFirst("^0*", ""));
                    letter.append("</td>\r\n");
                    letter.append("<td>\r\n");
                    letter.append(row.getDescription());
                    letter.append("</td>\r\n");
                    letter.append("<td>\r\n");
                    String[] sArr = row.getDeadline().split("-");
                    String s = sArr[2] + "." + sArr[1] + "." + sArr[0];
                    letter.append(s);
                    letter.append("</td>\r\n");
                    letter.append("<td>\r\n");
                    letter.append(row.getVendor().replaceFirst("^0*", ""));
                    letter.append("</td>\r\n");
                    letter.append("<td>\r\n");
                    letter.append(row.getVendorName());
                    letter.append("</td>\r\n");
                    letter.append("<td>\r\n");
                    letter.append(row.getContract());
                    letter.append("</td>\r\n");
                    letter.append("</tr>\r\n");
                }
            }
            letter.append("</table>\r\n");

        } catch (ParseException fme) {
            fme.printStackTrace();
        }

        return letter.toString();
    }

    // Read data about recipients
    private List<String> readRecipients() {

        List<String> recipients = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get("./recipients.txt"))) {
            stream.forEach(line -> {
                recipients.add(line);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipients;
    }

}
