package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.ZserioType;
import zserio.ast.FunctionType;
import zserio.emit.common.ExpressionFormatter;

public final class CompoundFunctionTemplateData
{
    public CompoundFunctionTemplateData(JavaNativeTypeMapper javaNativeTypeMapper, CompoundType compoundType,
                                        ExpressionFormatter javaExpressionFormatter)
    {
        compoundFunctionList = new ArrayList<CompoundFunction>();
        final Iterable<FunctionType> compoundFunctionTypeList = compoundType.getFunctions();
        for (FunctionType compoundFunctionType : compoundFunctionTypeList)
            compoundFunctionList.add(new CompoundFunction(javaNativeTypeMapper, compoundFunctionType,
                                                          javaExpressionFormatter));
    }

    public Iterable<CompoundFunction> getList()
    {
        return compoundFunctionList;
    }

    public static class CompoundFunction
    {
        public CompoundFunction(JavaNativeTypeMapper javaNativeTypeMapper, FunctionType functionType,
                                ExpressionFormatter javaExpressionFormatter)
        {
            final ZserioType returnZserioType = functionType.getReturnType();
            returnTypeName = javaNativeTypeMapper.getJavaType(returnZserioType).getFullName();
            name = functionType.getName();
            resultExpression = javaExpressionFormatter.formatGetter(functionType.getResultExpression());
        }

        public String getReturnTypeName()
        {
            return returnTypeName;
        }

        public String getName()
        {
            return name;
        }

        public String getResultExpression()
        {
            return resultExpression;
        }

        private final String returnTypeName;
        private final String name;
        private final String resultExpression;
    }

    private final List<CompoundFunction>    compoundFunctionList;
}
