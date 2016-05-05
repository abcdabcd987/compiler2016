package com.abcdabcd987.compiler2016.Utility;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by abcdabcd987 on 2016-04-18.
 */
public class TeeOutputStream extends OutputStream {
    private List<OutputStream> streams = new ArrayList<>();

    public TeeOutputStream(OutputStream... streams) {
        super();
        Collections.addAll(this.streams, streams);
    }

    public void add(OutputStream outputStream) {
        streams.add(outputStream);
    }

    @Override
    public void write(int b) throws IOException {
        for (OutputStream stream : streams) stream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (OutputStream stream : streams) stream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (OutputStream stream : streams) stream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream stream : streams) stream.flush();
    }

    @Override
    public void close() throws IOException {
        super.close();
        // do not close streams
    }
}
