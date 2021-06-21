package com.fsc.fscmonitor.controller;

import com.fsc.fscmonitor.core.FileOper;
import com.fsc.fscmonitor.enums.Content;
import com.fsc.fscmonitor.enums.ResultCode;
import com.fsc.fscmonitor.model.FileDto;
import com.fsc.fscmonitor.model.GeneralResponse;
import com.fsc.fscmonitor.util.PropertiesUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpController {
    private Logger log = Logger.getLogger(HttpController.class);

    @RequestMapping(uri = "/createfileall")
    public GeneralResponse createfileall(@RequestBody Map<String,String> params) {
        String path = "";
        if(params!=null){
            if(params.get("fold")!=null){
                path = params.get("fold").toString();
            }
        }
        //生成文件
        List<File> filelist = new ArrayList<>();
        List<File> list = FileOper.getFileList(PropertiesUtils.getStringValue(Content.MONITOR_ADDRESS)+File.separator+path, filelist);
        File file = new File(PropertiesUtils.getStringValue(Content.ADDRESS) + Content.FILEDATE);
        if (file.exists()) {
            file.delete();
        }
        list.forEach( f ->
        FileOper.write(System.currentTimeMillis() + "---ENTRY_MODIFY---"
                + f.getPath() + Content.ENTRY, Content.FILEDATE));

        return new GeneralResponse(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getName());
    }

    @RequestMapping(uri = "/fileall")
    public GeneralResponse fileall() {
        //返回文件列表
        List<File> filelist = new ArrayList<>();
        List<File> list = FileOper.getFileList(PropertiesUtils.getStringValue(Content.MONITOR_ADDRESS), filelist);
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
                path = PropertiesUtils.getStringValue(Content.MONITOR_ADDRESS) + File.separator + params.get("fold").toString();
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
