package sql_tables.without_pk_table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_tables.IParameterProvider;
import sql_tables.TestDb;

import zserio.runtime.ZserioError;
import zserio.runtime.SqlDatabase.Mode;

public class WithoutPkTableTest
{
    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp() throws IOException, URISyntaxException, SQLException
    {
        FileUtil.deleteFileIfExists(file);
        database = new TestDb(file.toString());
        database.createSchema();
    }

    @After
    public void tearDown() throws SQLException
    {
        if (database != null)
        {
            database.close();
            database = null;
        }
    }

    @Test
    public void deleteTable() throws SQLException
    {
        assertTrue(isTableInDb());

        final WithoutPkTable testTable = database.getWithoutPkTable();
        testTable.deleteTable();
        assertFalse(isTableInDb());

        testTable.createTable();
        assertTrue(isTableInDb());
    }

    @Test
    public void readWithoutCondition() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final WithoutPkTable testTable = database.getWithoutPkTable();

        final List<WithoutPkTableRow> writtenRows = new ArrayList<WithoutPkTableRow>();
        fillWithoutPkTableRows(writtenRows);
        testTable.write(writtenRows);

        final WithoutPkTableParameterProvider parameterProvider = new WithoutPkTableParameterProvider();
        final List<WithoutPkTableRow> readRows = testTable.read(parameterProvider);
        checkWithoutPkTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final WithoutPkTable testTable = database.getWithoutPkTable();

        final List<WithoutPkTableRow> writtenRows = new ArrayList<WithoutPkTableRow>();
        fillWithoutPkTableRows(writtenRows);
        testTable.write(writtenRows);

        final WithoutPkTableParameterProvider parameterProvider = new WithoutPkTableParameterProvider();
        final String condition = "name='Name1'";
        final List<WithoutPkTableRow> readRows = testTable.read(parameterProvider, condition);

        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final WithoutPkTableRow readRow = readRows.get(0);
        checkWithoutPkTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final WithoutPkTable testTable = database.getWithoutPkTable();

        final List<WithoutPkTableRow> writtenRows = new ArrayList<WithoutPkTableRow>();
        fillWithoutPkTableRows(writtenRows);
        testTable.write(writtenRows);

        final int updateRowId = 3;
        final WithoutPkTableRow updateRow = createWithoutPkTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "id=" + updateRowId;
        testTable.update(updateRow, updateCondition);

        final WithoutPkTableParameterProvider parameterProvider = new WithoutPkTableParameterProvider();
        final List<WithoutPkTableRow> readRows = testTable.read(parameterProvider, updateCondition);
        assertEquals(1, readRows.size());

        final WithoutPkTableRow readRow = readRows.get(0);
        checkWithoutPkTableRow(updateRow, readRow);
    }

    private static class WithoutPkTableParameterProvider implements IParameterProvider
    {
        @Override
        public long getComplexTable_count(ResultSet resultSet)
        {
            return 0;
        }
    }

    private static void fillWithoutPkTableRows(List<WithoutPkTableRow> rows)
    {
        for (int id = 0; id < NUM_WITHOUT_PK_TABLE_ROWS; ++id)
        {
            rows.add(createWithoutPkTableRow(id, "Name" + id));
        }
    }

    private static WithoutPkTableRow createWithoutPkTableRow(int id, String name)
    {
        final WithoutPkTableRow row = new WithoutPkTableRow();
        row.setId(id);
        row.setName(name);

        return row;
    }

    private static void checkWithoutPkTableRows(List<WithoutPkTableRow> rows1, List<WithoutPkTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkWithoutPkTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkWithoutPkTableRow(WithoutPkTableRow row1, WithoutPkTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getName(), row2.getName());
    }

    private boolean isTableInDb() throws SQLException
    {
        // check if database does contain table
        final String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME +
                "'";

        final PreparedStatement statement = database.prepareStatement(sqlQuery);
        try
        {
            final ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return false;

            // read table name
            final String tableName = resultSet.getString(1);
            if (resultSet.wasNull() || !tableName.equals(TABLE_NAME))
                return false;
        }
        finally
        {
            statement.close();
        }

        return true;
    }

    private static final String TABLE_NAME = "withoutPkTable";

    private static final int    NUM_WITHOUT_PK_TABLE_ROWS = 5;
    private static final String FILE_NAME = "without_pk_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
