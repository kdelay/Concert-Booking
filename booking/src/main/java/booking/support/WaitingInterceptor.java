package booking.support;

import booking.api.waiting.domain.WaitingService;
import booking.support.exception.CustomNotFoundException;
import booking.support.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitingInterceptor implements HandlerInterceptor {

    private final WaitingService waitingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //시작 시간 로깅
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        String header = request.getHeader("Authorization");
        if (header == null) throw new CustomNotFoundException(ErrorCode.WAITING_TOKEN_IS_NOT_FOUND, "토큰이 존재하지 않습니다.");

        //헤더에서 토큰을 추출하여 요청 속성으로 저장
        String token = extractToken(header);
        request.setAttribute("token", token);

        //처리열에 있는 토큰 정보인지 검증
        waitingService.checkActiveQueue(token);

        //핸들러 접근 가능
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private String extractToken(String header) {
        if (header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //Handler 동작 시간 로깅
        long midTime = System.currentTimeMillis();
        request.setAttribute("midTime", midTime);

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
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

        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
