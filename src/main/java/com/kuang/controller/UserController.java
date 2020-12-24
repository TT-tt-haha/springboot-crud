package com.kuang.controller;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.kuang.mapper.UserMapper;
import com.kuang.pojo.User;
import com.kuang.service.UserService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

//    @RequestMapping("/getUsers")
//    public String getUsers(Model model){
//        List<User> users = userMapper.getUsers();
//        model.addAttribute("users",users);
//        return "show";
//    }

    @RequestMapping("/getUsersByLimit")
//   @ResponseBody
    public  String getUsersByLimit(Model model,@RequestParam(value = "startIndex", defaultValue = "1") int startIndex,
                                  @RequestParam(value = "pageSize", defaultValue = "2") int pageSize){
        PageHelper.startPage(startIndex, pageSize,true);

        List<User> list = userMapper.getUsers();

        PageInfo<User> page = new PageInfo<>(list);
        model.addAttribute("pages", page);

        return "show";

    }

    @RequestMapping("/searchUser")
    public String searchUser(String firstname,Model model,@RequestParam(value = "startIndex", defaultValue = "1") int startIndex,
                             @RequestParam(value = "pageSize", defaultValue = "2") int pageSize){
        PageHelper.startPage(startIndex, pageSize,true);

        List<User> list = userMapper.searchUser(firstname);

        if(list.size()==0){
            PageHelper.startPage(startIndex, pageSize,true);
            list = userMapper.getUsers();
            model.addAttribute("err","查询信息不存在");
        }
        PageInfo<User> page = new PageInfo<>(list);
        //System.out.println(list);
        //List<User> users = new ArrayList<>();

        model.addAttribute("pages",page);
        return "show";
    }

    @RequestMapping("/deleteUser")
    public String deleteUser(Integer id){
        int i = userMapper.deleteUser(id);
        //System.out.println(i);
        return  "redirect:/getUsersByLimit";
    }

    @RequestMapping("/toAdd")
    public String toAdd(){
        return "toAdd";
    }
    @RequestMapping("/addUser")
    public String addUser(User user){
        int i = userMapper.addUser(user);
        System.out.println(i);
        return  "redirect:/getUsersByLimit";
    }

    @RequestMapping("/toUpdate")
    public String toUpdate(Integer id,Model model){
        User user = userMapper.getUser(id);
        model.addAttribute("user",user);
        return "toUpdate";
    }

    @RequestMapping("/updateUser")
    public String updateUser(User user){
        int i = userMapper.updateUser(user);
        System.out.println(i);
        return "redirect:/getUsersByLimit";
    }



    @RequestMapping("/login")
    public String loginController(String username, String pwd, Model model, HttpSession session){
        if("admin".equals(username) && "123456".equals(pwd)){
            session.setAttribute("loginUser",username);
            return "redirect:/getUsersByLimit";
        }else {
            model.addAttribute("msg","用户名或密码错误");
            return "index";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("loginUser");
        return "index";
    }

    @RequestMapping("/export")
    public String exportController(HttpServletResponse response) throws IOException {
        List<User> users = userMapper.getUsers();
        String fileName = "users" + ".xls";
        //创建一个工作簿
        Workbook workbook = new HSSFWorkbook();
        //创建一个工作表
        Sheet sheet = workbook.createSheet();
        //创建一个行
        Row row = sheet.createRow(0);
        row.setHeight((short) (22.50 * 20));
        int rowNum = 1;
        String [] headers = {"id","firstname","lastname","email"};
        for (int i = 0;i<headers.length;i++){
            Cell cell = row.createCell((short) i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        for (User user : users) {
            Row row1 = sheet.createRow(rowNum);
            row1.createCell((short) 0).setCellValue(user.getId());
            row1.createCell((short) 1).setCellValue(user.getFirstname());
            row1.createCell((short) 2).setCellValue(user.getLastname());
            row1.createCell((short) 3).setCellValue(user.getEmail());
            rowNum++;
        }
        sheet.setDefaultRowHeight((short) (16.5 * 20));
        //列宽自适应
        for (int i = 0; i <= 13; i++) {
            sheet.autoSizeColumn(i);
        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
        return "redirect:/getUsersByLimit";
    }

    @RequestMapping(value = "/uploadPage")
    public String uploadPageController() {
        return "/uploadPage";
    }
    @RequestMapping("/import")
    public String importController(@RequestParam(value = "filename") MultipartFile filename, HttpSession session){

        boolean a = false;
        String fileName = filename.getOriginalFilename();
        try {
            a = userService.batchImport(fileName, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:getUsersByLimit";

    }
}
