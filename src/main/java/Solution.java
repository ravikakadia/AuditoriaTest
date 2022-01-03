import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.text.*;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

public class Solution {
    final static String taxId = "123-23-3456";
    final static String newTaxId = "222-33-4444";
    final static String taxType = "ITIN";
    final static String gmailUserId = "ravi.kakadia@gmail.com";
    static String current = System.getProperty("user.dir");
    final static File file = new File(current + "\\src\\main\\resources\\SampleFile.rtf");
    final static String dirToZip = current + "\\src\\main\\java";


    public static void main(String args[]) throws IOException, BadLocationException, javax.mail.MessagingException, GeneralSecurityException, MessagingException {
        //1.	Open and parse the file SampelFile.rtf
        //2.	Replace the values of these two fields, in all their occurrences :
        //Tax Identification:
        //Type of Tax Identification Number:
        //With new values  222-33-4444 and ITIN
        parseAndUpdateFile();

        FileOutputStream fos = new FileOutputStream("dirCompressed.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(dirToZip);
        zipSourceCode(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
        //3.	Use either GMail or O365-Email API in your code and  send the updated document as attachment from your GMail
        sendGmailMessage();
    }

    public static void parseAndUpdateFile() throws IOException, BadLocationException {
        RTFEditorKit kit = new RTFEditorKit();
        Document doc = kit.createDefaultDocument();

        InputStream is = Solution.class.getResourceAsStream("/SampleFile.rtf");
        kit.read(is, doc, 0);
        int len = doc.getLength();

        String fileAsString = doc.getText(0, len);

        MutableAttributeSet mutableAttributeSet = new SimpleAttributeSet();
        StyleConstants.setItalic(mutableAttributeSet, true);

        List<Integer> taxIdIndexes = new ArrayList<Integer>();
        int index = 0;
        while (index != -1) {
            index = fileAsString.indexOf(taxId, index);
            if (index != -1) {
                taxIdIndexes.add(index);
                index++;
            }
        }
        List<Integer> taxTypeIndexes = new ArrayList<Integer>();
        index = 0;
        while (index != -1) {
            index = fileAsString.indexOf(taxType, index);
            if (index != -1) {
                taxTypeIndexes.add(index);
                index++;
            }
        }
        //Replace with new strings
        for (int i : taxIdIndexes) {
            //System.out.println(i);
            doc.remove(i, taxId.length());
            doc.insertString(i, newTaxId, mutableAttributeSet);
        }
        for (int i : taxTypeIndexes) {
            //System.out.println(i);
            doc.remove(i, taxType.length());
            doc.insertString(i, taxType, mutableAttributeSet);
        }

        //Write updated text in file
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        kit.write(baos, doc, doc.getStartPosition().getOffset(), doc.getLength());
        baos.close();

        String rtfContent = baos.toString();
        //String current = System.getProperty("user.dir");
       // final File file = new File(current + "\\src\\main\\resources\\SampleFile.rtf");
        final FileOutputStream fos = new FileOutputStream(file);
        fos.write(rtfContent.getBytes());
        fos.close();
    }

    public static void sendGmailMessage() throws IOException, GeneralSecurityException, MessagingException, javax.mail.MessagingException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, GmailService.JSON_FACTORY, GmailService.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(GmailService.APPLICATION_NAME)
                .build();

        GmailService.sendMessage(service, gmailUserId);

    }

    public static void zipSourceCode(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {

        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipSourceCode(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
