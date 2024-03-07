package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Person;
import com.example.service.PersonService;
import com.example.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.util.Generate;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import com.example.exception.CustomException;
import cn.hutool.core.util.StrUtil;

import javax.annotation.Resource;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/person")
public class PersonController {
    @Resource
    private PersonService personService;
    @Resource
    private HttpServletRequest request;
    private Generate AccountGenerate;

    public User getUser() {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomException("-1", "请登录");
        }
        return user;
    }

    @PostMapping
    public Result<?> save(@RequestBody Person person) {
        String account = AccountGenerate.getAccount();
        return Result.success(personService.save(person));
    }

    @PutMapping
    public Result<?> update(@RequestBody Person person) {
        return Result.success(personService.updateById(person));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        personService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(personService.getById(id));
    }

    @GetMapping
    public Result<?> findAll() {
        return Result.success(personService.list());
    }

    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Person> query = Wrappers.<Person>lambdaQuery().orderByDesc(Person::getId);
        if (StrUtil.isNotBlank(name)) {
            query.like(Person::getUserName, name);
        }
        return Result.success(personService.page(new Page<>(pageNum, pageSize), query));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        List<Map<String, Object>> list = CollUtil.newArrayList();

        List<Person> all = personService.list();
        for (Person obj : all) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("账户", obj.getUserAccount());
            row.put("用户角色", obj.getUserIdentify());
            row.put("用户姓名", obj.getUserName());
            row.put("用户密码", obj.getUserPassword());
            row.put("手机号码", obj.getUserPhone());
            row.put("用户性别", obj.getUserSex());

            list.add(row);
        }

        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(System.out);
    }

    @GetMapping("/upload/{fileId}")
    public Result<?> upload(@PathVariable String fileId) {
        String basePath = System.getProperty("user.dir") + "/src/main/resources/static/file/";
        List<String> fileNames = FileUtil.listFileNames(basePath);
        String file = fileNames.stream().filter(name -> name.contains(fileId)).findAny().orElse("");
        List<List<Object>> lists = ExcelUtil.getReader(basePath + file).read(1);
        List<Person> saveList = new ArrayList<>();
        for (List<Object> row : lists) {
            Person obj = new Person();
            obj.setUserAccount((String) row.get(1));
            obj.setUserIdentify((String) row.get(2));
            obj.setUserName((String) row.get(3));
            obj.setUserPassword((String) row.get(4));
            obj.setUserPhone((String) row.get(5));
            obj.setUserSex((String) row.get(6));

            saveList.add(obj);
        }
        personService.saveBatch(saveList);
        return Result.success();
    }

}
