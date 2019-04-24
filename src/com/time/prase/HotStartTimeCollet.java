package com.time.prase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class HotStartTimeCollet {

	public static FileHelper fh = new FileHelper();
	static int times = 1000;
	static String deviceId = "";
	public static String packageName = "com.nationsky.uem.dt";
	//com.nationsky.uem.dt   com.nq.fakedingtalk com.emmsdk.androiddemo
	public static String launchActivity = ".MainActivity";
	public static String amActivityStr = packageName + "/" + launchActivity;
	public static String appContainer = "com.emmsdk.androiddemo";
	public static String alias = "Test";
	public static int crashCount = 0;
	public static String driver = "D://";
	public static String resultFile = driver + "result.txt";
	static DateFormat format = new SimpleDateFormat("hh:mm:ss");
	
	public static void delay(int i){
		try{
			Thread.sleep(i*1000);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//adb shell am start -S -R 10 -W amActivityStr(packageName/launchActivity)
	public static double runCommand(String command, boolean flag){
		//System.out.println(command);
		double totaltime = 0.0;
		try{
			Runtime runtime = Runtime.getRuntime();
			Process getSIBaseVersionProcess2  =  runtime.exec(command);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
						getSIBaseVersionProcess2.getInputStream()));
			String SIBaseVersion = " ";
			 while((SIBaseVersion =bufferedReader.readLine())!=null){
				 //System.out.println(SIBaseVersion);
				 if(flag){
					 if(SIBaseVersion.contains("TotalTime")){
						 totaltime = Double.parseDouble(SIBaseVersion.substring(11).trim());
						 //System.out.println("total time:" + totaltime);
					 }else if(SIBaseVersion.contains("total +")){
						 System.out.println("第一次启动时间：" + SIBaseVersion.substring(SIBaseVersion.lastIndexOf(":")+1));
					 }
					 fh.writeDataToFile(resultFile, SIBaseVersion);
				 }else{
					 continue;
				 }
			 }
		}catch(Exception ex){
			ex.printStackTrace();
		}
		 return totaltime;
	}
	
	//多指令
	public static void runCommand(String[] command, boolean flag){
		//System.out.println(command);
		try{
			Runtime runtime = Runtime.getRuntime();
			Process getSIBaseVersionProcess2  =  runtime.exec(command);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
						getSIBaseVersionProcess2.getInputStream()));
			String SIBaseVersion = " ";
			 while((SIBaseVersion =bufferedReader.readLine())!=null){
				 System.out.println(SIBaseVersion);
			 }
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void readFileByLines(File fh) throws ParseException {
			BufferedReader reader = null;  
			try {  
				reader = new BufferedReader(new FileReader(fh));
				String tempString = null;  
				// 一次读入一行，直到读入null为文件结束
				String time = "";
				while ((tempString = reader.readLine()) != null) {
					//判断crash关键字  
					if(tempString.contains("Fatal") && tempString.contains("android.rimet")){
						crashCount++;
					}else if(tempString.contains("not responding") || tempString.contains("FATAL EXCEPTION") || tempString.contains("Uncaught Exception") || tempString.contains("Anr") || tempString.contains("java.lang.NullPointerException") ){// || tempString.contains("VM exiting") ||  tempString.contains("java.lang.RuntimeException: Unable to unbind to service") ){ 
						//System.out.println(tempString);
						String time2 = tempString.substring(6, 14);
						if(time.equals("")){
							crashCount++;
						}else if(format.parse(time2).getTime()-format.parse(time).getTime()>10*1000){
							crashCount++;
						}else{
							//System.out.println(time2 +" 同一个crash不记录");
						}
						time = tempString.substring(6, 14);
					}
				}
				reader.close();  
			} catch (IOException e) {  
				e.printStackTrace();  
			} finally {  
				if (reader != null) {  
					try {  
						reader.close();  
					} catch (IOException e1) {  
						e1.printStackTrace();
					}
				}
			}
	}
	
	public static void transFiles(File dir) throws ParseException{
		File[] files = dir.listFiles();
		for(int i=0;i<files.length;i++){
			readFileByLines(files[i]);
			System.out.println(files[i].getName() + " ： " + crashCount);
		}
	}
	
	public static void crashAnalyze(String args[]) throws ParseException{
		System.out.println("参数：" + args[0]);
		alias = args[0];
		//crash日志处理
		transFiles(new File(driver+alias+"//"));
		System.out.println("crash次数： " + crashCount +"次. crash比例： " + ((float)crashCount/times));
	}
	
	public static void crashCollect(String args[]) throws ParseException{
		System.out.println("参数：" + args[0] + ", " + args[1]+", " + args[2]);
		deviceId = args[0];
		
		if(args[1] !=null){
			times = Integer.parseInt(args[1]);
		}
		alias = args[2];
		int sleepTime = 0;
		if(args.length > 3 ){
			sleepTime = Integer.parseInt(args[3]);
		}
		
		/////crash日志分析
		//文件夹
		fh.deleteDir(new File(driver+alias+"//"));
		fh.newFolder(driver+alias+"//");
		int step = 20;
		System.out.println("开始 ...");
		System.out.println("一共需要运行 " +times/step+ " 次Iteration:");
		for(int i=0;i<times/step;i++){
			for(int j=0;j<step;j++){
				runCommand("adb -s \""+args[0]+"\" logcat -c", false);
				runCommand("adb -s \""+args[0]+"\" shell am start "+ amActivityStr,false);
//				if(alias.contains("huawei")||alias.contains("honor")||alias.contains("nova")){
				if(sleepTime !=0){	
					delay(sleepTime);
				}else{
					delay(8);
				}
				//win10 - win7
				String[] comms = new String[]{"cmd", "/c", "adb -s \""+args[0]+"\" logcat -d >> "+driver+alias+"//logcat"+i+".txt"};
				runCommand(comms ,false);
				runCommand("adb -s \""+args[0]+"\" shell am force-stop "+appContainer,false);
			}
			//runCommand("cmd /c adb -s \""+args[0]+"\" logcat -d > "+driver+alias+"//logcat"+i+".txt",false);
			System.out.println("第 " +(i+1)+ " 次Iteration运行完成...");
		}
		System.out.println("结束.");
		//crash日志处理
		transFiles(new File(driver+alias+"//"));
		System.out.println("crash次数： " + crashCount +"次. crash比例： " + ((float)crashCount/times));
	}
	
	public static void collectTime(String args[]){
		fh.delFile(resultFile);
		//arg[0] 设备id     arg[1] 次数
		System.out.println("参数：" + args[0] + ", " + args[1]);
		deviceId = args[0];
		
		if(args[1] !=null){
			times = Integer.parseInt(args[1]);
		}
		//热启动
		int failTime = 0;
		double totalTime = 0.0;
		runCommand("adb -s \""+args[0]+"\" logcat -c", false);
		for(int i=0;i<times;i++){
			double time = runCommand("adb -s \""+args[0]+"\" shell am start -R 1 -W "+ amActivityStr,true);
			if(i==0){
				System.out.println("第一次启动时间：" + time/1000 + "秒");
			}
			if(time>20000.0){
				failTime++;
			}else{
				totalTime +=time;
			}
			runCommand("adb -s \""+args[0]+"\" shell input keyevent 3",false);//home
		}
		System.out.println("热启动平均时间:" + totalTime/((times-failTime)*1000) + "秒");
		fh.writeDataToFile(resultFile, "=================\r\n=================");
		//冷启动
		failTime = 0;
		totalTime = 0.0;
		runCommand("adb -s \""+args[0]+"\" logcat -c", false);
		for(int i=0;i<times;i++){
			double time =  runCommand("adb -s \""+args[0]+"\" shell am start -R 1 -W "+ amActivityStr,true);
			if(time>20000.0){
				failTime++;
			}else{
				totalTime +=time;
			}
			runCommand("adb -s \""+args[0]+"\" shell am force-stop "+appContainer,false);
		}
		runCommand("cmd /c adb -s \""+args[0]+"\" logcat -d > "+driver + "logcat.txt",false);
		System.out.println("冷启动平均时间:" + totalTime/((times-failTime)*1000) + "秒");
	}
	
	public static void main(String args[]) throws ParseException{
//		collectTime(args);
//		crashCollect(args);
		crashAnalyze(new String[]{"三星S9+"});
	}
	
}
