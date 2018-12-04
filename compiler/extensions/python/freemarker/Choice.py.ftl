<#include "FileHeader.inc.ftl"/>
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports typeImports/>
<#macro choice_selector_condition expressionList>
    <#if expressionList?size == 1>
selector == (${expressionList?first})<#rt>
    <#else>
        <#list expressionList as expression>
selector == (${expression})<#if expression?has_next> || </#if><#rt>
        </#list>
    </#if>
</#macro>
<#macro choice_if memberActionMacroName>
        selector = ${selector}
    <#list caseMemberList as caseMember>
        <#if caseMember?has_next || !isDefaultUnreachable>
        <#if caseMember?is_first>if <#else>elif </#if><@choice_selector_condition caseMember.expressionList/>:
        <#else>
        else:
        </#if>
            <@.vars[memberActionMacroName] caseMember, 3/>
    </#list>
    <#if !isDefaultUnreachable>
        else:
        <#if defaultMember??>
            <@.vars[memberActionMacroName] defaultMember, 4/>
        <#else>
            raise PythonRuntimeException("No match in choice ${name}: " + selector + "!")
        </#if>
    </#if>
</#macro>

<#assign constructorParamList><@compound_constructor_parameters compoundParametersData/></#assign>
class ${name}:
    def __init__(self, ${constructorParamList}):
        <@compound_constructor_parameter_assignments compoundParametersData/>
        self._choice = None

    @classmethod
    def fromReader(cls, reader, ${constructorParamList}):
        instance = cls(${constructorParamList})
        instance.read(reader)

        return instance

    def __eq__(self, other):
        if isinstance(other, ${name}):
            return (<@compound_compare_parameters compoundParametersData, 5/> and
                    self._choice == other._choice)

        return False

    def __hash__(self):
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
        result = zserio.hashcode.calcHashCode(result, hash(self._choice))

        return result;
<#list compoundParametersData.list as parameter>

    def ${parameter.getterName}(self):
        <@compound_parameter_accessor parameter/>
</#list>
<#list fieldList as field>

    def ${field.getterName}(self):
        return self._choice<#if field.array??>.getRawArray()</#if>
    <#if withWriterCode>

    def ${field.setterName}(self, <@field_argument_name field/>):
        <#if field.array??>
        self.<@field_member_name field/> = zserio.Array(<@array_field_constructor_parameters field/>)
        <#else>
        self.<@field_member_name field/> = <@field_argument_name field/>
        </#if>
    </#if>
</#list>

<#macro choice_bitsizeof_member member indent>
    <#if member.compoundField??>
        <@compound_bitsizeof_field member.compoundField, indent/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}pass
    </#if>
</#macro>
    def bitSizeOf(self, <#if !fieldList?has_content>_</#if>bitPosition=0):
<#if fieldList?has_content>
        endBitPosition = 0

        <@choice_if "choice_bitsizeof_member"/>

        return endBitPosition - bitPosition
<#else>
        return 0
</#if>
<#if withWriterCode>

    <#macro choice_initialize_offsets_member member indent>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#if member.compoundField??>
            <@compound_initialize_offsets_field member.compoundField, indent/>
        <#else>
            <#lt>${I}pass
        </#if>
    </#macro>
    def initializeOffsets(self, bitPosition):
    <#if fieldList?has_content>
        endBitPosition = bitPosition

        <@choice_if "choice_initialize_offsets_member"/>

        return endBitPosition
    <#else>
        return bitPosition
    </#if>
</#if>

<#macro choice_read_member member indent>
    <#if member.compoundField??>
        <@compound_read_field member.compoundField, name, indent/>
        <@compound_check_constraint_field member.compoundField, name, indent/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}pass
    </#if>
</#macro>
    def read(self, <#if !fieldList?has_content>_</#if>reader):
<#if fieldList?has_content>
        <@choice_if "choice_read_member"/>
<#else>
        pass
</#if>
<#if withWriterCode>

    <#macro choice_write_member member indent>
        <#if member.compoundField??>
            <@compound_check_constraint_field member.compoundField, name, indent/>
            <@compound_write_field member.compoundField, name, indent/>
        <#else>
            <#local I>${""?left_pad(indent * 4)}</#local>
            <#lt>${I}pass
        </#if>
    </#macro>
    def write(self, <#if fieldList?has_content>_</#if>writer, *, callInitializeOffsets=True):
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        if callInitializeOffsets:
            initializeOffsets(writer.getBitPosition())

        </#if>
        <@choice_if "choice_write_member"/>
    <#else>
        pass
    </#if>
</#if>
