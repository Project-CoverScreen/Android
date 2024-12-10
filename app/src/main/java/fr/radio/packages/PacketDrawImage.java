package fr.radio.packages;

import android.util.Log;

import java.nio.ByteBuffer;

public class PacketDrawImage extends PackagesDef{

    private static final String TAG = "PacketDrawImage";

    private static final int LINES_PER_PACKET = 60;
    private static final int SCREEN_WIDTH = 240;

    public int line_count = 0;
    public byte y = 0;
    public char[][] colors = new char[LINES_PER_PACKET][SCREEN_WIDTH];


    public PacketDrawImage() {
        super(PacketType.CHUNKED_LINE_DRAW);
    }

    public byte[] serialize(int i) throws UnsupportedOperationException {
        byte[] headers = super.serialize();
        ByteBuffer buffer = ByteBuffer.allocate(headers.length + 4+1+(2*60*240));
        buffer.put(headers);
        buffer.putInt(LINES_PER_PACKET);
        buffer.put((byte) (y + (i * LINES_PER_PACKET)));
        for (int x = 0; x < LINES_PER_PACKET; x++) {
            for (int y = 0; y < SCREEN_WIDTH; y++) {
                buffer.putChar((char) (i % 2 == 0 ? 0xFFFF : 0x0000));
            }
        }
        Log.e(TAG, "serialize: " + buffer.array().length);
        return buffer.array();
    }
    // sectionnage ici
    @Override
    public byte[][] serializee() throws UnsupportedOperationException {
        byte[][] packets = new byte[4][];

        for(int i = 0; i < 4; i++) {
            packets[i] = serialize(i);
        }

        return packets;
    }
}
