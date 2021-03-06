package zserio.emit.common;

import antlr.collections.AST;

/**
 * An Emitter is a class that emits (i.e. generates) code while traversing
 * the Abstract Syntax Tree corresponding to a Zserio module.
 *
 * An Emitter is passed to a Tree Walker which traverses the AST and invokes
 * methods of its Emitter when entering or leaving subtrees of a given type.
 *
 * Thus, any kind of code can be generated by using different Emitter classes
 * implementing this interface together with the same Tree Walker.
 *
 * The Tree Walker class is called ZserioEmitter and should be renamed,
 * because it does not implement this interface.
 */
public interface Emitter
{
    public void beginRoot(AST r);
    public void endRoot();

    public void beginTranslationUnit(AST r, AST u);
    public void endTranslationUnit();

    public void beginPackage(AST p);
    public void endPackage(AST p);

    public void beginImport(AST i);
    public void endImport();

    public void beginConst(AST c);
    public void endConst(AST c);

    public void beginMembers();
    public void endMembers();

    public void beginField(AST f);
    public void endField(AST f);

    public void beginFunction(AST f);
    public void endFunction(AST f);

    public void beginStructure(AST s);
    public void endStructure(AST s);
    public void beginChoice(AST c);
    public void endChoice(AST c);
    public void beginUnion(AST u);
    public void endUnion(AST u);

    public void beginEnumeration(AST e);
    public void endEnumeration(AST e);
    public void beginEnumItem(AST e);
    public void endEnumItem(AST e);

    public void beginSubtype(AST s);
    public void endSubtype(AST s);

    public void beginSqlDatabase(AST s);
    public void endSqlDatabase(AST s);
    public void beginSqlTable(AST s);
    public void endSqlTable(AST s);
}
