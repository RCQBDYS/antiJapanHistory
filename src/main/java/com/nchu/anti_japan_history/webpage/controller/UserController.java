package com.nchu.anti_japan_history.webpage.controller;

import com.nchu.anti_japan_history.utils.RandomUtils;
import com.nchu.anti_japan_history.webpage.entity.User;
import com.nchu.anti_japan_history.webpage.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.rmi.server.ServerCloneException;
import java.util.List;

/**
 * @Author: wangshen
 * @Date: 2020/4/6
 * @Description: 登录、注册控制类
 */
@Controller
public class UserController {
    //服务类
    @Autowired
    UserService userService;
    @Autowired
    AntiHistoryService antiHistoryService;//抗战历史
    @Autowired
    AntiSiteService antiSiteService;//抗战遗址
    @Autowired
    AntiWarStoriesService antiWarsStoriesService;//抗战故事
    @Autowired
    AntiJapanPersonService antiJapanPersonService;//抗战人物
    @Autowired
    InternationalFriendService internationalFriendService;//国际友人
    @Autowired
    PeriodicalService periodicalService;//期刊
    @Autowired
    NewspaperService newspaperService;//报纸
    @Autowired
    BookService bookService;//书籍
    @Autowired
    PictureService pictureService;//图片

    //配置文件中的图片保存路径
    @Value("${cbs.imagesPath}")
    private String ImagesPath;

    //日志
    Logger logger = LoggerFactory.getLogger(indexController.class);

    //向注册页面跳转
    @GetMapping("/enroll")
    public String skipEnroll() {
        return "enroll";
    }

    //进行用户名的筛选
    @GetMapping("/checkout")
    public void checkout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServerCloneException, IOException {
        String userName = httpServletRequest.getParameter("UserName");
        httpServletRequest.setCharacterEncoding("utf-8");
        httpServletResponse.setCharacterEncoding("utf-8");
        List<User> result = userService.selectUserName(userName);
        if(result.isEmpty()){
            httpServletResponse.getWriter().write("");
        }else{
            httpServletResponse.getWriter().write("用户已存在");
        }


    }

    //用户注册，头像上传
    @PostMapping("/enrollSave")
    public String saveEnroll(@RequestParam(value = "userPicture") MultipartFile multipartFile,
                             @RequestParam(value = "userName") String userName,
                             @RequestParam(value = "userPassword") String userPassword,
                             @RequestParam(value = "userPower") Integer userPower,
                             HttpServletRequest httpServletRequest) {
        //注册与否的标志,message传递信息
        int flag = 0;
        User user = new User();
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setUserPower(userPower);
        //通过对imagesPath路径的截取，以获得图片存储的文件路径
        String parentDirPath = ImagesPath.substring(ImagesPath.indexOf(':') + 1, ImagesPath.length());
        //logger.info("parentDirPath=" + parentDirPath);
        //获取图片名称
        String originalFilename = multipartFile.getOriginalFilename();
        //logger.info("originalFilename=" + originalFilename);
        //生成随机图片前缀+图片名称
        String pictureName = RandomUtils.random() + originalFilename;
        //logger.info("fileName=" + pictureName);
        //将路径转变为文件存储路径格式
        File parentDir = new File(parentDirPath);
        //图片的访问路径
        String userPicture = "images" + "\\" + pictureName;
        user.setUserPicture(userPicture);
        //logger.info("parentDir = " + parentDir);
        //logger.info("userPicture = " + userPicture);

        if (!parentDir.exists()) //如果那个目录不存在先创建目录
        {
            parentDir.mkdir();
        }
        try {
            //全局配置文件中配置的目录加上文件名,将用户选择的图片，写入磁盘中
            multipartFile.transferTo(new File(parentDirPath + pictureName));
            flag = 1;
        } catch (IOException e) {
            flag = 0;
            e.printStackTrace();
        }
        //通过flag判断是否注册成功，并使用message传递信息
        if (flag == 0) {
            httpServletRequest.setAttribute("message", flag);
            return "enroll";
        } else {
            userService.save(user);
            httpServletRequest.setAttribute("message", flag);
            return "login";
        }
    }

    //用户修改用户信息保存
    @PostMapping("/userUpdate")
    public String userMessageUpdate(@RequestParam(value = "userPicture") MultipartFile multipartFile,
                                    @RequestParam(value = "userName") String userName,
                                    @RequestParam(value = "userPassword") String userPassword,
                                    @RequestParam(value = "userId") int userId, HttpSession session){
        User user1 = new User();
        user1.setUserName(userName);
        user1.setUserPassword(userPassword);
        user1.setUserId(userId);
        //图片的保存
        String parentDirPath = ImagesPath.substring(ImagesPath.indexOf(':') + 1, ImagesPath.length());
        String originalFilename = multipartFile.getOriginalFilename();
        String pictureName = RandomUtils.random() + originalFilename;
        File parentDir = new File(parentDirPath);
        String userPicture = "images" + "\\" + pictureName;
        user1.setUserPicture(userPicture);
        int flag = 0;
        if (!parentDir.exists()) //如果那个目录不存在先创建目录
        {
            parentDir.mkdir();
        }
        try {
            //全局配置文件中配置的目录加上文件名,将用户选择的图片，写入磁盘中
            multipartFile.transferTo(new File(parentDirPath + pictureName));
            flag = 1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (flag == 1){
            userService.update(user1);
            User user = userService.login(userName,userPassword);
            session.removeAttribute("user");
            session.setAttribute("user",user);
            return "userUnit";
        }else{
            logger.info("修改失败！");
            return "userUnit";
        }
    }

    //已注册用户进行登录
    @PostMapping("/loginData")
    public String loginData(String userName, String userPassword, HttpServletRequest httpServletRequest) {
        //logger.info("userName=" + userName + " userPassword = " + userPassword);
        User user = userService.login(userName, userPassword);
        //logger.info("user = "+ user);
        if (user == null) {
            //logger.info("登录出错");
            httpServletRequest.getSession().setAttribute("loginMsg", "账户或者密码错误");
            return "login";
        } else {
            httpServletRequest.getSession().setAttribute("user", user);
            return "index";
        }
    }

    //用户进入个人中心
    @GetMapping("/unit")
    public String skipUserUnit(Model model){
        //List<AntiHistory> antiHistoryTable = antiHistoryService;
        return "userUnit";
    }







}
