package core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringPrintWriter extends PrintWriter {
    public StringPrintWriter() {
        super(new StringWriter());
    }

    public StringPrintWriter(int initialSize) {
        super(new StringWriter(initialSize));
    }

    public String getString() {
        flush();
        return ((StringWriter) this.out).toString();
    }

    @Override
    public String toString() {
        return getString();
    }

    /**
     * ��ȡ�쳣�Ķ�ջ�ַ���
     *
     * @param e
     * @return
     */
    public static String getExceptionString(Exception e) {
        StringPrintWriter spw = new StringPrintWriter();
        e.printStackTrace(spw);
        return spw.getString();
    }

}
