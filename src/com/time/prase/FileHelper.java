package com.time.prase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
	String filepath = "";

	public FileHelper(String filepath) {
		this.filepath = filepath;
	}

	public FileHelper() {
	}

	public File createFile(String fileName) {
		File txtFile = new File(fileName);
		try {
			if (!txtFile.exists()) {
				txtFile.createNewFile();
			} else {
				txtFile.delete();
				txtFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return txtFile;
		
	}
	
	public void delFile(String filename){
		File fi = new File(filename);
		if(fi.exists())
			fi.delete();
	}

	public File createResultFile(String fileName) {
		// create file to write test result
		File file = new File(File.separator + "data" + File.separator + "local"
				+ File.separator + "tmp" + File.separator + "uiauto"
				+ File.separator);
		if (!file.exists()) {
			file.mkdir();
		}

		File txtFile = new File(File.separator + "data" + File.separator
				+ "local" + File.separator + "tmp" + File.separator + "uiauto"
				+ File.separator + fileName);
		try {
			if (!txtFile.exists()) {
				txtFile.createNewFile();
			} else {
				txtFile.delete();
				txtFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return txtFile;
	}

	public void writeDataToFile(String fileName, String data) {
		BufferedWriter fw = null;
		try {
			fw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(fileName), true), "UTF-8"));
			fw.write("");
			fw.append(data);
			fw.newLine();
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String readMailTemplate() {
		int len = 0;
		StringBuffer str = new StringBuffer("");
		File file = new File(filepath);
		System.out.println(file.getAbsolutePath());
		try {
			FileInputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is,"UTF-8");
			BufferedReader in = new BufferedReader(isr);
			String line = null;
			while ((line = in.readLine()) != null) {
				if (len != 0) // 处理换行符的问题
				{
					str.append(System.getProperty("line.separator") + line);

				} else {
					str.append(line);
				}
				len++;
			}
			in.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return str.toString();
	}
	
	//push file log
	public static void saveToFile(String text, String path, boolean isClose) {
		File file = new File(path);
		BufferedWriter bf = null;
		try {
			FileOutputStream outputStream = new FileOutputStream(file, true);
			OutputStreamWriter outWriter = new OutputStreamWriter(outputStream);
			bf = new BufferedWriter(outWriter);
			bf.append(text);
			bf.newLine();
			bf.flush();

			if (isClose) {
				bf.close();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<String> readDataToList(String filePath){
		String line;
		List<String> array = new ArrayList<String>();
		File file = new File(filePath);
		if (!file.exists()) 
			System.out.println("无法找到文件：" + filePath);
		try{
			InputStream is = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			while ((line = reader.readLine()) != null) {		  //   这一行有毛病,因为只读取第一行		  
				array.add(line);				//这一行也有毛病，这里添加也错了， 但是要怎么写呢？ 请教高手
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return array;
	}
	
	public void newFolder(String folderPath) {
		try {
		String filePath = folderPath;
		File myFilePath = new File(filePath);
		if (!myFilePath.exists()) {
		myFilePath.mkdir();
		}
		} catch (Exception e) {
		System.out.println("新建文件夹操作出错");
		e.printStackTrace();
		}
		}
	
	public  boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();	
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
