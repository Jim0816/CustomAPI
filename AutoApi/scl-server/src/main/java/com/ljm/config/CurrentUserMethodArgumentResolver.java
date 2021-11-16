package com.ljm.config;
import com.ljm.entity.User;
import com.ljm.service.UserService;
import com.ljm.util.TokenUtil;
import com.ljm.vo.AccessUser;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @CurrentUser 注解 解析器
 */
@Slf4j
@Component
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserService userService;

    //此方法用来判断本次请求的接口是否需要解析参数，如果需要返回true，然后调用下面的resolveArgument方法
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //如果接口的参数有User，那么需要解析参数
        Class<?> clazz = parameter.getParameterType();
        return clazz == AccessUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        //获取请求头中的token
        String token = request.getHeader("Authorization");
        //校验token (token为空通过validToken校验结果为无效)
        Map<String, Object> checkResult = TokenUtil.validToken(token);
        JSONObject data = (JSONObject) checkResult.get("data");
        String uid =  data.get("uid").toString();
        User queryUser = new User();
        queryUser.setId(uid);
        User user = userService.get(queryUser);
        AccessUser accessUser = new AccessUser();
        return  accessUser.transform(user);
    }
}

