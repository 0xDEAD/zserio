/**
 *
 */
package zserio.runtime.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.junit.Test;

public class ByteArrayBitStreamWriterTest
{
    @Test
    public void test1() throws Exception
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (int value : DATA)
                {
                    writer.writeBits(value, 4);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (int value : DATA)
                {
                    assertEquals(value, reader.readBits(4));
                }
            }

            private final int[] DATA = {
                0x6,
                0x7,
                0x8,
                0x9,
                0x1,
                0x2,
                0x3,
                0x4,
                0xc,
                0xd,
                0xe,
                0xf
            };
        });
    }

    @Test
    public void test2() throws Exception
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeBits(6, 4);
                writer.writeByte(0x78);
                writer.writeByte(0x91);
                writer.writeByte(0x23);
                writer.writeByte(0x4c);
                writer.writeByte(0xde);
                writer.writeBits(0xf, 4);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                assertEquals(0x6, reader.readBits(4));
                assertEquals(0x7, reader.readBits(4));
                assertEquals(0x8, reader.readBits(4));
                assertEquals(0x9, reader.readBits(4));

                assertEquals(0x1, reader.readBits(4));
                assertEquals(0x2, reader.readBits(4));
                assertEquals(0x3, reader.readBits(4));
                assertEquals(0x4, reader.readBits(4));

                assertEquals(0xc, reader.readBits(4));
                assertEquals(0xd, reader.readBits(4));
                assertEquals(0xe, reader.readBits(4));
                assertEquals(0xf, reader.readBits(4));
            }
        });
    }

    @Test
    public void test3() throws Exception
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeBits(6, 4);
                writer.writeShort(0x7891);
                writer.writeShort(0x234c);
                writer.writeBits(0xd, 4);
                writer.writeByte(0xef);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                assertEquals(0x6, reader.readBits(4));
                assertEquals(0x7, reader.readBits(4));
                assertEquals(0x8, reader.readBits(4));
                assertEquals(0x9, reader.readBits(4));

                assertEquals(0x1, reader.readBits(4));
                assertEquals(0x2, reader.readBits(4));
                assertEquals(0x3, reader.readBits(4));
                assertEquals(0x4, reader.readBits(4));

                assertEquals(0xc, reader.readBits(4));
                assertEquals(0xd, reader.readBits(4));
                assertEquals(0xe, reader.readBits(4));
                assertEquals(0xf, reader.readBits(4));
            }
        });
    }

    @Test
    public void test4() throws Exception
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeShort(0x234c);
        writer.writeByte(0xef);
        writer.alignTo(32);
        writer.close();

        final byte[] b = writer.toByteArray();
        assertEquals(b.length * 8L, 32);
    }

