package com.ljm.config;

import com.ljm.service.UserService;
import com.ljm.util.TokenUtil;
import com.ljm.vo.Res;
import com.ljm.vo.ResCode;
import com.ljm.vo.TokenState;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserService userService;
    /**
     * 预处理
     * 在业务处理器处理请求之前被调用。可以进行编码、安全控制、权限校验等处理；
     * @param
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler)throws Exception{
        boolean result = false;
        //进行跨域请求的时候，并且请求头中有额外参数，比如token，客户端会先发送一个OPTIONS请求,来探测后续需要发起的跨域POST请求是否安全可接受
        //不进行跨域拦截
        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        //获取请求头中的token
        String token = request.getHeader("Authorization");
        //校验token (token为空通过validToken校验结果为无效)
        Map<String, Object> checkResult = TokenUtil.validToken(token);
        //token检验后的结果状态
        String state = checkResult.get("state").toString();
        //token中解析出的数据 , 没有token时，获取不到data
        String userId = "";
        if(state.equals(TokenState.VALID.toString())){
            JSONObject data = (JSONObject) checkResult.get("data");
            userId =  data.get("userId").toString();
        }

        //封装返回对象
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        if(state.equals(TokenState.INVALID.toString())){
            //token合法性校验失败,原因：token不存在或者token格式有误
            log.info("userId:" + userId + "====== token不存在或者token格式有误 =====");
            response.getWriter().append(Res.failed(ResCode.TOKEN_ERROR_INVALID).toString());
        }else if(state.equals(TokenState.EXPIRED.toString())){
            //token合法性校验失败，原因：token过期
            log.info("userId:" + userId + "============= token已经过期 ============");
            response.getWriter().append(Res.failed(ResCode.TOKEN_ERROR_EXPIRE).toString());
        }else{
            //token合法性校验成功，还需要判断token是否存在（用户是否离线）
            String redisToken = "";
            //redis中token已被删除(redis中key过期自动删除 | 用户下线)，或者当前用户对应token已被修改（其他地方登录,被迫下线）
            if(redisToken == null || !redisToken.equals(token)){
                //用户已经下线，token被清除
                response.getWriter().append(Res.failed(ResCode.TOKEN_ERROR_EXIT).toString());
                log.info("userId:" + userId + "============= 已经离线，token已被清理 ============");
            }else{
                //用户在线
                result = true;
            }
        }
        return result;
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
