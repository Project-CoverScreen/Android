package fr.radio.packages;

import java.nio.ByteBuffer;

public abstract class PackagesDef {

    int magic = 0;
    PacketType type = PacketType.INVALID;

    public PackagesDef(PacketType e) {
        this.magic = 0xdeadbeef;
        this.type = e;
    }
    public byte[][] serializee() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Aucune fonction serialize");
    }
    public byte[] serialize() throws UnsupportedOperationException {
        ByteBuffer data = ByteBuffer.allocate(4 + 4);
        data.putInt(magic);
        data.putInt(type.ordinal());
        return data.array();
    };
}
