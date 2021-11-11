package com.ljm.service;

import com.ljm.entity.Token;
import com.ljm.entity.User;
import com.ljm.vo.Res;
import com.ljm.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {
    /**
     * 直接插入 (插入前必须满足表的元数据信息已经存在   表和元数据绑定一致，表被删了，对应元数据也必须清空)
     * @param
     * @return
     * @author Jim
     */
    boolean add(User user);

    /**
     * 登录验证
     * @param
     * @return
     * @author Jim
     */
    Res login(User user);

    /**
     * 用户离线
     * @param
     * @return
     * @author Jim
     */
    boolean logout(String token, User user);

    /**
     * 创建token
     * @param
     * @return
     * @author Jim
     */
    String createToken(String userId);

    /**
     * 保存Token对象
     * @param
     * @return
     * @author Jim
     */
    boolean saveToken(Token token);

    /**
     * 获取Token对象
     * @param
     * @return
     * @author Jim
     */
    Token getToken(Token token);

    /**
     * 修改Token对象
     * @param
     * @return
     * @author Jim
     */
    boolean updateToken(Token token, Set<String> filterFields, Set<String> updateFields);

    /**
     * 删除Token对象
     * @param
     * @return
     * @author Jim
     */
    boolean removeToken(Token token);

    User get(User user);

    /**
     * 通过token查询用户信息
     * @param
     * @return
     * @author Jim
     */
    User get(String token);

    List<User> list(User user);

    /**
     * 获取当前访问接口的用户信息
     * @param request
     * @return
     * @author Jim
     */
    User getAccessUser(HttpServletRequest request);


    /**
     * 获取当前访问接口的用户所有准备信息
     * @param user
     * @return
     * @author Jim
     */
    UserVO getAllUserInfo(User user);

    /**
     * 获取所有用户基本信息
     * @param
     * @return
     * @author Jim
     */
    List<UserVO> getAllUserInfoList();


}
