package fit.example.login5.controller;

import fit.example.login5.dao.UserDAO;
import fit.example.login5.model.UserBean;
import fit.example.login5.utils.ImageCodeUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@WebServlet("/5/login")
public class LoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> imageCode = ImageCodeUtils.genImageCode();
        String code = imageCode.get("code").toString();
        BufferedImage buffImg = (BufferedImage) imageCode.get("image");
        //登录成功创建一个Session对象，记录当前连接
        HttpSession session = req.getSession(true);
        session.setAttribute("verify_code", code);
        // 将上面图片输出到浏览器 ImageIO
        ImageIO.setUseCache(false);// 防止Tomcat缓存
        ImageIO.write(buffImg, "png", resp.getOutputStream()); //注意有些情况修改图片格式为png
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Object verifyCode = session.getAttribute("verify_code");
        if (Objects.isNull(verifyCode)) {
            req.setAttribute("message", "验证码错误，请重新刷新页面！");
            req.getRequestDispatcher( req.getContextPath() + "/login5.jsp").forward(req, resp);
        } else {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String verify = req.getParameter("verify");
            // 获取用户信息
            UserBean user = null;
            try {
                UserDAO userDAO = new UserDAO();
                user = userDAO.getUserByUsername(username);
            } catch (Exception e) {
                req.setAttribute("message", "系统异常！");
                req.getRequestDispatcher("/login5.jsp").forward(req, resp);
                throw new RuntimeException(e);
            }
            if (!verifyCode.toString().equals(verify)) {
                req.setAttribute("message", "验证码错误！");
                req.getRequestDispatcher("/login5.jsp").forward(req, resp);
            } else if (Objects.isNull(user)) {
                req.setAttribute("message", "用户名错误！");
                req.getRequestDispatcher("/login5.jsp").forward(req, resp);
            } else if (!user.getUserPwd().equals(password)) {
                req.setAttribute("message", "密码错误！");
                req.getRequestDispatcher("/login5.jsp").forward(req, resp);
            } else {
                session.setAttribute("login_user", username);
                resp.sendRedirect(req.getContextPath() + "/week5.jsp");
            }
        }
    }
}
