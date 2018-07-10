package com.shoxuan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class shouxuanBadcase {
	
	//首选率计数，统计结果文档中各数据个数
	protected static int wordstart = 0 ;
	protected static int wordend = 0 ;
	protected static int targertMissWrong = 0  ; //表示有未命中
	protected static int targertMissError = 0 ;  //表示未有触击键盘

	// 获取当前jar包所在地址
	public  String  getCurrentDirctoryName() {
		File thisDir = new File("");
		String thisDirName = null;
		try {
			thisDirName = thisDir.getCanonicalPath().toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return thisDirName;
	}
	// 控制台打印语料未命中的详细内容
	private static void print(int badcaseLinenum,String[] badcaseDetail) {
		for(int i=0 ; i<badcaseLinenum ; i++) {
			System.out.println(badcaseDetail[i]);
		}		
	}
	
	public static void main(String[] args) throws IOException {
		
		int currentFileNum = -1 ;
		File thisDir = new File("");
		String thisDirName = null;
		String resultDirName = null;
		try {
			thisDirName = thisDir.getCanonicalPath().toString();
			//程序处理的多个结果文件，放在新建的result文件夹下
			thisDirName =  thisDirName + "\\" + "result" ;
			resultDirName = thisDirName + "\\" + "AnylaseRsult" ;
			System.out.println(thisDirName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File dirFile = new File(thisDirName);
		File resultDirNameFile = new File(resultDirName);
		if(!dirFile.exists()){
			dirFile.mkdir();
		}
		if(!resultDirNameFile.exists()) {
			resultDirNameFile.mkdirs();
		}
		String[] fileName = dirFile.list();	
		File resultFile = new File(resultDirName,"wrongResultFile.txt");	
		FileOutputStream fileOutputStream = new FileOutputStream(resultFile);
		OutputStreamWriter  outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8") ;
		BufferedWriter writer = new BufferedWriter(outputStreamWriter);
		if(resultFile.exists()) {
			resultFile.delete();
		}
		resultFile.createNewFile();
		File resultFile2 = new File(resultDirName,"ErrorResultFile.txt");	
		FileOutputStream fileOutputStream2 = new FileOutputStream(resultFile2);
		OutputStreamWriter  outputStreamWriter2 = new OutputStreamWriter(fileOutputStream2, "UTF-8") ;
		BufferedWriter writer2 = new BufferedWriter(outputStreamWriter2);
		if(resultFile2.exists()) {
			resultFile2.delete();
		}
		resultFile2.createNewFile();
		
		String currentFileName = null;
		File shouxuanFile = null;
		String[] badcaseDetail =  new String[100];
		for(currentFileNum = 0 ; currentFileNum < fileName.length  ; currentFileNum ++ ) {
			currentFileName = fileName[currentFileNum] ;
			shouxuanFile = new File(thisDirName,currentFileName);
			int badcaseLinenum = -1;
			//File shouxuanResultFile = new File();
			if(!shouxuanFile.isDirectory()) {
				//对本目录的首选率结果文件进行处理
				FileInputStream fileInputStream = new FileInputStream(shouxuanFile);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"UTF-8");
				BufferedReader reader = null ;
				String lineData = null;
				Boolean target = false;
				Boolean errorType = false;
				try {
					//reader = new BufferedReader(new FileReader(shouxuanFile));
					reader = new BufferedReader(inputStreamReader);
					while((lineData = reader.readLine())!= null ) {
						//处理首选率格式
						if(lineData.contains("wordstart")) {
							wordstart ++;
							target = false;
							errorType = false;
 							badcaseLinenum = 0 ;
							badcaseDetail[badcaseLinenum] = lineData; 
							badcaseLinenum ++ ;
						}else if(lineData.contains("wordend")){
							wordend++;
							//判断存档  true   false 是否未命中 
							if(target){
								if(!errorType) {
									print(badcaseLinenum,badcaseDetail);
									for(int i = 0 ;i < badcaseLinenum; i++) {
										writer.write(badcaseDetail[i]+"\r\n");
									}
								}else {
									for(int i = 0 ;i < badcaseLinenum; i++) {
										writer2.write(badcaseDetail[i]+"\r\n");
									}
								}
								
							}
							badcaseLinenum = 0 ;
						}else if(lineData.contains("target:-1")) {
							targertMissWrong++;
							//判断是否是有存本地必要 
							//半盘存本地类型
							target = true;
							badcaseDetail[badcaseLinenum] = lineData; 
							badcaseLinenum ++ ;
						}else if(lineData.contains("target:-2")) {
							targertMissError++;
							//判断是否是有存本地必要 
							//半盘存本地类型
							target = true;
							errorType=true;
							badcaseDetail[badcaseLinenum] = lineData; 
							badcaseLinenum ++ ;
						}
						else {
							badcaseLinenum = badcaseLinenum < 0 ? 0 : badcaseLinenum ;
							badcaseDetail[badcaseLinenum] = lineData; 
							badcaseLinenum ++ ;
						}
						
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					if(reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				reader.close();	
				inputStreamReader.close();
				fileInputStream.close();
			}
			
		}
		printDetail(resultDirName);
		writer.close();
		outputStreamWriter.close();
		fileOutputStream.close();
		writer2.close();
		outputStreamWriter2.close();
		fileOutputStream2.close();
	}
	
	private static String myPercent(int aNum,int bNum) {
		if(aNum==0 || bNum== 0)
			return "0%";
		double aDouble = aNum * 1.0 ;
		double bDouble = bNum * 1.0 ;
		double cDouble = 1-(aDouble / bDouble) ;
		DecimalFormat percent = new DecimalFormat("##.00%");
		String percentString = percent.format(cDouble);
		return percentString;
	}

	
	private static void printDetail(String thisDirName) throws IOException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"+" "+"hh:mm:ss");
		String dataTime = simpleDateFormat.format(new java.util.Date());
		File detailFile = new File(thisDirName,"Detail.txt");	
		FileOutputStream detailOutputStream = new FileOutputStream(detailFile);
		OutputStreamWriter  detailOutputStreamWriter = new OutputStreamWriter(detailOutputStream, "UTF-8") ;
		BufferedWriter detailWriter = new BufferedWriter(detailOutputStreamWriter);
		if(detailFile.exists()) {
			detailFile.delete();
		}
		detailWriter.write("结果生成时间："+dataTime+"\r\n");
		detailWriter.write("首选率语料有效的未命中个数:"+ (wordstart-wordend == 0 ? wordstart : -1 )+"\r\n");
		detailWriter.write("wordstart个数为："+wordstart+"\r\n");
		detailWriter.write("wordend个数为："+wordend+"\r\n");
		detailWriter.write("无候选的语料个数为："+targertMissWrong+"\r\n");
		detailWriter.write("有候选语料类首选率："+myPercent(targertMissWrong, wordstart)+"\r\n");
		detailWriter.write("未有触击键盘个数为："+targertMissError+"\r\n");
		detailWriter.close();
		System.out.println("\n\n"+thisDirName);
	}
	
}
