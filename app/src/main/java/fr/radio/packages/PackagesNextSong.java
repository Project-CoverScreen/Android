package fr.radio.packages;

import java.nio.ByteBuffer;

public class PackagesNextSong extends PackagesDef {

    public static String songId;
    public static final byte[] data = new byte[0x40];

    public PackagesNextSong() {
        super(PacketType.CHANGE_CURRENT_SONG);
    }

    @Override
    public byte[] serialize() throws UnsupportedOperationException {
        ByteBuffer buffer = ByteBuffer.allocate((20));
        buffer.put(songId.getBytes());
        return buffer.array();
    }
}
