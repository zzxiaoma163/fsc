package com.fsc.fscserver.controller;

import com.fsc.fscserver.core.FileOper;
import com.fsc.fscserver.enums.Content;
import com.fsc.fscserver.enums.ResultCode;
import com.fsc.fscserver.model.FileDto;
import com.fsc.fscserver.model.GeneralResponse;
import com.fsc.fscserver.util.PropertiesUtils;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpController {
    private Logger log = Logger.getLogger(HttpController.class);

    @RequestMapping(uri = "/fileall")
    public GeneralResponse fileall() {
        //返回文件列表
        List<File> filelist = new ArrayList<>();
        List<File> list = FileOper.getFileList(PropertiesUtils.getStringValue(Content.FILEPATH), filelist);
        List<FileDto> filedtolist = new ArrayList<>();
        for (File f : list) {
            FileDto fileDto = new FileDto();
            fileDto.setPath(f.getPath());
            try (InputStream is = new FileInputStream(f)) {
                fileDto.setMd5(DigestUtils.md5Hex(is));
            } catch (FileNotFoundException e) {
                log.error("file not find:" + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            filedtolist.add(fileDto);
        }
        return new GeneralResponse(filedtolist);
    }

    @RequestMapping(uri = "/filebyfold")
    public GeneralResponse filebyfold(@RequestBody Map<String,String> params) {
        String path = "";
        if(params!=null){
            if(params.get("fold")!=null){
                path = PropertiesUtils.getStringValue(Content.FILEPATH) + File.separator + params.get("fold").toString();
            }
        }
        if("".equals(path)){
            return new GeneralResponse(ResultCode.NOFOLD.getCode(), ResultCode.NOFOLD.getName());
        }
        File filepath = new File(path);
        if (!filepath.exists()) {
            return new GeneralResponse(ResultCode.NOFOLD.getCode(), ResultCode.NOFOLD.getName());
        } else {
            //返回文件列表
            List<File> filelist = new ArrayList<>();
            List<File> list = FileOper.getFileList(path, filelist);
            List<FileDto> filedtolist = new ArrayList<>();
            for (File f : list) {
                FileDto fileDto = new FileDto();
                fileDto.setPath(f.getPath());
                try (InputStream is = new FileInputStream(f)) {
                    fileDto.setMd5(DigestUtils.md5Hex(is));
                } catch (FileNotFoundException e) {
                    log.error("file not find:" + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                filedtolist.add(fileDto);
            }
            return new GeneralResponse(filedtolist);
        }
    }
}
