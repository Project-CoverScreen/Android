package fr.radio.packages;

import java.nio.ByteBuffer;

public abstract class PackagesDef {

    int magic = 0;
    PacketType type = PacketType.INVALID;

    public PackagesDef(PacketType e) {
        this.magic = 0xdeadbeef;
        this.type = e;
    }
    public byte[] serialize(byte[] data) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Aucune fonction serialize");
    };
}
