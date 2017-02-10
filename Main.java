package io.github.cshu.detecttextfileencoding;

import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.*;

public class Main extends Application {
	List<String> up;
	Map<String,String> np;
	public FileInputStream mkfis()throws Exception{
		if(np.containsKey("in")){
			File jf=new File(np.get("in"));
			if(jf.isFile())return new FileInputStream(jf);
			else System.err.println("Input file doesn't exist. Reading stdin.");
		}
		return null;
	}
	public PrintStream mkouts()throws Exception{
		if(np.containsKey("utf8out")){
			return new PrintStream(new File(np.get("utf8out")),"UTF8");
		}
		return null;
	}
	@Override
	public void start(Stage stage) throws Exception {
		up=this.getParameters().getUnnamed();
		np=this.getParameters().getNamed();
		try(
			FileInputStream fis=mkfis();
			PrintStream outs=mkouts();
		){
			if(fis!=null)System.setIn(fis);
			if(outs!=null)System.setOut(outs);
			//todo add option to display the utf8 output result on gui
			//todo add option to try shifting (discard first N bytes) to find possible decoding
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte[] bytebuf=new byte[0x1000];
			for(;;){
				int numofb=System.in.read(bytebuf);
				if(numofb==-1)break;
				buffer.write(bytebuf,0,numofb);
			}
			buffer.flush();
			bytebuf=buffer.toByteArray();
			for(Map.Entry<String,Charset> ent:Charset.availableCharsets().entrySet()){
				System.out.println();
				System.out.print(ent.getKey());
				CharBuffer cb;
				try{
					cb=ent.getValue().newDecoder().decode(ByteBuffer.wrap(bytebuf));
				}catch(Exception e){
					System.out.println(" failed when decoding.");
					continue;
				}
				System.out.print(" -> ");
				System.out.append(cb);
				System.out.println();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Platform.exit();
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
}
