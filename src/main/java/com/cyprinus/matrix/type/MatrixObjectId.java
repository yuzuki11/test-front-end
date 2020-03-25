package com.cyprinus.matrix.type;

import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.util.Date;

public class MatrixObjectId implements Serializable {
    private String _id;

    private Date timestamp;

    public MatrixObjectId(String id) {
        String time = id.substring(0, 8);
        byte[] timeBytes = DatatypeConverter.parseHexBinary(time);
        int value;
        value = ((timeBytes[3] & 0xFF)
                | ((timeBytes[2] & 0xFF) << 8)
                | ((timeBytes[1] & 0xFF) << 16)
                | ((timeBytes[0] & 0xFF) << 24));
        timestamp = new Date((long) value * 1000);
        _id = id;

    }

    public String toString() {
        return _id;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
