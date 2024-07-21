package booking.support;

import booking.support.exception.CustomBadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import static booking.support.exception.ErrorCode.WAITING_TOKEN_AUTH_FAIL;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //시작 시간 로깅
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        //Handler 종류 확인
        if (handler instanceof ResourceHttpRequestHandler) return true;

        //형 변환
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //@Authorization 정보 조회
        Authorization authorization = handlerMethod.getMethodAnnotation(Authorization.class);

        //토큰 정보가 없는 경우 에러 반환
        if (authorization == null) {
            authorization = handlerMethod.getBeanType().getAnnotation(Authorization.class);
        }

        if (authorization != null) {
            String header = request.getHeader("Authorization");
            String uri = request.getRequestURI();

            //헤더가 없을 시 토큰을 발급해야 한다.
            if (header == null) {
                if (!uri.equals("/waiting/token")) {
                    log.warn("Authorization invalid header = {}", header);
                    throw new CustomBadRequestException(WAITING_TOKEN_AUTH_FAIL, "토큰 인증 실패");
                }
            }
            log.info("Authorization valid header = {}", header);
        }
        //핸들러 접근 가능
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //Handler 동작 시간 로깅
        long midTime = System.currentTimeMillis();
        request.setAttribute("midTime", midTime);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //전체 동작 시간 로깅
        Long startTime = (Long)request.getAttribute("startTime");
        Long midTime = (Long)request.getAttribute("midTime");
        long endTime = System.currentTimeMillis();

        if(midTime != null) {
            log.info("Handler 동작 시간 : {} (ms)", endTime - midTime);
        }
        log.info("전체 동작 시간 : {} (ms)", endTime - startTime);
    }
}
