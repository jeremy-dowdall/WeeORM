package fm.strength.worm.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fm.strength.sloppyj.Jay;
import fm.strength.worm.data.Value;

import static fm.strength.worm.ObjectBuilder.P;
import static fm.strength.worm.ObjectBuilder.Q;

public class TestHelper {

    public static final String[] ID_COLS = { Q, P };


    public static Cursor cursor(String[] header, String[]...rows) {
        if(header.length == rows[0].length - 3) {
            final String[] dst = Arrays.copyOf(ID_COLS, header.length + 3);
            System.arraycopy(header, 0, dst, 3, header.length);
            header = dst;
        }
        MatrixCursor mc = new MatrixCursor(header);
        for(String[] row : rows) {
            mc.addRow(row);
        }
        return mc;
    }

    public static Cursor cursor(Cursor...cursors) {
        return new MergeCursor(cursors);
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    public static Cursor cursor(String...cursors) {
        Cursor[] ca = new Cursor[cursors.length];
        for(int i = 0; i < ca.length; i++) {
            Map<String, Object> c = Jay.get(cursors[i]).asMap();
            List<?> h = (List<?>) c.get("h");
            List<?> r = (List<?>) c.get("r");

            String[] header = h.toArray(new String[h.size()]);
            String[][] rows = new String[r.size()][];
            for(int j = 0; j < rows.length; j++) {
                List<?> row = (List<?>) r.get(j);
                rows[j] = new String[row.size()];
                for(int k = 0; k < row.size(); k++) {
                    Object val = row.get(k);
                    rows[j][k] = (val != null) ? val.toString() : null;
                }
            }

            ca[i] = cursor(header, rows);
        }
        return cursor(ca);
    }

    public static String[] row(String...columns) {
        return columns;
    }

    public static Value[] values(String json, Object...args) {
        Map<String, Object> obj = Jay.get(json).withArgs(args).asMap();
        int i = 0;
        Value[] values = new Value[obj.size()];
        for(Map.Entry<String, Object> entry : obj.entrySet()) {
            values[i++] = new Value(entry.getKey(), entry.getValue().toString());
        }
        return values;
    }

}
