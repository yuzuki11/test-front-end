package com.cyprinus.matrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy
public class MatrixApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatrixApplication.class, args);
	}

}
