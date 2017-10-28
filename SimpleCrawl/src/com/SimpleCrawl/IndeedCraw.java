package com.SimpleCrawl;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;

public class IndeedCraw {
	
	public String connectStr;
    public String urlStr="https://www.indeed.com/jobs?q=semiconductor&start=";
    public int listCount = 0;
    
	public IndeedCraw() {
		// TODO Auto-generated constructor stub
	}
	
	public void test(){
		System.out.println("good");
	}
	
	public void connect()
	{
		URL url;
		StringBuilder sb = new StringBuilder(); 
		try {
			String str=""; 
			url = new URL(urlStr); 
			InputStreamReader input0=new InputStreamReader(url.openStream());
	        BufferedReader reader =new BufferedReader(input0);    
            String temp="";
            while((temp=reader.readLine())!=null){     
            	//System.out.println(temp);  
                sb.append(temp);
            }
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		connectStr = sb.toString();
	}

	public void titleMatch() throws FileNotFoundException{
		
		
		try {
			File file =new File("Data.xls");
			FileInputStream inputstream = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(inputstream);
			HSSFSheet sheet = workbook.getSheetAt(0);
			HSSFRow rowhead;// = sheet.createRow(1);
	        //rowhead.createCell(1).setCellValue("abc");
			
			int ii=1;
			for(ii=0;ii<10;ii++){
				sheet.autoSizeColumn(ii);
			}
			
	        
	        String regex="title=\"([^\"]*)\"class=\"turnstileLink\"";
			Pattern pattern=Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(connectStr);
	        int initCount=listCount;
	        int finalCount=listCount;
	        while(matcher.find()) {
	            listCount++;
	            //System.out.println("found: " + count + " : ");
	            //System.out.println(matcher.group(1));
	            rowhead = sheet.createRow(listCount);
		        rowhead.createCell(0).setCellValue(matcher.group(1));
	        }
	        finalCount=listCount;
	        //company
	        listCount=initCount;
	        regex="<span class=\"company\">(?:\\s+)?(?:<a[^>]*>)?([^<]*)(?:</a>)?</span>";
			pattern=Pattern.compile(regex);
	        matcher = pattern.matcher(connectStr);
	        while(matcher.find()) {
	            listCount++;
	            if (listCount<=finalCount)
	            {
	            	rowhead = sheet.getRow(listCount);
	            	rowhead.createCell(1).setCellValue(matcher.group(1));
	            }
	       	}
	        //location
	        listCount=initCount;
	        regex="<span class=(?:\")?location(?:\")?>([^<]*)</span>";
			pattern=Pattern.compile(regex);
	        matcher = pattern.matcher(connectStr);
	        while(matcher.find()) {
	            listCount++;
	            if (listCount<=finalCount)
	            {
	            	rowhead = sheet.getRow(listCount);
	            	rowhead.createCell(2).setCellValue(matcher.group(1));
	            }
	       	}
	        //link:
	        listCount=initCount;
	        regex="<a(?:\\s+)?href=\"([^\"]*)\" target=\"_blank\"(?:\\s+)?"+
	        		"rel=\"noopener nofollow\"";
			pattern=Pattern.compile(regex);
	        matcher = pattern.matcher(connectStr);
	        
	        String writeStr="";
	        while(matcher.find()) {
	            listCount++;
	            if (listCount<=finalCount)
	            {
	            	rowhead = sheet.getRow(listCount);
	            	HSSFCell cell=rowhead.createCell(3);
	            	cell.setCellValue("Link");
	            	writeStr="https://www.indeed.com"+matcher.group(1);
	            	CreationHelper createHelper = workbook.getCreationHelper();
	    	        Hyperlink link =createHelper.createHyperlink(HyperlinkType.URL);
	    	        link.setAddress(writeStr);
	            	cell.setHyperlink((org.apache.poi.ss.usermodel.Hyperlink) link);	            	            	
	            
	            	//if (checkCitizen(writeStr)){
	            	//	cell=rowhead.createCell(4);
	            	//	cell.setCellValue("PHD");
	            	//}	            		
	            }
	       	}
	        
	        listCount=finalCount;
	        System.out.println(listCount);
	        inputstream.close();

            FileOutputStream outFile =new FileOutputStream(file);
            workbook.write(outFile);
            outFile.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public void printTitle(int N) throws FileNotFoundException{
		int i;
		urlStr="https://www.indeed.com/q-semiconductor-jobs.html";
		this.connect();
		this.titleMatch();
		for(i=1;i<N;i++)
		{
			urlStr="https://www.indeed.com/jobs?q=semiconductor&start="+Integer.toString(i*10);	
			this.connect();
			this.titleMatch();
		}
	}
	private boolean checkCitizen(String urlStr){
		URL url;
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL(urlStr); 
		    URLConnection con;
			con = url.openConnection();
			con.setConnectTimeout(10000);//unit:milisecond
		    con.setReadTimeout(10000);
		    InputStream in = con.getInputStream();
			InputStreamReader input0=new InputStreamReader(in);
	        BufferedReader reader =new BufferedReader(input0);
	        System.out.println(listCount);
	        BufferedWriter writer1 = new BufferedWriter(new FileWriter("temp/"+listCount+".html"));
	        String temp="";
	        while((temp=reader.readLine())!=null){       
	            sb.append(temp);
	            writer1.write(temp+"\r\n");
	        }
	        temp=sb.toString();
	       
	        //writer1.write(reader.toString());      
	        writer1.close();

	        
	        String regex="(PhD)|(Ph.D.)|(PHD)";
			Pattern pattern=Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(temp);
	        if(matcher.find()) {
	        	return true;
	        }     
			return false;	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	    
	}
	
	public void creatExcel(){
		try{
			String filename = "Data.xls" ;
	        @SuppressWarnings("resource")
	        HSSFWorkbook workbook = new HSSFWorkbook();
	        HSSFSheet sheet = workbook.createSheet("Title");  
	        HSSFRow rowhead = sheet.createRow((short)0);
	        rowhead.createCell(0).setCellValue("Title");
	        rowhead.createCell(1).setCellValue("Address");
	
	        FileOutputStream fileOut = new FileOutputStream(filename);
	        workbook.write(fileOut);
	        fileOut.close();
	        //System.out.println("Your excel file has been generated!");
		}
        catch ( Exception ex ) {
            System.out.println(ex);
        }
	}
	
}
