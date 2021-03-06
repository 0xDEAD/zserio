package zserio.runtime.array;

import java.io.IOException;
import java.util.Iterator;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;

public class UnsignedByteArrayFactory implements ArrayFactory
{
    @Override
    public ArrayWrapper create(int size)
    {
        return wrap(new UnsignedByteArray(size));
    }

    @Override
    public ArrayWrapper create(long[] data, int offset, int length)
    {
        return wrap(new UnsignedByteArray(convertDataArray(data), offset, length));
    }

    public ArrayWrapper create(BitStreamReader reader, int length, int numBits) throws IOException
    {
        return wrap(new UnsignedByteArray(reader, length, numBits));
    }

    private short[] convertDataArray(long[] data)
    {
        short[] intData = new short[data.length];
        for (int i = 0; i < data.length; i++)
            intData[i] = (short)data[i];

        return intData;
    }

    private ArrayWrapper wrap(UnsignedByteArray array)
    {
        return new UnsignedByteArrayWrapper(array);
    }

    private static class UnsignedByteArrayWrapper implements ArrayWrapper
    {
        public UnsignedByteArrayWrapper(UnsignedByteArray array)
        {
            this.array = array;
        }

        @Override
        public void setElementAt(long value, int index)
        {
            array.setElementAt((short)value, index);
        }

        @Override
        public long elementAt(int index)
        {
            return array.elementAt(index);
        }

        @Override
        public int length()
        {
            return array.length();
        }

        public int bitSizeOf(long bitPosition, int numBits)
        {
            return array.bitSizeOf(bitPosition, numBits);
        }

        public int sum()
        {
            return array.sum();
        }

        public ArrayWrapper subRange(int offset, int length)
        {
            return new UnsignedByteArrayWrapper((UnsignedByteArray)array.subRange(offset, length));
        }

        public int hashCode()
        {
            return array.hashCode();
        }

        public boolean equals(Object other)
        {
            return array.equals(unwrap(other));
        }

        public Iterator<Long> iterator()
        {
            return new IteratorWrapper<Short>(array.iterator());
        }

        public void write(BitStreamWriter writer, int numBits) throws IOException, ZserioError
        {
            array.write(writer, numBits);
        }

        private Object unwrap(Object obj)
        {
            // if other is IntArrayWrapper, unwrap the array
            if (obj instanceof UnsignedByteArrayWrapper)
            {
                obj = ((UnsignedByteArrayWrapper)obj).array;
            }
            // else: leave argument as it is

            return obj;
        }

        private final UnsignedByteArray array;
    }
}
