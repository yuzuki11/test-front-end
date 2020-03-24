package com.cyprinus.matrix;

import com.cyprinus.matrix.util.MongoLikeIdGenerator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

class MongoLikeIdTest {

    @Test
    void getId() {
        MongoLikeIdGenerator mongoLikeIdGenerator = new MongoLikeIdGenerator();
        try {
            Method method = mongoLikeIdGenerator.getClass().getDeclaredMethod("getId");
            method.setAccessible(true);
            for (int i = 0; i < 5; i++) {
                Thread.sleep(50);
                System.out.println(method.invoke(mongoLikeIdGenerator).toString());
            }
        } catch (Exception ignored) {
        }
    }

}
