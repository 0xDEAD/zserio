package array_types;

import static org.junit.Assert.*;

import org.junit.Test;

import array_types.arrays_mapping.ArraysMapping;
import array_types.arrays_mapping.TestStructure;
import array_types.arrays_mapping.TestEnum;

import zserio.runtime.array.BigIntegerArray;
import zserio.runtime.array.BoolArray;
import zserio.runtime.array.ByteArray;
import zserio.runtime.array.FloatArray;
import zserio.runtime.array.IntArray;
import zserio.runtime.array.LongArray;
import zserio.runtime.array.ObjectArray;
import zserio.runtime.array.ShortArray;
import zserio.runtime.array.StringArray;
import zserio.runtime.array.UnsignedByteArray;
import zserio.runtime.array.UnsignedIntArray;
import zserio.runtime.array.UnsignedLongArray;
import zserio.runtime.array.UnsignedShortArray;
import zserio.runtime.array.VarInt16Array;
import zserio.runtime.array.VarInt32Array;
import zserio.runtime.array.VarInt64Array;
import zserio.runtime.array.VarUInt16Array;
import zserio.runtime.array.VarUInt32Array;
import zserio.runtime.array.VarUInt64Array;

public class ArraysMappingTest
{
    @Test
    public void unsignedIntegerArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setUint8Array(new UnsignedByteArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setUint16Array(new UnsignedShortArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setUint32Array(new UnsignedIntArray(FIXED_ARRAY_LENGTH));

        // just do something with arraysMapping not to have FindBugs warning
        final BigIntegerArray bigIntegerArray = new BigIntegerArray(EMPTY_ARRAY_LENGTH);
        arraysMapping.setUint64Array(bigIntegerArray);
        assertEquals(bigIntegerArray, arraysMapping.getUint64Array());
    }

    @Test
    public void signedIntegerArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setInt8Array(new ByteArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setInt16Array(new ShortArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setInt32Array(new IntArray(FIXED_ARRAY_LENGTH));

        // just do something with arraysMapping not to have FindBugs warning
        final LongArray longArray = new LongArray(FIXED_ARRAY_LENGTH);
        arraysMapping.setInt64Array(longArray);
        assertEquals(longArray, arraysMapping.getInt64Array());
    }

    @Test
    public void unsignedBitfieldArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setBitfield8Array(new UnsignedByteArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setBitfield16Array(new UnsignedShortArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setBitfield32Array(new UnsignedIntArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setBitfield63Array(new UnsignedLongArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setVariableBitfieldLongArray(new UnsignedLongArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setVariableBitfieldIntArray(new UnsignedIntArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setVariableBitfieldShortArray(new UnsignedShortArray(FIXED_ARRAY_LENGTH));

        // just do something with arraysMapping not to have FindBugs warning
        final UnsignedByteArray unsignedByteArray = new UnsignedByteArray(FIXED_ARRAY_LENGTH);
        arraysMapping.setVariableBitfieldByteArray(unsignedByteArray);
        assertEquals(unsignedByteArray, arraysMapping.getVariableBitfieldByteArray());
    }

    @Test
    public void signedBitfieldArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setIntfield8Array(new ByteArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setIntfield16Array(new ShortArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setIntfield32Array(new IntArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setIntfield64Array(new LongArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setVariableIntfieldLongArray(new LongArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setVariableIntfieldIntArray(new IntArray(FIXED_ARRAY_LENGTH));
        arraysMapping.setVariableIntfieldShortArray(new ShortArray(FIXED_ARRAY_LENGTH));

        // just do something with arraysMapping not to have FindBugs warning
        final ByteArray byteArray = new ByteArray(FIXED_ARRAY_LENGTH);
        arraysMapping.setVariableIntfieldByteArray(byteArray);
        assertEquals(byteArray, arraysMapping.getVariableIntfieldByteArray());
    }

    @Test
    public void floatArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        // just do something with arraysMapping not to have FindBugs warning
        final FloatArray floatArray = new FloatArray(FIXED_ARRAY_LENGTH);
        arraysMapping.setFloat16Array(floatArray);
        assertEquals(floatArray, arraysMapping.getFloat16Array());
    }

    @Test
    public void variableUnsignedIntegerArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setVaruint16Array(new VarUInt16Array(FIXED_ARRAY_LENGTH));
        arraysMapping.setVaruint32Array(new VarUInt32Array(FIXED_ARRAY_LENGTH));

        // just do something with arraysMapping not to have FindBugs warning
        final VarUInt64Array varUInt64Array = new VarUInt64Array(FIXED_ARRAY_LENGTH);
        arraysMapping.setVaruint64Array(varUInt64Array);
        assertEquals(varUInt64Array, arraysMapping.getVaruint64Array());
    }

    @Test
    public void variableSignedIntegerArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setVarint16Array(new VarInt16Array(FIXED_ARRAY_LENGTH));
        arraysMapping.setVarint32Array(new VarInt32Array(FIXED_ARRAY_LENGTH));

        // just do something with arraysMapping not to have FindBugs warning
        final VarInt64Array varInt64Array = new VarInt64Array(FIXED_ARRAY_LENGTH);
        arraysMapping.setVarint64Array(varInt64Array);
        assertEquals(varInt64Array, arraysMapping.getVarint64Array());
    }

    @Test
    public void boolArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        // just do something with arraysMapping not to have FindBugs warning
        final BoolArray boolArray = new BoolArray(FIXED_ARRAY_LENGTH);
        arraysMapping.setBoolArray(boolArray);
        assertEquals(boolArray, arraysMapping.getBoolArray());
    }

    @Test
    public void stringArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();
        final StringArray stringArray = new StringArray(FIXED_ARRAY_LENGTH);
        arraysMapping.setStringArray(stringArray);
        assertEquals(FIXED_ARRAY_LENGTH, arraysMapping.getStringArray().length());
    }

    @Test
    public void compoundArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        // just do something with arraysMapping not to have FindBugs warning
        final ObjectArray<TestStructure> objectArray = new ObjectArray<TestStructure>(FIXED_ARRAY_LENGTH);
        arraysMapping.setCompoundArray(objectArray);
        assertEquals(objectArray, arraysMapping.getCompoundArray());
    }

    @Test
    public void enumArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        // just do something with arraysMapping not to have FindBugs warning
        final ObjectArray<TestEnum> objectArray = new ObjectArray<TestEnum>(FIXED_ARRAY_LENGTH);
        arraysMapping.setEnumArray(objectArray);
        assertEquals(objectArray, arraysMapping.getEnumArray());
    }

    private static final int FIXED_ARRAY_LENGTH = 5;
    private static final int EMPTY_ARRAY_LENGTH = 0;
}
