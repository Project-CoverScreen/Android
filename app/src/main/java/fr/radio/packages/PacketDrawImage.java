package fr.radio.packages;

import android.util.Log;

import java.nio.ByteBuffer;

public class PacketDrawImage extends PackagesDef{

    private static final String TAG = "PacketDrawImage";

    private static final int SCREEN_WIDTH = 240;

    public int line_count = 0;
    public char[][] colors = new char[1][SCREEN_WIDTH]; // [1][240]

    //TODO: add CRC value in Packages and line_count
    private static final int bufferSize = 4+(2*240); //One ligne of Pixels
                                        // Magic / line_count????! / Pixels (2 octets each)

    public PacketDrawImage() {
        super(PacketType.CHUNKED_LINE_DRAW);
    }

    public byte[] serialize(byte[] data) throws UnsupportedOperationException {
        ByteBuffer buffer = ByteBuffer.allocate(4 + bufferSize);
        buffer.putInt(this.magic);
        buffer.put(data);
        Log.e(TAG, "serialize: " + buffer.array().length);
        return buffer.array();
    }
}
