package com.lianshidai.bcebe.Controller;


import com.lianshidai.bcebe.Pojo.JsonResult;
import com.lianshidai.bcebe.Service.Impl.FileSendingEmail;
import com.lianshidai.bcebe.Service.Impl.SaveFileToLocal;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
//接收文件
@RestController
@Validated
public class AcceptFile {
    @Resource
    private FileSendingEmail fileSendingEmail;
    @Resource
    private SaveFileToLocal saveFileToLocal;
    @RequestMapping(value = "/file",method = POST)
    public JsonResult<String> upload( @RequestParam("file") MultipartFile file, @NotBlank(message = "部门不能为空") @RequestParam("department") String department) {
        fileSendingEmail.sendEmailWithAttachment(department, file);
        return new JsonResult<>(200,saveFileToLocal.upload(file));
    }
}
