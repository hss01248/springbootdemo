package com.hss01248.anywhere;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String index(){
        return "Hello World";
    }

    @RequestMapping(value="/uploadImgs",method= RequestMethod.POST)
    public Map uploadImgs(@RequestParam("file1")MultipartFile file1,
                          @RequestParam("file2")MultipartFile file2,
                          @RequestParam String name, HttpServletRequest request){// @RequestParam("file2") MultipartFile file2,

        print(name);
        String fileName1 = file1.getOriginalFilename();
        print(fileName1);
        String savePath = request.getSession().getServletContext().getRealPath("imgUpload/");
        print(savePath);
        String fileName2 = file2.getOriginalFilename();
        print(fileName2);
        Map map = new HashMap();
        try {
            uploadFile(file1.getBytes(),savePath,fileName1);
            uploadFile(file2.getBytes(),savePath,fileName2);
            map.put("success",true);
        }catch (Exception e){
            e.printStackTrace();
            map.put("success",false);
            map.put("errMsg",e.getMessage());
            return map;
        }
        return map;
    }

    private void print(String str){
        System.out.print(str+"\n");
    }

    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }
}
