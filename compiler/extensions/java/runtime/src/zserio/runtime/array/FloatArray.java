package zserio.runtime.array;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import zserio.runtime.ZserioError;
import zserio.runtime.Mapping;
import zserio.runtime.Util;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

/**
 * Implements Zserio zserio float16 array.
 *
 * Zserio float16 array is mapped to Java float type.
 */
public class FloatArray extends NumericArrayBase<Float>
{
    /**
     * Constructs array from bit stream.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream or IMPLICIT_LENGTH for implicit-length
     *                array or AUTO_LENGTH for auto length array.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public FloatArray(BitStreamReader reader, int length) throws IOException, ZserioError
    {
        this(reader, length, null);
    }

    /**
     * Constructs array from bit stream applying offset checking.
     *
     * @param reader  Bit stream reader to construct from.
     * @param length  Number of elements to read from given bit stream or IMPLICIT_LENGTH for implicit-length
     *                array or AUTO_LENGTH for auto length array.
     * @param checker Specifies offset checker for indexed offsets.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public FloatArray(BitStreamReader reader, int length, OffsetChecker checker)
            throws IOException, ZserioError
    {
        readImpl(reader, length, 0, checker);
    }

    /**
     * Constructs array using given length.
     *
     * All elements in created array will be zero.
     *
     * @param length Number of elements for created object.
     */
    public FloatArray(int length)
    {
        data = new float[length];
    }

    /**
     * Constructs array from float array.
     *
     * @param data   float array to construct from.
     * @param offset Index to array <code>data</code> where the first element for construction is located.
     * @param length Number of elements in array <code>data</code> to use for construction.
     */
    public FloatArray(float[] data, int offset, int length)
    {
        if (offset + length > data.length)
            throw new ArrayIndexOutOfBoundsException("FloatArray: Requested array sequence ends beyond " +
                    "the end of the given array. Requested offset is " + offset + ", length is " + length +
                    "but array length is " + data.length + ".");

        this.data = Arrays.copyOfRange(data, offset, offset + length);
    }

    /**
     * Returns element at the given position.
     *
     * @param i Index of element to return.
     *
     * @return Element at the given position.
     */
    public float elementAt(int i)
    {
        return data[i];
    }

    /**
     * Sets element at the given position.
     *
     * @param value Element value to set.
     * @param i     Index of element to set.
     */
    public void setElementAt(float value, int i)
    {
        data[i] = value;
    }

    @Override
    public Array<Float> map(Mapping<Float> mapping)
    {
        final FloatArray result = new FloatArray(data.length);
        for (int index = 0; index < data.length; index++)
        {
            result.setElementAt(mapping.map(elementAt(index)), index);
        }
        return result;
    }

    @Override
    public Array<Float> subRange(int offset, int length)
    {
        return new FloatArray(data, offset, length);
    }

    @Override
    public int length()
    {
        return data.length;
    }

