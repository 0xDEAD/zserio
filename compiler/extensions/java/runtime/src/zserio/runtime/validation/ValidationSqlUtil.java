package zserio.runtime.validation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import zserio.runtime.SqlDatabase;

/**
 * Contains SQL utilities need for validation code generated by Zserio.
 */
public class ValidationSqlUtil
{
    /**
     * Describes the table column.
     */
    public static class ColumnDescription
    {
        /**
         * Constructs table column description from all properties.
         *
         * @param name         Name of the column.
         * @param type         Column SQLite data type ("INTEGER", "REAL", "TEXT" or "BLOB").
         * @param isNotNull    true if column has "NOT NULL" constraint.
         * @param isPrimaryKey true if column is primary key.
         */
        public ColumnDescription(String name, String type, boolean isNotNull, boolean isPrimaryKey)
        {
            this.name = name;
            this.type = type;
            this.isNotNull = isNotNull;
            this.isPrimaryKey = isPrimaryKey;
        }

        /**
         * Gets the column name.
         *
         * @return Column name.
         */
        public String getName()
        {
            return name;
        }

        /**
         * Gets the column SQLite data type.
         *
         * @return Column SQLite data type ("INTEGER", "REAL", "TEXT" or "BLOB").
         */
        public String getType()
        {
            return type;
        }

        /**
         * Gets "NOT NULL" constaint flag of this column.
         *
         * @return true if column has "NOT NULL" constraint.
         */
        public boolean isNotNull()
        {
            return isNotNull;
        }

        /**
         * Gets primary key flag of this column.
         *
         * @return true if this column is primary key.
         */
        public boolean isPrimaryKey()
        {
            return isPrimaryKey;
        }

        private final String    name;
        private final String    type;
        private final boolean   isNotNull;
        private final boolean   isPrimaryKey;
    };

    /**
     * Returns a map of column names to column description for given SQLite table.
     *
     * @param db             Database to use.
     * @param attachedDbName Attached database name if table is relocated in different database or null.
     * @param tableName      Name of the table to get column types of.
     *
     * @return Map of column names to column description.
     *
     * @throws SQLException Throws in case of any SQLite error.
     */
    public static Map<String, ColumnDescription> getTableSchema(SqlDatabase db, String attachedDbName,
            String tableName) throws SQLException
    {
        Map<String, ColumnDescription> columnTypes = new HashMap<String, ColumnDescription>();

        // prepare SQL query
        final StringBuilder sqlQuery = new StringBuilder("PRAGMA ");
        if (attachedDbName != null)
        {
            sqlQuery.append(attachedDbName);
            sqlQuery.append('.');
        }
        sqlQuery.append("table_info(");
        sqlQuery.append(tableName);
        sqlQuery.append(")");

        // get table info
        final PreparedStatement statement = db.prepareStatement(sqlQuery.toString());
        try
        {
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                final String columnName = resultSet.getString(2);
                final String columnType = resultSet.getString(3);
                final boolean isNullable = resultSet.getBoolean(4);
                final int primaryKeyIndex = resultSet.getInt(6);
                columnTypes.put(columnName,
                        new ColumnDescription(columnName, columnType, isNullable, primaryKeyIndex != 0));
            }
        }
        finally
        {
            statement.close();
        }

        return columnTypes;
    }

    /**
     * Checks if hidden column exits in given SQLite table.
     *
     * @param db               Database to use.
     * @param attachedDbName   Attached database name if table is relocated in different database or null.
     * @param tableName        Name of the table where to check hidden column.
     * @param hiddenColumnName Name of hidden column to check.
     *
     * @return true if hidden column exists in given SQLite table.
     */
    public static boolean isHiddenColumnInTable(SqlDatabase db, String attachedDbName, String tableName,
            String hiddenColumnName)
    {
        // prepare SQL query
        final StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(hiddenColumnName);
        sqlQuery.append(" FROM ");
        if (attachedDbName != null)
        {
            sqlQuery.append(attachedDbName);
            sqlQuery.append('.');
        }
        sqlQuery.append(tableName);
        sqlQuery.append(" LIMIT 0");

        // try select to check if hidden column exists
        try
        {
            final PreparedStatement statement = db.prepareStatement(sqlQuery.toString());
            statement.close();
            return true;
        }
        catch (SQLException exception)
        {
            return false;
        }
    }
}
