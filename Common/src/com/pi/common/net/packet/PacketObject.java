package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public interface PacketObject {
    public void writeData(PacketOutputStream pOut) throws IOException;

    public int getLength();

    public void readData(PacketInputStream pIn) throws IOException;
}
