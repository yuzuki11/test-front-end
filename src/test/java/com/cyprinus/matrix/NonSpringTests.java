package com.cyprinus.matrix;

import com.cyprinus.matrix.type.MatrixObjectId;
import com.cyprinus.matrix.util.MongoLikeIdGenerator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

class NonSpringTests {

    @Test
    void getId() {
        MongoLikeIdGenerator mongoLikeIdGenerator = new MongoLikeIdGenerator();
        try {
            Method method = mongoLikeIdGenerator.getClass().getDeclaredMethod("getId");
            method.setAccessible(true);
            for (int i = 0; i < 5; i++) {
                Thread.sleep(50);
                System.out.println(method.invoke(mongoLikeIdGenerator).toString());
                //method.invoke(mongoLikeIdGenerator).toString();
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    void testMatrixObjectId() {
        MatrixObjectId matrixObjectId = new MatrixObjectId("5349b4ddd2781d08c09890f4");
        System.out.println(matrixObjectId.toString());
        System.out.println(matrixObjectId.getTimestamp());
    }

    @Test
    void testNotNull(){
        Boolean b = null;
        System.out.print(b);
        System.out.print(!b);
        System.out.print(!!b);
    }

}
