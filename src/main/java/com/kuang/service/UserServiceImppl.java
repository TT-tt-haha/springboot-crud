package com.kuang.service;

import com.kuang.common.MyException;
import com.kuang.mapper.UserMapper;
import com.kuang.pojo.User;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceImppl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean batchImport(String fileName, MultipartFile file) throws Exception {
        boolean notNull = false;
        List<User> userList = new ArrayList<>();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            throw new MyException("上传文件格式不正确");
        }
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        InputStream is = file.getInputStream();
        Workbook wb = null;
        if (isExcel2003) {
            wb = new HSSFWorkbook(is);
        } else {
            wb = new XSSFWorkbook(is);
        }
        Sheet sheet = wb.getSheetAt(0);
        if(sheet!=null){
            notNull = true;
        }
        User user;
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {//r = 2 表示从第三行开始循环 如果你的第三行开始是数据
            Row row = sheet.getRow(r);//通过sheet表单对象得到 行对象
            if (row == null){
                continue;
            }
            //sheet.getLastRowNum() 的值是 10，所以Excel表中的数据至少是10条；不然报错 NullPointerException
            user = new User();
//            if( row.getCell(0).getCellType() !=1){//循环时，得到每一行的单元格进行判断
//                throw new MyException("导入失败(第"+(r+1)+"行,用户名请设为文本格式)");
//            }
//            int id = row.getCell(0).getStringCellValue();//得到每一行第一个单元格的值
//            if(username == null || username.isEmpty()){//判断是否为空
//                throw new MyException("导入失败(第"+(r+1)+"行,用户名未填写)");
//            }
            row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);//得到每一行的 第二个单元格的值
            String firstname = row.getCell(1).getStringCellValue();
            if(firstname==null || firstname.isEmpty()){
                throw new MyException("导入失败(第"+(r+1)+"行,firstname未填写)");
            }
            row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);//得到每一行的 第二个单元格的值
            String lastname = row.getCell(2).getStringCellValue();
            if(lastname==null || lastname.isEmpty()){
                throw new MyException("导入失败(第"+(r+1)+"行,lastname)");
            }
            row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);//得到每一行的 第二个单元格的值
            String email = row.getCell(3).getStringCellValue();
            if(email==null || email.isEmpty()){
                throw new MyException("导入失败(第"+(r+1)+"行,email)");
            }
            //完整的循环一次 就组成了一个对象
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setEmail(email);
            userList.add(user);
        }
        for (User user1 : userList) {
            userMapper.addUser(user1);
        }
//        for (User userResord : userList) {
//            String name = userResord.getUsername();
//            int cnt = userMapper.selectByName(name);
//            if (cnt == 0) {
//                userMapper.addUser(userResord);
//                System.out.println(" 插入 "+userResord);
//            } else {
//                userMapper.updateUserByName(userResord);
//                System.out.println(" 更新 "+userResord);
//            }
//        }
        return notNull;

    }


}