   @Override
    public int hashCode()
    {
        int result = Util.HASH_SEED;

        for (int index = 0; index < data.length; index++)
        {
            result = result * Util.HASH_PRIME_NUMBER + Float.floatToIntBits(elementAt(index));
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof FloatArray)
        {
            final FloatArray that = (FloatArray)obj;
            if (data.length != that.data.length)
            {
                return false;
            }

            for (int index = 0; index < data.length; index++)
            {
                if (Math.abs(this.elementAt(index) - that.elementAt(index)) > EQUALITY_TRESHOLD)
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Sums up all values of an array.
     *
     * @return Sum of all array values.
     */
    public float sum()
    {
        float sumValue = 0.0f;
        for (final float value : data)
            sumValue += value;

        return sumValue;
    }

    /**
     * Writes array to bit stream.
     *
     * @param writer Bit stream where to write.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public void write(BitStreamWriter writer) throws IOException, ZserioError
    {
        writeAligned(writer, null);
    }

    /**
     * Writes auto length array to bit stream.
     *
     * @param writer  Bit stream where to write.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Cannot occurred because indexed offsets are not used.
     */
    public void writeAuto(BitStreamWriter writer) throws IOException, ZserioError
    {
        writeAlignedAuto(writer, null);
    }

    /**
     * Writes aligned auto length array to bit stream applying offset checking.
     *
     * @param writer  Bit stream where to write.
     * @param checker Offset checker to use.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void writeAlignedAuto(BitStreamWriter writer, OffsetChecker checker)
            throws IOException, ZserioError
    {
        writer.writeVarUInt64(data.length);
        writeAligned(writer, checker);
    }

    /**
     * Writes aligned array to bit stream applying offset checking.
     *
     * @param writer  Bit stream where to write.
     * @param checker offset checker to use.
     *
     * @throws IOException     Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void writeAligned(BitStreamWriter writer, OffsetChecker checker) throws IOException, ZserioError
    {
        for (int index = 0; index < data.length; index++)
        {
            alignAndCheckOffset(index, writer, checker);
            writer.writeFloat16(data[index]);
        }
    }

    /**
     * Returns length of array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOf(long bitPosition)
    {
        return bitSizeOfImpl(bitPosition, FLOAT_BIT_SIZE);
    }

    /**
     * Returns length of auto length array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOfAuto(long bitPosition)
    {
        return bitSizeOfAutoImpl(bitPosition, FLOAT_BIT_SIZE);
    }

    /**
     * Returns length of aligned auto length array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOfAlignedAuto(long bitPosition)
    {
        return bitSizeOfAlignedAutoImpl(bitPosition, FLOAT_BIT_SIZE);
    }

    /**
     * Returns length of aligned array stored in bit stream in bits.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Length of array stored in bit stream in bits.
     */
    public int bitSizeOfAligned(long bitPosition)
    {
        return bitSizeOfAlignedImpl(bitPosition, FLOAT_BIT_SIZE);
    }

    /**
     * Initializes indexed offsets for the array.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsets(long bitPosition)
    {
        return initializeOffsetsImpl(bitPosition, FLOAT_BIT_SIZE);
    }

    /**
     * Initializes indexed offsets for the auto length array.
     *
     * @param bitPosition Current bit stream position.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsetsAuto(long bitPosition)
    {
        return initializeOffsetsAutoImpl(bitPosition, FLOAT_BIT_SIZE);
    }

    /**
     * Initializes indexed offsets for the aligned auto length array.
     *
     * @param bitPosition Current bit stream position.
     * @param setter      Offset setter to use.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsetsAlignedAuto(long bitPosition, OffsetSetter setter)
    {
        return initializeOffsetsAlignedAutoImpl(bitPosition, FLOAT_BIT_SIZE, setter);
    }

    /**
     * Initializes indexed offsets for the aligned length array.
     *
     * @param bitPosition Current bit stream position.
     * @param setter      Offset setter to use.
     *
     * @return Updated bit stream position which points to the first bit after the array.
     */
    public long initializeOffsetsAligned(long bitPosition, OffsetSetter setter)
    {
        return initializeOffsetsAlignedImpl(bitPosition, FLOAT_BIT_SIZE, setter);
    }

    @Override
    protected Float boxedElementAt(int index)
    {
        return elementAt(index);
    }

    @Override
    protected Float readBoxedElement(BitStreamReader reader, int numBits) throws IOException
    {
        return reader.readFloat16();
    }

    @Override
    protected void setFromList(List<Float> list)
    {
        data = new float[list.size()];
        for (int index = 0; index < data.length; ++index)
        {
            data[index] = list.get(index);
        }
    }

    @Override
    protected void readN(BitStreamReader reader, int length, int numBits, OffsetChecker checker)
            throws IOException, ZserioError
    {
        data = new float[length];
        for (int index = 0; index < length; index++)
        {
            alignAndCheckOffset(index, reader, checker);
            data[index] = reader.readFloat16();
        }
    }

    private static final int FLOAT_BIT_SIZE = 16;
    private static final float EQUALITY_TRESHOLD = 0.0000001f;

    private float[] data;
}
