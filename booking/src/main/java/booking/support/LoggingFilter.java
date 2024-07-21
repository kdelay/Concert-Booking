package booking.support;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //캐시 가능하도록 래핑
        ContentCachingRequestWrapper httpServletRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper httpServletResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        //FilterChain 전, 후 필터 적용
        chain.doFilter(httpServletRequest, httpServletResponse);

        //request 요청 - uri 조회
        String uri = httpServletRequest.getRequestURI();

        //request 내용 확인
        String reqContent = new String(httpServletRequest.getContentAsByteArray());
        log.info("### Request(Filter) uri : {}, request : {}", uri, reqContent);

        // response - status, content
        int httpStatus = httpServletResponse.getStatus();
        String resContent = new String(httpServletResponse.getContentAsByteArray());

        //클라이언트 조회용 response 복사
        httpServletResponse.copyBodyToResponse();

        log.info("### Response(Filter) status: {}, response {}", httpStatus, resContent);
    }

}
