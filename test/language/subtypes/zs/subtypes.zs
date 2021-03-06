package subtypes;

subtype uint16 Identifier;

struct TestStructure
{
    Identifier  identifier;
    string      name;
};

subtype TestStructure Student;

struct SubtypeStructure
{
    Student     student;
};

sql_table TestTable
{
    int32               id
            sql "PRIMARY KEY";
    SubtypeStructure    student;
};

subtype TestTable SubtypedTable;

sql_database Database
{
    SubtypedTable students;
};
