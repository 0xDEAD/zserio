package without_writer_code;

struct Item(bool hasExtraParameter)
{
    uint16    param;
    uint32    extraParam if hasExtraParameter == true;
};

choice ItemChoice(bool hasItem) on hasItem
{
    case true:
        Item(true)  item;

    case false:
        uint16      param;
};

struct ItemChoiceHolder
{
    bool                hasItem;
    ItemChoice(hasItem) itemChoice;
};

struct Tile
{
    uint8               version;
    uint32              numElementsOffset;

numElementsOffset:
    uint32              numElements;
    ItemChoiceHolder    data[numElements];
};

enum int8 TypeEnum
{
    SIMPLE,
    COMPLEX
};

union ElementsUnion
{
    int8 elements8[];
    int16 elements16[];
    int32 elements32[];
};

sql_table GeoMapTable
{
    int32           tileId sql "PRIMARY KEY";
    Tile            tile;
};

sql_database WorldDb
{
    GeoMapTable europe;
    GeoMapTable america;
};
