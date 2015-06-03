package codingpark.net.cheesecloud.view.dummy.utils;

import java.io.File;

import android.widget.ImageView;

import codingpark.net.cheesecloud.R;

public class ContentntIsFile {
	 public  static final int TAB_File_IS_IMAGER1               = 1;
	 public  static final int  TAB_File_IS_VIEW               = 2;
	 public  static final int  TAB_File_IS_MUSIC               = 3;
	 public  static final int  TAB_File_IS_file               = 4;
	 public static final int TAB_File_IS_Full                 =5;
	 public static String [][]  MIME_MapTable={   
         //{后缀名，MIME类型}    
         {".3gp",    "ft_gp"},   
         {".apk",    "ft_apk"},   
         {".asf",    "ft_asf"},   
         {".avi",    "ft_avi"},   
         {".bin",    "ft_bin"},   
         {".bmp",    "ft_bmp"},   
         {".cpp",    "ft_cpp"},   
         {".doc",    "ft_doc"},   
         {".docx",   "ft_docx"},  
         {".xls",    "ft_xls"},    
         {".xlsx",   "ft_xlsx"},   
         {".gif",    "ft_fig"},   
         {".html",   "ft_html"},
         {".java",   "ft_java"},   
         {".jpeg",   "ft_jpg"},  
         {".jpg",    "ft_jpg"},   
         {".log",    "ft_log "},  
         {".m4a",    "ft_m4a"},
         {".m4v",    "ft_m4a"},    
         {".mov",    "ft_mov"},  
         {".mp2",    "ft_mp2"},
         {".mob",    "ft_mod"},
         {".mp3",    "ft_mp3"},   
         {".mp4",    "ft_mp4"},   
         {".mpc",    "ft_mp2"},        
         {".mpe",    "ft_mpeg"},     
         {".mpeg",   "ft_mpeg"},     
         {".mpg",    "ft_mpeg"},     
         {".mpg4",   "ft_mp4 "},    
         {".mpga",   "ft_mpeg"},   
         {".msp",    "ft_msp "},    
         {".ogg",    "ft_ogg"},    
         {".pdf",    "ft_pdf"},   
         {".png",    "ft_png"},  
         {".ppt",    "ft_ppt"},   
         {".pptx",   "ft_pptx"},   
         {".rmvb",   "ft_rmvb"},   
         {".rtf",    "ft_rtf "},  
         {".txt",    "ft_txt"}, 
         {".wav",    "ft_wav"},   
         {".wma",    "ft_wma"},
         {".wmv",    "ft_wmv"},   
         {".wp/",    "ft_wpl "},  
         {".xml",    "ft_xml "}, 
         {".zip",    "ft_zip"},   
         {"",        "*/*"}     
     };
	 
	 public static String getFileType(File file ) {   
         
	      String type="*/*";   
	      String fName = file.getName();   
	      //获取后缀名前的分隔符"."在fName中的位置。    
	      int dotIndex = fName.lastIndexOf(".");   
	      if(dotIndex < 0){   
	          return type;   
	      }   
	      /* 获取文件的后缀名*/   
	      String end=fName.substring(dotIndex,fName.length()).toLowerCase();   
	      if(end=="")return type;   
	      //在MIME和文件类型的匹配表中找到对应的MIME类型。    
	      for(int i=0;i<MIME_MapTable.length;i++){   

	      if(end.equals(MIME_MapTable[i][0]))   
	          type = MIME_MapTable[i][1];   
	  }          
	  return type;   
	}  
	 
	 public static int isFileType(String fileName){
    	 int typeIndex = fileName.lastIndexOf(".");
         String fileType ;
         fileType= fileName.substring(typeIndex + 1).toLowerCase();
         if (typeIndex != -1) {
         	fileType= fileName.substring(typeIndex + 1).toLowerCase();
         	if (fileType != null&& (fileType.equals("jpg") || fileType.equals("gif")
                     || fileType.equals("png") || fileType.equals("jpeg")
                     || fileType.equals("bmp") || fileType.equals("wbmp")
                     || fileType.equals("ico") || fileType.equals("jpe")
                     )) {
         			return ContentntIsFile.TAB_File_IS_IMAGER1;
         		}else if(fileType != null&& (fileType.equals("flv") || fileType.equals("rmvb")
                        || fileType.equals("m4v") || fileType.equals("avi")
                        || fileType.equals("mp4") || fileType.equals("rmvb")
                        || fileType.equals("3gp ") || fileType.equals("mov")
                        || fileType.equals("rm")   || fileType.equals("mts")
                        || fileType.equals("mkv")  || fileType.equals("vob")
                        || fileType.equals("asf")  || fileType.equals("mpg")
                        || fileType.equals("wmv")
                        )){
         			return ContentntIsFile.TAB_File_IS_VIEW;
         		}else if(fileType.equals("mp3")){
         			return ContentntIsFile.TAB_File_IS_MUSIC;
         		}else{
         			return ContentntIsFile.TAB_File_IS_file;
         		}
         }
		return ContentntIsFile.TAB_File_IS_Full;
    }
	 
}
