package com.qfjy.redis.impl;

import com.qfjy.redis.bean.User;
import com.qfjy.redis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    RedisTemplate<String,String> redisTemplate;

    /**Redis String 类型测试
     *通过某个key得到值
     * 如果key在redis中不存在，到数据库中查询
     * 如果存在就在redis中查询
     * @param key
     * @return
     */
    @Override
    public String getString(String key) {
        ValueOperations<String, String> string = redisTemplate.opsForValue();
       // redisTemplate.expire("java1802",2, TimeUnit.HOURS);
        redisTemplate.opsForValue().set("java1803","test",2,TimeUnit.HOURS);
        if (redisTemplate.hasKey(key)){
            //Redis中取出返回值
            System.out.println("在Redis中取出并返回");
           return string.get(key);
        } else {
            //查询数据库
            String result = "RedisTemplate模板练习";
            string.set(key,result);
            System.out.println("在mysql中取出并返回");
            return result;
        }
    }

    //#####################################33
    //存储往Redis中存储Hash数据
    public void add(User u) {
        redisTemplate.opsForHash().put("user",u.getId(),u);
    }

    /**
     * 根据用户主键ID查询对象
     * @param id
     * @return
     */
    public User       selectById(int id){
        //1.先判断Redis中是否存在该Key，如果存在，从redis中取，并返回即可
        Boolean b = redisTemplate.opsForHash().hasKey("user", id);
        if (b){
            User user = (User) redisTemplate.opsForHash().get("user", id);
            System.out.println("--Redis中查询出对象");
            return user;
        } else {
            //如果不存在，从数据库中查询，取出付给Redis，并返回
            //如果不存在，从数据库中查询，取出付给Redis，并返回
            User user = new User();
            user.setId(id);
            user.setAge(22);
            user.setName("正平平");
            user.setPassword("123");
            user.setUsername("admin");
            redisTemplate.opsForHash().put("user",user.getId(),user);
            System.out.println("Mysql中查询的");
            return user;
        }
    }

    /**
     * list类型
     */
    @Override
    public void listAdd() {
        String key = "news:top10";
        /*redisTemplate.opsForList().leftPush("news:top10","i撒谎丢少ijd");
        redisTemplate.opsForList().leftPush("news:top10","aaa");
        redisTemplate.opsForList().leftPushAll("news:top10","bbb","weas");*/

        redisTemplate.opsForList().rightPushAll(key,"1","2","3","4asds","5sdsa","6","7","8");

    }

    @Override
    public List<String> listRange(int pageNum, int pageSize) {
        String key = "news:top10";
        /**
         * start: (pageNum-1)*pageSize
         * stop:pageSize*pageNum-1
         */
        int start = (pageNum-1)*pageSize;
        int stop = pageSize*pageNum-1;
        List<String> range = redisTemplate.opsForList().range(key, start, stop);
        Long count = redisTemplate.opsForList().size(key);
        System.out.println("总记录数："+count);
        return range;

    }

    @Override
    public void listQueueInit(String cardId) {
        String key = "prod"+cardId+":init";//初始化的key 带有任务的要完成
        redisTemplate.opsForList().leftPushAll(key,"1商家出货","2小哥发件","3从北京某小区到机场","4机场到南京机场","机场",
                   "5机场到建业","6.到本人");
    }
    //2.触发事件
    public void listQueueTouch(String cardId){
        String key = "prod"+cardId+":succ";//代表已完成的队列
        redisTemplate.opsForList().rightPopAndLeftPush("prod"+cardId+":init",key);

    }
    //3查询：客户查询：我的快递到哪了

    public List<String> listQueueSucc(String cardId){
        String key = "prod"+cardId+":succ";//代表已完成的队列
        List<String> range = redisTemplate.opsForList().range(key, 0, -1);
        return range;
    }
    //4.物流查询：当前快递还有多少任务没有完成
    public List<String>listQueueWait(String cardId){
        String key = "prod"+cardId+":init";//初始化的key 带有任务的要完成
        return redisTemplate.opsForList().range(key,0,-1);
    }

    /**
     *  判断当前登录的用户是否被限制登录
     *  查询当前key是否存在，如果存在，就被限制 注意：需要给用户提示：您当前的用户已被限制，还剩多少时间
     *  如果不存在，就不被限制
     * @param username
     * @return
     */
    @Override
    public Map<String,Object> loginUserLock(String username) {
        String key = User.getLoginTimeLockKey(username);
        Map<String,Object> map = new HashMap<String,Object>();
      if(redisTemplate.hasKey(User.getLoginTimeLockKey(username))){
          Long lockTime = redisTemplate.getExpire(key, TimeUnit.MINUTES);//以分钟为单位进行返回
//如果存在
          map.put("flag",true);
          map.put("lockTime",lockTime);//还剩多长时间（小时为单位锁定，给用户返回分钟）
      }else {
          map.put("flag",false);
      }
        return map;
    }

    @Override
    public User login(String username, String password) {

        return null;
    }

    /**
     * 登陆不成功的相应操作
     * @param username
     * @param user
     * @return
     */
    @Override
    public String loginValdate(String username) {
    //记录登录错误次数的key
        String key = User.getLoginCountFailKey(username);
        int num = 5;//登录错误的次数
        if (!redisTemplate.hasKey(key)){
            //如果不存在
            //是第一次登录失败次数为1复制为1并设置失效期2分钟 user:loginCount:fail:用户名进行赋值，同时设置失效期
            //注意redisTemplate中赋值是不能直接赋值并设置失效期（会设置失败）
            redisTemplate.opsForValue().set(key,"1");//先赋值
            redisTemplate.expire(key,2,TimeUnit.MINUTES);//在设置是失效期2分钟
            return "登陆失败，在2分钟内还允许输入错误"+(num-1)+"次";
        }else {
            //如果存在
            //查询登陆失败的次数的key的结果
            long loginFailCount =Long.parseLong(redisTemplate.opsForValue().get(key));
            if (loginFailCount<(num-1)){
                //代表如果当前登录失败次数<4,还有资格进行登录
                //user:loginCount:fail:+1登录次数加1
                redisTemplate.opsForValue().increment(key,1);//对指定key，增加指定数据
                Long seconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);//返回的是秒
                return "登陆失败，在"+seconds+"秒内还允许输入错误"+(num-loginFailCount-1)+"次";
            }else {
                //超过了指定的登录次数
                //限制登录key存在，同时设置限制登录事件锁定1小时
                redisTemplate.opsForValue().set(User.getLoginTimeLockKey(username),"-1");
                redisTemplate.expire(User.getLoginTimeLockKey(username),1,TimeUnit.HOURS);
                return "因登陆失败次数超过限制"+num+"次，已经对其进行限制登录1小时";
            }
        }
    }


}
