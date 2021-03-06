package zserio.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import zserio.antlr.util.ParserException;
import zserio.ast.doc.DocCommentToken;
import zserio.tools.HashUtil;

/**
 * AST abstract node for all compound types.
 *
 * This is an abstract class for all compound Zserio types (structure types, choice types, ...).
 */
public abstract class CompoundType extends TokenAST implements ZserioType, Comparable<CompoundType>
{
    protected CompoundType()
    {
        fields = new ArrayList<Field>();
        parameters = new ArrayList<Parameter>();
        functions = new ArrayList<FunctionType>();
        usedByCompoundList = new TreeSet<CompoundType>();
        ZserioTypeContainer.add(this);
    }

    @Override
    public int compareTo(CompoundType other)
    {
        final int result = getName().compareTo(other.getName());
        if (result != 0)
            return result;

        return getPackage().getPackageName().compareTo(other.getPackage().getPackageName());
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
            return true;

        if (other instanceof CompoundType)
            return compareTo((CompoundType)other) == 0;

        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, getName());
        hash = HashUtil.hash(hash, getPackage().getPackageName());

        return hash;
    }

    @Override
    public Package getPackage()
    {
        return pkg;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Iterable<ZserioType> getUsedTypeList()
    {
        Set<ZserioType> usedTypeSet = new LinkedHashSet<ZserioType>();
        addFieldsToUsedTypeSet(usedTypeSet);

        return usedTypeSet;
    }

    /**
     * Gets the scope defined by this type.
     *
     * @return The scope defined by this type.
     */
    public Scope getScope()
    {
        return scope;
    }

    /**
     * Sets lexical scope and package for the compound type.
     *
     * This method is called by code generated by ANTLR using TypeEvaluator.g.
     *
     * @param scope Lexical scope to set.
     * @param pkg   Package to set.
     */
    public void setScope(Scope scope, Package pkg)
    {
        this.scope = scope;
        this.pkg = pkg;
    }

    /**
     * Sets compound type which uses this compound type.
     *
     * @param compoundType Compound type to set.
     *
     * @throws Throws if circular containment occures.
     */
    public void setUsedByCompoundType(CompoundType compoundType) throws ParserException
    {
        // check for circular containment  TODO This is used by expressions
        if (compoundType.isContainedIn(this))
            throw new ParserException(this, "Circular containment between '" + getName() +
                                      "' and '" + compoundType.getName() + "'!");

        usedByCompoundList.add(compoundType);
    }

    /**
     * Gets all fields associated to this compound type.
     *
     * Fields are ordered according to their definition in Zserio source file.
     *
     * @return List of fields which this compound type contains.
     */
    public List<Field> getFields()
    {
        return fields;
    }

    /**
     * Gets all parameters associated to this compound type.
     *
     * Parameters are ordered according to their definition in Zserio source file.
     *
     * @return List of parameters which this compound type contains.
     */
    public List<Parameter> getParameters()
    {
        return parameters;
    }

    /**
     * Gets all functions associated to this compound type.
     *
     * Functions are ordered according to their definition in Zserio source file.
     *
     * @return List of functions which this compound type contains.
     */
    public List<FunctionType> getFunctions()
    {
        return functions;
    }

    /**
     * Checks if this compound type contains itself as optional field.
     *
     * This is called from C++ emitter during mapping of optional fields.
     *
     * @return true if this compound type contains itself as optional field.
     */
    public boolean isRecursive()
    {
        return containsField(this, this);
    }

    /**
     * Checks if this compound type needs children initialization method.
     *
     * This is called from C++ emitter to check if the compound type has some descendant with parameters (if
     * (some descendant has initialize method).
     *
     * @return true if this compound type has some descendant with parameters.
     */
    public boolean needsChildrenInitialization()
    {
        for (Field field : fields)
        {
            final ZserioType fieldBaseType = TypeReference.resolveBaseType(field.getFieldReferencedType());
            if (fieldBaseType instanceof CompoundType)
            {
                final CompoundType childCompoundType = (CompoundType)fieldBaseType;
                // compound type can have itself as an optional field
                if (!childCompoundType.getParameters().isEmpty() ||
                        (childCompoundType != this && childCompoundType.needsChildrenInitialization()))
                    return true;
            }
        }

        return false;
    }

    /**
     * Checks if this compound type or any of its subfield contains some offset.
     *
     * @return true if this compound type contains some offset.
     */
    public boolean hasFieldWithOffset()
    {
        for (Field field : fields)
        {
            if (field.getOffsetExpr() != null)
                return true;

            final ZserioType fieldBaseType = TypeReference.resolveBaseType(field.getFieldReferencedType());
            if (fieldBaseType instanceof CompoundType)
            {
                final CompoundType childCompoundType = (CompoundType)fieldBaseType;
                // compound type can have itself as an optional field
                if (childCompoundType != this && childCompoundType.hasFieldWithOffset())
                    return true;
            }
        }

        return false;
    }

    /**
     * Gets list of compound types which use this compound type.
     *
     * @return List of compound types which use this compound type.
     */
    public Iterable<CompoundType> getUsedByCompoundList()
    {
        return usedByCompoundList;
    }

    /**
     * Checks if this compound type is contained in the given compound type 'outer'.
     *
     * @return true if this compound type is contained in compound type 'outer'
     */
    public boolean isContainedIn(CompoundType outer)
    {
        return isContainedIn(outer, new Stack<CompoundType>());
    }

    /**
     * Gets the list of referenced Zserio types of given type.
     *
     * @param clazz  Zserio type to use.
     *
     * @return List of referenced Zserio types of given type.
     */
    public <T extends ZserioType> Set<T> getReferencedTypes(Class<? extends T> clazz)
    {
        final Set<T> result = new HashSet<T>();
        for (Field field : fields)
            result.addAll(field.getReferencedTypes(clazz));
        for (FunctionType function : functions)
            result.addAll(function.getResultExpression().getReferencedSymbolObjects(clazz));

        return result;
    }

    /**
     * Gets documentation comment associated to this compound type.
     *
     * @return Documentation comment token associated to this compound type.
     */
    public DocCommentToken getDocComment()
    {
        return (docComment != null) ? docComment : getHiddenDocComment();
    }

    protected void addFieldsToUsedTypeSet(Set<ZserioType> usedTypeSet)
    {
        for (Field field : fields)
        {
            final ZserioType usedType = TypeReference.resolveType(field.getFieldReferencedType());
            if (!ZserioTypeUtil.isBuiltIn(usedType))
                usedTypeSet.add(usedType);
        }
    }

    protected void setName(String name)
    {
        this.name = name;
    }

    protected void addField(Field field)
    {
        field.setCompoundType(this);
        fields.add(field);
    }

    protected void addParameter(Parameter parameter)
    {
        parameters.add(parameter);
    }

    protected void addFunction(FunctionType function)
    {
        functions.add(function);
    }

    protected void setDocComment(DocCommentToken docComment)
    {
        this.docComment = docComment;
    }

    protected void checkTableFields() throws ParserException
    {
        // check if fields are not sql tables
        for (Field field : fields)
        {
            ZserioType fieldBaseType = TypeReference.resolveBaseType(field.getFieldReferencedType());
            if (fieldBaseType instanceof SqlTableType)
                throw new ParserException(field, "Field '" + field.getName() +
                        "' cannot be a sql table!");
        }
    }

    @Override
    protected void check() throws ParserException
    {
        // add use-by compound for subtypes needed for documentation emitter
        for (Field field : fields)
        {
            final ZserioType fieldReferencedType =
                    TypeReference.resolveType(field.getFieldReferencedType());
            if (fieldReferencedType instanceof Subtype)
                ((Subtype)fieldReferencedType).setUsedByCompound(this);
        }
        for (Parameter parameter : parameters)
        {
            final ZserioType parameterType = TypeReference.resolveType(parameter.getParameterType());
            if (parameterType instanceof Subtype)
                ((Subtype)parameterType).setUsedByCompound(this);
        }
        for (FunctionType function : functions)
        {
            final ZserioType functionType = TypeReference.resolveType(function.getReturnType());
            if (functionType instanceof Subtype)
                ((Subtype)functionType).setUsedByCompound(this);
        }
    }

    /**
     * The "is contained" relationship may contain cycles use a stack to avoid them. This is a simple DFS path
     * finding algorithm that finds a path from 'this' to 'outer'.
     */
    private boolean isContainedIn(CompoundType outer, Stack<CompoundType> seen)
    {
        if (usedByCompoundList.contains(outer))
        {
            return true;
        }

        // check whether any container of 'this' is contained in 'outer'
        for (CompoundType c : usedByCompoundList)
        {
            if (seen.search(c) == -1)
            {
                seen.push(c);
                if (c.isContainedIn(outer, seen))
                    return true;
                seen.pop();
            }
        }

        return false;
    }

    private boolean containsField(CompoundType compound, ZserioType searchedField)
    {
        for (Field field : compound.getFields())
        {
            final ZserioType fieldType = TypeReference.resolveBaseType(field.getFieldReferencedType());
            if (fieldType == searchedField)
                return true;

            if (fieldType != compound && fieldType instanceof CompoundType)
                if (containsField((CompoundType)fieldType, searchedField))
                    return true;
        }

        return false;
    }

    private Scope   scope;
    private Package pkg;

    private String  name;

    private final List<Field>               fields;
    private final List<Parameter>           parameters;
    private final List<FunctionType>        functions;

    private final SortedSet<CompoundType>   usedByCompoundList;

    private DocCommentToken                 docComment;

    private static final long serialVersionUID = -3176164167667658185L;
}
