package table_field_constraint_error;

sql_table TestTable
{
    uint32      id      sql "PRIMARY KEY";
    string      name;
    int8        wrongConstraintField : wrongConstraintField != 0;
};
