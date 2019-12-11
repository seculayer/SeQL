package com.seculayer.seql.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtil {

	public static File[] listFiles(File dir) {
	    String[] ss = dir.list();
	    if (ss == null)
	        return null;
	    int n = ss.length;
	    File[] fs = new File[n];
	    for(int i = 0; i < n; i++) {
	        fs[i] = new File(dir.getPath(), ss[i]);
	    }
	    return fs;
    }
	
	public static File[] listFiles(File dir, String fileType) {
		ArrayList<String> fileList = new ArrayList<String>();
	    String[] ss = dir.list();
	    if (ss == null)
	        return null;

	    for(int i = 0; i < ss.length; i++){
			String fileName = ss[i];
			if( fileName.indexOf(fileType) == -1 ) {
				continue;
			}
			fileList.add(fileName);
		}
	    
	    Collections.sort(fileList);
	    
	    File[] fs = new File[fileList.size()];
	    for(int i = 0; i < fileList.size(); i++) {
	    	String fname = (String)fileList.get(i);
	        fs[i] = new File(dir.getPath(), fname);
	    }
	    return fs;
    }
	
	public static File[] listFiles(File dir, String fileType, String exceptName) {
		ArrayList<String> fileList = new ArrayList<String>();
	    String[] ss = dir.list();
	    if (ss == null)
	        return null;

	    for(int i = 0; i < ss.length; i++){
			String fileName = ss[i];
			if( fileName.indexOf(fileType) == -1 || fileName.equals(exceptName)) {
				continue;
			}
			fileList.add(fileName);
		}
	    
	    Collections.sort(fileList);
	    
	    File[] fs = new File[fileList.size()];
	    for(int i = 0; i < fileList.size(); i++) {
	    	String fname = (String)fileList.get(i);
	        fs[i] = new File(dir.getPath(), fname);
	    }
	    return fs;
    }
	
	public static File[] listFiles2(File dir, String fileType) {
		ArrayList<File> fileList = new ArrayList<File>();
		File[] ss = dir.listFiles();
	    if (ss == null)
	        return null;

	    for(int i = 0; i < ss.length; i++){
			File file = ss[i];
			if( file.getName().indexOf(fileType) == -1 ) {
				continue;
			}
			fileList.add(file);
		}
	    
	    File[] files = new File[fileList.size()];
	    fileList.toArray(files);
		Arrays.sort(files, new Comparator<Object>() {
			public int compare(final Object o1, final Object o2) {
				return new Long(((File) o1).lastModified()).compareTo(new Long(((File) o2).lastModified()));
			}
		});
		return files;
    }
	
	public static ArrayList<String> listFileNames(File dir, String fileType) {
		ArrayList<String> fileList = new ArrayList<String>();
		String[] ss = dir.list();
		if (ss == null)
			return null;
		
		for(int i = 0; i < ss.length; i++){
			String fileName = ss[i];
			if( fileName.indexOf(fileType) == -1 ) {
				continue;
			}
			fileList.add(fileName);
		}
		
		Collections.sort(fileList);

		return fileList;
	}
	
	public static List<String> getFileExtNames(Object obj, String ext) {		
		List<String> list = new ArrayList<String>();
		
		try {
			File dir = null;
			if(obj instanceof File) {
				dir = (File) obj;
			} else if(obj instanceof String) {
				dir = new File((String)obj);
			}
			
			if(!dir.isDirectory()) return null;
			
			File[] arr = dir.listFiles();
			for(File f:arr) {
				if(f.isFile() && f.getName().endsWith("."+ext)) list.add(f.getName());
			}			
		} catch (Exception ex) {
			return null;
		}
		return list;
	}
	
	public static List<String> getFilePrefixNames(Object obj, String prefix) {		
		List<String> list = new ArrayList<String>();
		
		try {
			File dir = null;
			if(obj instanceof File) {
				dir = (File) obj;
			} else if(obj instanceof String) {
				dir = new File((String)obj);
			}
			
			if(!dir.isDirectory()) return null;
			
			File[] arr = dir.listFiles();
			for(File f:arr) {
				if(f.isFile() && f.getName().startsWith(prefix)) list.add(f.getName());
			}			
		} catch (Exception ex) {
			return null;
		}
		return list;
	}
	
	public static List<String> getFileIncludeNames(Object obj, String include) {		
		List<String> list = new ArrayList<String>();
		
		try {
			File dir = null;
			if(obj instanceof File) {
				dir = (File) obj;
			} else if(obj instanceof String) {
				dir = new File((String)obj);
			}
			
			if(!dir.isDirectory()) return null;
			
			File[] arr = dir.listFiles();
			for(File f:arr) {
				if(f.isFile() && f.getName().indexOf(include)>=0) list.add(f.getName());
			}			
		} catch (Exception ex) {
			return null;
		}
		return list;
	}
	
	public static List<String> getFileExcludeNames(Object obj, String exclude) {		
		List<String> list = new ArrayList<String>();
		
		try {
			File dir = null;
			if(obj instanceof File) {
				dir = (File) obj;
			} else if(obj instanceof String) {
				dir = new File((String)obj);
			}
			
			if(!dir.isDirectory()) return null;
			
			File[] arr = dir.listFiles();
			for(File f:arr) {
				if(!f.isFile() || f.getName().indexOf(exclude)>=0) continue;
				list.add(f.getName());
			}			
		} catch (Exception ex) {
			return null;
		}
		return list;
	}
	
	public static List<String> getFileNames(Object obj) {		
		List<String> list = new ArrayList<String>();
		
		try {
			File dir = null;
			if(obj instanceof File) {
				dir = (File) obj;
			} else if(obj instanceof String) {
				dir = new File((String)obj);
			}
			
			if(!dir.isDirectory()) return null;
			
			File[] arr = dir.listFiles();
			for(File f:arr) {
				if(f.isFile()) list.add(f.getName());
			}
			
		} catch (Exception ex) {
			return null;
		}
		return list;
	}
	
	public static int getFileCounts(Object obj) {		
		int fileCnt = 0;
		
		try {
			File dir = null;
			if(obj instanceof File) {
				dir = (File) obj;
			} else if(obj instanceof String) {
				dir = new File((String)obj);
			}
			
			if(!dir.isDirectory()) return -1;
			
			File[] arr = dir.listFiles();
			for(File f:arr) {
				if(f.isFile()) fileCnt++;
			}
			
		} catch (Exception ex) {
			return -1;
		}
		return fileCnt;
	}
	
	public static int getFileLines(Object obj, boolean nullCheck) {		
		int lineCnt = 0;
		BufferedReader br = null;
		
		try {
			File file = null;
			if(obj instanceof File) {
				file = (File) obj;
			} else if(obj instanceof String) {
				file = new File((String)obj);
			}
			
			if(!file.isFile()) return -1;
			
			br = new BufferedReader(new FileReader(file));
			String line;
			while((line=br.readLine())!=null) {
				if(nullCheck && line.trim().length()==0) continue;
				lineCnt++;
			}
		} catch (Exception ex) {
			return -1;
		} finally {
			try { if(br!=null) br.close(); } catch(Exception ex) {}
		}
		return lineCnt;
	}
	
	public static String getFileContents(Object obj) {		
		StringBuffer buffer = new StringBuffer();
		BufferedReader br = null;
		
		try {
			File file = null;
			if(obj instanceof File) {
				file = (File) obj;
			} else if(obj instanceof String) {
				file = new File((String)obj);
			}
			br = new BufferedReader(new FileReader(file));
			String line;
			int cnt = 0;
			while((line=br.readLine())!=null) {
				if(cnt>0) buffer.append(System.lineSeparator());
				buffer.append(line);
				cnt++;
			}
		} catch (Exception ex) {
			return null;
		} finally {
			try { if(br!=null) br.close(); } catch(Exception ex) {}
		}
		return buffer.toString();
	}
	
	public static void main(String[] args) {
		int contents = getFileCounts("d://tmp");
		System.out.println("contents====>" + contents);
	}
}
