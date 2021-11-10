package com.ljm.config;

import com.ljm.entity.Token;
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
        String uid = "";
        try{
            JSONObject data = (JSONObject) checkResult.get("data");
            uid =  data.get("uid").toString();
        }catch (Exception e){
            log.info("token中解析数据失败!");
        }

        Token queryToken = new Token();
        queryToken.setUid(uid);
        //封装返回对象
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        if(state.equals(TokenState.INVALID.toString())){
            //token合法性校验失败,原因：token不存在或者token格式有误
            log.info("uid:" + uid + "====== token不存在或者token格式有误 =====");
            response.getWriter().append(Res.failed(ResCode.TOKEN_ERROR_INVALID).toString());
        }else if(state.equals(TokenState.EXPIRED.toString())){
            //token合法性校验失败，原因：token过期
            log.info("uid:" + uid + "============= token已经过期,准备清除本地token数据 ============");
            //已经知道token过期,需要立即清空本地数据库中token
            userService.removeToken(queryToken);
            response.getWriter().append(Res.failed(ResCode.TOKEN_ERROR_EXPIRE).toString());
        }else{
            //token合法性校验成功, 还需要判断token是否被过期删除
            Token dbToken = userService.getToken(queryToken);
            if(dbToken != null){
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
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 完成请求，已经返回给前端
     * 在DispatcherServlet完全处理完请求后被调用，可用于清理资源等。返回处理
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception { }

}
