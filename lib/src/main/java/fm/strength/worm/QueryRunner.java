package fm.strength.worm;

import android.database.Cursor;
import android.database.MergeCursor;

import java.util.ArrayList;
import java.util.List;

import fm.strength.worm.SqlBuilder.Statement;

class QueryRunner {

    public static Cursor run(ContractContentProvider contentProvider, Query query) {
        QueryRunner runner = new QueryRunner(contentProvider);
        return runner.run(query);
    }


    private final ContractContentProvider provider;
    private List<Cursor> cursors;

    QueryRunner(ContractContentProvider provider) {
        this.provider = provider;
    }

    private Cursor run(Query query) {
        cursors = new ArrayList<Cursor>();

        Statement stmt = SqlBuilder.create()
                .withQuery(query)
        .buildSql();

        Cursor cursor = provider.rawQuery(stmt.sql, stmt.args);
        cursors.add(cursor);

        processIncludes(query, cursor);

        if(cursors.size() == 1) {
            return cursors.get(0);
        } else {
            return new MergeCursor(cursors.toArray(new Cursor[cursors.size()]));
        }
    }

    private void processIncludes(Query query, Cursor cursor) {
        if(query.includes != null && cursor.moveToFirst()) {
            for(Query include : query.includes) {
                long[] ids = getIds(cursor, include);
                Statement stmt = SqlBuilder.create()
                        .withCount(cursors.size())
                        .withParents(ids)
                        .withQuery(include)
                .buildSql();

                Cursor includeCursor = provider.rawQuery(stmt.sql, stmt.args);
                cursors.add(includeCursor);

                processIncludes(include, includeCursor);
            }
        }
    }

    private long[] getIds(Cursor cursor, Query include) {
        int ix = cursor.getColumnIndex("_"+include.parentIx+"__id");
        if(ix == -1) {
            ix = cursor.getColumnIndex("_id"); // TODO
        }
        long[] ids = new long[cursor.getCount()];
        for(int i = 0; i < ids.length; i++) {
            cursor.moveToPosition(i);
            ids[i] = cursor.getLong(ix);
        }
        return ids;
    }

}
