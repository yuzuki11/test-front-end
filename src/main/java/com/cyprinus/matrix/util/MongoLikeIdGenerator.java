package com.cyprinus.matrix.util;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class MongoLikeIdGenerator implements Configurable, IdentifierGenerator {
    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {

    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return getId();
    }

    public MongoLikeIdGenerator() {
        super();
        Random random = new Random(System.currentTimeMillis());
        random.nextBytes(instanceUnique);
        increment = random.nextInt();
    }

    private int increment;

    private byte instanceUnique[] = new byte[5];

    public String getId() {
        synchronized (MongoLikeIdGenerator.class) {
            byte id[] = new byte[12];
            byte timestampBytes[] = new byte[4];
            byte incrementBytes[] = new byte[3];
            int timestamp = (int) (System.currentTimeMillis() / 1000);
            increment++;
            timestampBytes[3] = (byte) (timestamp & 0xff);
            timestampBytes[2] = (byte) (timestamp >> 8 & 0xff);
            timestampBytes[1] = (byte) (timestamp >> 16 & 0xff);
            timestampBytes[0] = (byte) (timestamp >> 24 & 0xff);
            incrementBytes[2] = (byte) (increment & 0xff);
            incrementBytes[1] = (byte) (increment >> 8 & 0xff);
            incrementBytes[0] = (byte) (increment >> 16 & 0xff);
            System.arraycopy(timestampBytes, 0, id, 0, 4);
            System.arraycopy(instanceUnique, 0, id, 4, 5);
            System.arraycopy(incrementBytes, 0, id, 9, 3);
            return DatatypeConverter.printHexBinary(id).toLowerCase();
        }
    }
}
