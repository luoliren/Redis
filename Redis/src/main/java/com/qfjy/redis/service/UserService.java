package com.qfjy.redis.service;

import com.qfjy.redis.bean.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * string 存和取测试
     */
    public String getString(String key);

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public User login(String username, String password);

    /**登陆不成功
     * 登录前的限制检查
     * 给用户详细的信息提示
     */
    public String loginValdate(String username);

    /**
     * 判断当前的登录用户是否被限制登陆
     * @param username
     * @return
     */
    public Map<String,Object> loginUserLock(String username);

    /**
     * Hash测试添加
     * @param u
     */
    public void add(User u);

    /**
     * Hash模拟单个对象操作查询
     * 根据用户逐渐ID查询对象
     * @param id
     * @return
     */
    public User selectById(int id);

    /**
     * list 类型的案例操作
     */
    public void listAdd();
    public List listRange(int pageNum, int pageSize);

    /**
     * list订单流程实例
     * 1.付款完成后，会根据用户的收货地址和商家的发货地址生成一个队列（任务）
     */
    public void listQueueInit(String cardId);
    public void listQueueTouch(String cardId);
    public List<String> listQueueSucc(String cardId);
    public List<String>listQueueWait(String cardId);
}
