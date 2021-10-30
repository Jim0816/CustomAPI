package com.ljm.config;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.User;
import com.ljm.util.TokenUtil;
import com.ljm.util.TokenUtilTest;
import com.ljm.vo.Res;
import com.ljm.vo.ResCode;
import com.ljm.vo.TokenState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 预处理
     * 在业务处理器处理请求之前被调用。可以进行编码、安全控制、权限校验等处理；
     * @param
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler)throws Exception{
        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        //获取请求头中的token
        String token = request.getHeader("token");
        //校验token
        Map<String, Object> checkResult = TokenUtil.validToken(token);
        String state = checkResult.get("state").toString();
        //封装返回对象
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        if(state.equals(TokenState.INVALID.toString()) || state.equals(TokenState.INVALID.toString())){
            response.getWriter().append(Res.failed(ResCode.TOKEN_ERROR).toString());
            log.info(state + ": token认证失败，未通过拦截器");
            return false;
        }
        //token合法性校验成功，还需要判断用户是否登出，登出则需要清理token
        User user = (User) redisTemplate.opsForValue().get("user:" + token);
        if(user == null){
            response.getWriter().append(Res.failed(ResCode.TOKEN_ERROR).toString());
            log.info("用户已经离线，token失效");
            return false;
        }
        return true;
    }

    /**
     * 后处理
     * 在业务处理器处理请求执行完成后，生成视图之前执行。
     * @param
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {}

    /**
     * 完成请求，已经返回给前端
     * 在DispatcherServlet完全处理完请求后被调用，可用于清理资源等。返回处理
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception { }

}