/*
    FIXME: fails because ByteArrayBitStreamWriter.writeBoolean() does 8bit padding
    while ByteArrayBitStreamReader does not do any padding.

    @Test
    public void writeBoolean() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (boolean value : DATA)
                {
                    writer.writeBoolean(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (boolean value : DATA)
                {
                    assertEquals(value, reader.readBoolean());
                }
                assertEquals(8 * DATA.length, getBitOffset(reader));
            }

            private final boolean[] DATA =
            {
                true,
                false
            };
        });
    }
*/

    @Test
    public void writeBit() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                final int initialOffset = writer.getBitOffset();
                for (int value : DATA)
                {
                    writer.writeBit(value);
                }
                assertEquals(initialOffset + DATA.length, writer.getBitOffset());
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (int value : DATA)
                {
                    assertEquals(value, reader.readBit());
                }
            }

            private final int[] DATA =
            {
                0,
                1,
                0
            };
        });
    }

    @Test
    public void writeBytes() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeBytes("Test");
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                assertEquals(84, reader.readByte());
            }
        });
    }

    @Test
    public void writeChar() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeChar(TEST_CHARACTER);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                assertEquals(TEST_CHARACTER, reader.readChar());
            }

            private final static char TEST_CHARACTER = 'c';
        });
    }

    @Test
    public void writeChars() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeChars(TEST_STRING);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (int i = 0; i < TEST_STRING.length(); ++i)
                {
                    char c = TEST_STRING.charAt(i);
                    assertEquals(c, reader.readChar());
                }
            }

            private final static String TEST_STRING = "cd";
        });
    }

    @Test
    public void writeByte() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (byte value : DATA)
                {
                    writer.writeByte(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (byte value : DATA)
                {
                    assertEquals(value, reader.readByte());
                }
            }

            private final byte[] DATA =
            {
                0,
                1,
                -1,
                Byte.MAX_VALUE,
                Byte.MIN_VALUE
            };
        });
    }

    @Test
    public void writeShort() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (short value : DATA)
                {
                    writer.writeShort(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (short value : DATA)
                {
                    assertEquals(value, reader.readShort());
                }
            }

            private final short[] DATA =
            {
                0,
                1,
                -1,
                Short.MAX_VALUE,
                Short.MIN_VALUE
            };
        });
    }

    @Test
    public void writeInt() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (int value : DATA)
                {
                    writer.writeInt(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (int value : DATA)
                {
                    assertEquals(value, reader.readInt());
                }
            }

            private final int[] DATA =
            {
                0,
                1,
                -1,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                127,
                137
            };
        });
    }

    @Test
    public void writeLong() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (long value : DATA)
                {
                    writer.writeLong(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (long value : DATA)
                {
                    assertEquals(value, reader.readLong());
                }
            }

            private final long[] DATA =
            {
                0,
                1,
                -1,
                Long.MAX_VALUE,
                Long.MIN_VALUE,
                1111111111L,
                1212121212L
            };
        });
    }

    @Test
    public void writeUnsignedInt() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (long value : DATA)
                {
                    writer.writeUnsignedInt(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (long value : DATA)
                {
                    assertEquals(value, reader.readUnsignedInt());
                }
            }

            private final long[] DATA =
            {
                0,
                1,
                Integer.MAX_VALUE
            };
        });
    }

    @Test
    public void writeUnsignedByte() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (short value : DATA)
                {
                    writer.writeUnsignedByte(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (short value : DATA)
                {
                    assertEquals(value, reader.readUnsignedByte());
                }
            }

            private final short[] DATA =
            {
                5,
                Byte.MAX_VALUE
            };
        });
    }

    @Test
    public void writeBigInteger() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeBigInteger(BigInteger.valueOf(0x1), 4);
                writer.writeBigInteger(BigInteger.valueOf(0x7), 4);
                writer.writeBigInteger(BigInteger.valueOf(0x7f), 8);
                writer.writeBigInteger(BigInteger.valueOf(0x7fff), 16);
                writer.writeBigInteger(BigInteger.valueOf(0x7fffffff), 32);
                writer.writeBigInteger(BigInteger.valueOf(0x7fffffffffffffffL), 64);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                final int[] expectedBytes = {
                    0x17, // 0x1, 0x7 nibbles combined
                    0x7f,
                    0x7f, 0xff,
                    0x7f, 0xff, 0xff, 0xff,
                    0x7f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff
                };
                for (int value : expectedBytes)
                {
                    assertEquals(value, reader.readUnsignedByte());
                }
            }
        });
    }

    @Test
    public void writeDouble() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (double value : DATA)
                {
                    writer.writeDouble(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (double value : DATA)
                {
                    assertEquals(value, reader.readDouble(), 0.0d);
                }
            }

            private final double[] DATA =
            {
                1.0d,
                2.0d
            };
        });
    }

    @Test
    public void writeFloat() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (float value : DATA)
                {
                    writer.writeFloat(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (float value : DATA)
                {
                    assertEquals(value, reader.readFloat(), 0.0f);
                }
            }

            private final float[] DATA =
            {
                1.0f,
                2.0f
            };
        });
    }

    @Test
    public void writeFloat16() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeFloat16(1.0f);
                writer.writeFloat16(2.0f);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // hand-encoded values
                assertEquals(0x3c00, reader.readUnsignedShort());
                assertEquals(0x4000, reader.readUnsignedShort());
            }
        });
    }

    /**
     * Test capacity.
     *
     * @throws IOException if the writings and readings fail
     */
    @Test
    public void capacity() throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeInt(10);
        writer.writeLong(10);
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        assertEquals(10, reader.readInt());
        assertEquals(10, reader.readLong());

        writer = new ByteArrayBitStreamWriter(1234);
        writer.write(127);
        writer.writeBits(7, 4);
        writer.writeInt(123);
        writer.writeLong(12345678910L);

        reader = new ByteArrayBitStreamReader(writer.toByteArray());
        assertEquals(127, reader.read());
        assertEquals(7, reader.readBits(4));
        assertEquals(123, reader.readInt());
        assertEquals(12345678910L, reader.readLong());

        try
        {
            writer = new ByteArrayBitStreamWriter(Integer.MAX_VALUE);
            fail();
        }
        catch (final Exception e)
        {
            assertTrue(true);
        }

        try
        {
            writer = new ByteArrayBitStreamWriter(-1, ByteOrder.BIG_ENDIAN);
            fail();
        }
        catch (final Exception e)
        {
            assertTrue(true);
        }
        reader.close();
        writer.close();
    }

    /**
     * Test the writeBool method.
     *
     * @throws IOException if the writing fails
     */
    @Test
    public void writeBool() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (boolean value : DATA)
                {
                    writer.writeBool(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (boolean value : DATA)
                {
                    assertEquals(value, reader.readBit() != 0 ? true : false);
                }
                assertEquals(DATA.length, getBitOffset(reader));
            }

            private final boolean[] DATA =
            {
                true,
                false
            };
        });
    }

    /**
     * Test the skip Bits method.
     *
     * @throws IOException if the ByteArrayBitStreamWriter cannot be closed
     */
    @Test
    public void skipBits() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertEquals(0, writer.getBitPosition());
        writer.skipBits(5);
        assertEquals(5, writer.getBitPosition());
        writer.close();
    }

    /**
     * Test the writeUTF method which is not supported at this moment.
     *
     * @throws IOException if the string cannot be written
     */
    @Test
    public void writeUTF() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        try
        {
            writer.writeUTF("test");
            assertTrue(false);
        }
        catch (final Exception e)
        {
            assertTrue(true);
        }
        writer.close();
    }

    /**
     * Test the growBuffer method.
     *
     * @throws IOException if the ByteArrayBitStreamWriter cannot be closed
     */
    @Test
    public void growBuffer() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        for (int i = 0; i < 8191; i++)
        {
            writer.writeByte(1);
        }
        assertEquals(8191, writer.getBytePosition());
        writer.close();
    }

    /**
     * Test the write methods of the ByteArrayBitStreamWriter in little endian mode.
     *
     * @throws IOException if the writing fails
     */
    @Test
    public void littleEndian() throws IOException
    {
        for (final TestMethod method : TestMethod.values())
        {
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter(ByteOrder.LITTLE_ENDIAN);
            if (method == TestMethod.UNALIGNED)
            {
                /*
                 * Write prolog bit to force test values to be unaligned.
                 */
                writer.writeBit(PROLOG_BIT);
            }
            writer.writeInt(5);
            writer.writeLong(3685477580L);
            writer.writeShort(5);
            writer.writeUnsignedShort(5);
            writer.writeUnsignedInt(4294967295L);
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                    ByteOrder.LITTLE_ENDIAN);
            if (method == TestMethod.UNALIGNED)
            {
                /*
                 * Read dummy bit.
                 */
                assertEquals("Failed to read prolog bit", PROLOG_BIT, reader.readBits(1));
            }
            assertEquals(5, reader.readInt());
            assertEquals(3685477580L, reader.readLong());
            assertEquals((short) 5, reader.readShort());
            assertEquals((short) 5, reader.readUnsignedShort());
            assertEquals(4294967295L, reader.readUnsignedInt());
            reader.close();
        }
    }

    @Test
    public void writeBitsInvalidNumException()
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final int numBits[] = { -1, 0, 65 };
        for (int i = 0; i < numBits.length; ++i)
        {
            try
            {
                writer.writeBits(0x1L, numBits[i]);
                fail();
            }
            catch (IOException e)
            {
                fail();
            }
            catch (IllegalArgumentException e)
            {
                assertTrue(true);
            }
        } // for numbits
    }

    @Test
    public void writeBitsIllegalArgumentException()
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (int i = 1; i < 64; i++)
        {
            long minSigned   = -(1L << (i-1));
            long maxUnsigned =  (1L << (i  )) - 1;

            long minSignedViolation   = minSigned   - 1;
            long maxUnsignedViolation = maxUnsigned + 1;

            try
            {
                writer.writeSignedBits(minSigned,   i);
                writer.writeBits(maxUnsigned, i);
            }
            catch (IOException e)
            {
                fail();
            }

            try
            {
                writer.writeBits(minSignedViolation, i);
                System.out.println("unexpected succes writeBits: " + minSignedViolation + " # " + i);
                fail();
            }
            catch (IOException e)
            {
                fail();
            }
            catch (IllegalArgumentException e)
            {
                assertTrue(true);
            }

            try
            {
                writer.writeBits(maxUnsignedViolation, i);
                System.out.println("unexpected succes writeBits: " + maxUnsignedViolation + " # " + i);
                fail();
            }
            catch (IOException e)
            {
                fail();
            }
            catch (IllegalArgumentException e)
            {
                assertTrue(true);
            }
        } // for numBits
    }

    @Test
    public void writeIllegalArgumentException()
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        // Note: no range check for writeBigInteger

        try
        {
            writer.writeUnsignedByte((short)-1);
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            writer.writeUnsignedShort(-1);
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            writer.writeUnsignedInt(-1L);
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            writer.writeUnsignedByte((short)(1 << 8));
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            writer.writeUnsignedShort(1 << 16);
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            writer.writeUnsignedInt(1L << 32);
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }
    }

    @Test
    public void writeZeros() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeBits(0, Short.SIZE);
                writer.writeBits(1, 1);
                writer.writeZeros(Short.SIZE - 2);
                writer.writeBits(1, 1);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                assertEquals(0x8001, reader.readInt());
            }
        });
    }

    @Test
    public void writeOnes() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeBits(0, 1);
                writer.writeOnes(Short.SIZE - 2);
                writer.writeBits(0, 1);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                assertEquals(0x7ffe, reader.readShort());
            }
        });
    }

    @Test
    public void writeVarInt16() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                // 1 byte
                writer.writeVarInt16((short)0);
                writer.writeVarInt16((short)+0x3f);
                writer.writeVarInt16((short)-0x3f);

                // 2 bytes
                writer.writeVarInt16((short)+0x7f);
                writer.writeVarInt16((short)-0x7f);
                writer.writeVarInt16((short)+0x3fff);
                writer.writeVarInt16((short)-0x3fff);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // 1 byte
                assertEquals(0x00, reader.readBits(8)); //  0
                assertEquals(0x3f, reader.readBits(8)); //  111111b =  0x3f
                assertEquals(0xbf, reader.readBits(8)); // -111111b = -0x3f

                // 2 bytes
                assertEquals(0x407f, reader.readBits(16)); //  0b1111111 =  0x7f
                assertEquals(0xc07f, reader.readBits(16)); // -0b1111111 = -0x7f
                assertEquals(0x7fff, reader.readBits(16)); //  0b11111111111111 =  0x3fff
                assertEquals(0xffff, reader.readBits(16)); // -0b11111111111111 = -0x3fff
            }
        });
    }

    @Test
    public void writeVarInt32() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                // 1 byte
                writer.writeVarInt32(0);
                writer.writeVarInt32(+0x3f);
                writer.writeVarInt32(-0x3f);

                // 2 bytes
                writer.writeVarInt32(+0x7f);
                writer.writeVarInt32(-0x7f);
                writer.writeVarInt32(+0x1fff);
                writer.writeVarInt32(-0x1fff);

                // 3 bytes
                writer.writeVarInt32(+0x3fff);
                writer.writeVarInt32(-0x3fff);
                writer.writeVarInt32(+0xfffff);
                writer.writeVarInt32(-0xfffff);

                // 4 bytes
                writer.writeVarInt32(+0x3fffff);
                writer.writeVarInt32(-0x3fffff);
                writer.writeVarInt32(+0xfffffff);
                writer.writeVarInt32(-0xfffffff);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // 1 byte
                assertEquals(0x00, reader.readBits(8)); //  0
                assertEquals(0x3f, reader.readBits(8)); //  111111b =  0x3f
                assertEquals(0xbf, reader.readBits(8)); // -111111b = -0x3f

                // 2 bytes
                assertEquals(0x407f, reader.readBits(16)); //  0b1111111 =  0x7f
                assertEquals(0xc07f, reader.readBits(16)); // -0b1111111 = -0x7f
                assertEquals(0x7f7f, reader.readBits(16)); //  0b1111111111111 =  0x1fff
                assertEquals(0xff7f, reader.readBits(16)); // -0b1111111111111 = -0x1fff

                // 3 bytes
                assertEquals(0x40ff7fL, reader.readBits(24)); //  0b11111111111111 =  0x3fff
                assertEquals(0xc0ff7fL, reader.readBits(24)); // -0b11111111111111 = -0x3fff
                assertEquals(0x7fff7fL, reader.readBits(24)); //  0b11111111111111 =  0xfffff
                assertEquals(0xffff7fL, reader.readBits(24)); // -0b11111111111111 = -0xfffff

                // 4 bytes
                assertEquals(0x40ffffffL, reader.readBits(32)); //  0x1fffff
                assertEquals(0xc0ffffffL, reader.readBits(32)); // -0x1fffff
                assertEquals(0x7fffffffL, reader.readBits(32)); //  0xfffffff
                assertEquals(0xffffffffL, reader.readBits(32)); // -0xfffffff
            }
        });
    }

    @Test
    public void writeVarUInt16() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                // 1 byte
                writer.writeVarUInt16((short)0);
                writer.writeVarUInt16((short)0x7f);

                // 2 bytes
                writer.writeVarUInt16((short)0xff);
                writer.writeVarUInt16((short)0x7fff);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // 1 byte
                assertEquals(0x00, reader.readBits(8));    // 0
                assertEquals(0x7f, reader.readBits(8));    // 1111111b = 0x7f

                // 2 bytes
                assertEquals(0x80ff, reader.readBits(16)); // 0b11111111 = 0xff
                assertEquals(0xffff, reader.readBits(16)); // 0b111111111111111 = 0x7fff
            }
        });
    }

    @Test
    public void writeVarUInt32() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                // 1 byte
                writer.writeVarUInt32(0);
                writer.writeVarUInt32(0x7f);

                // 2 bytes
                writer.writeVarUInt32(0xff);
                writer.writeVarUInt32(0x3fff);

                // 3 bytes
                writer.writeVarUInt32(0x7fff);
                writer.writeVarUInt32(0x1fffff);

                // 4 bytes
                writer.writeVarUInt32(0x3fffff);
                writer.writeVarUInt32(0x1fffffff);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // 1 byte
                assertEquals(0x00, reader.readBits(8));    // 0
                assertEquals(0x7f, reader.readBits(8));    // 1111111b = 0x7f

                // 2 bytes
                assertEquals(0x817f, reader.readBits(16));
                assertEquals(0xff7f, reader.readBits(16));

                // 3 bytes
                assertEquals(0x81ff7f, reader.readBits(24));
                assertEquals(0xffff7f, reader.readBits(24));

                // 4 bytes
                assertEquals(0x80ffffffL, reader.readBits(32));
                assertEquals(0xffffffffL, reader.readBits(32));
            }
        });
    }

    /**
     * Describes a test method.
     */
    private enum TestMethod
    {
        /**
         * Test method: write aligned.
         */

        ALIGNED,

        /**
         * Test method: write unaligned.
         */
        UNALIGNED;
    }

    private interface WriteReadTestable
    {
        // don't use BitStreamReader so that this tests solely the writer
        void write(ByteArrayBitStreamWriter writer) throws IOException;
        void read(ImageInputStream reader) throws IOException;
    }

    private void writeReadTest(WriteReadTestable writeReadTest) throws IOException
    {
        for (final TestMethod method : TestMethod.values())
        {
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

            if (method == TestMethod.UNALIGNED)
            {
                writer.writeBit(1);
            }
            writeReadTest.write(writer);
            writer.close();

            final byte[] data = writer.toByteArray();
            if (method == TestMethod.UNALIGNED)
                trimBitFromLeft(data);
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            final MemoryCacheImageInputStream reader = new MemoryCacheImageInputStream(inputStream);
            try
            {
                writeReadTest.read(reader);
            }
            finally
            {
                reader.close();
                inputStream.close();
            }
        }
    }

    private static void trimBitFromLeft(byte[] data)
    {
        byte carry = 0;
        for (int i = data.length; i > 0; --i)
        {
            final int currentValue = data[i - 1] & 0xff; // prevent sign extension later (signed byte->int)

            data[i - 1] = (byte)((currentValue << 1) | carry);
            carry = (byte)(currentValue >>> 7); // MSB move to carry
        }
        // the last carry bit is trimmed off
    }

    private static long getBitOffset(ImageInputStream inputStream) throws IOException
    {
        return 8 * inputStream.getStreamPosition() + inputStream.getBitOffset();
    }

    /**
     * Dummy bit used to force values to be written unaligned.
     */
    private static final int PROLOG_BIT = 1;
}
