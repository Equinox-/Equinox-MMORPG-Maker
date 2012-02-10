package com.pi.common.database;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public interface DatabaseObject {
    public void write(PacketOutputStream pOut) throws IOException;

    public void read(PacketInputStream pIn) throws IOException;
}
