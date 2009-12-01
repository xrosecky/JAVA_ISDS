package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.entities.DocumentIdent;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author xrosecky
 */
public class MessageTableModel extends AbstractTableModel {

    private final List<List<String>> table;

    public MessageTableModel() {
        table = Collections.emptyList();
    }

    public MessageTableModel(File file, Message mess) {
        table = new ArrayList<List<String>>();
        MessageEnvelope envelope = mess.getEnvelope();
        table.add(pair("Soubor", file.getAbsolutePath()));
        table.add(pair("Jméno odesílatele", envelope.getSender().getIdentity()));
        table.add(pair("Adresa odesílatele", envelope.getSender().getAddress()));
        table.add(pair("Jméno příjemce", envelope.getRecipient().getIdentity()));
        table.add(pair("Adresa příjemce", envelope.getRecipient().getAddress()));
        table.add(pair("ID zprávy", envelope.getMessageID()));
        table.add(pair("Předmět zprávy", envelope.getAnnotation()));
        table.add(pair("Označení odesílatele", ident(envelope.getSenderIdent())));
        table.add(pair("Označení příjemce", ident(envelope.getRecipientIdent())));
        if (mess.getTimeStamp() != null) {
            table.add(pair("Otisk zprávy", mess.getTimeStamp().getHash().toString()));
        }
    }

    static private List<String> pair(String a, String b) {
        return Arrays.asList(a, b);
    }

    static private String ident(DocumentIdent ident) {
        if (ident != null && ident.getIdent() != null && ident.getRefNumber() != null) {
            return ident.getIdent() + "/" + ident.getRefNumber();
        } else {
            return "";
        }
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return table.size();
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "atribut";
            case 1:
                return "hodnota";
            default:
                throw new AssertionError("table has only two columns");
        }
    }

    public Object getValueAt(int row, int col) {
        return table.get(row).get(col);
    }
}
