package com.SimpleCrawl;

import java.io.FileNotFoundException;

public class SimpleCrawl {

	public SimpleCrawl() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IndeedCraw A=new IndeedCraw();
		A.test();
		A.creatExcel();
		try {
			A.printTitle(500);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
