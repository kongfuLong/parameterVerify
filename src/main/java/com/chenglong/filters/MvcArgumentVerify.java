package com.chenglong.filters;

import com.chenglong.annotations.ParameterValid;
import com.chenglong.core.ParamterVerfy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Parameter;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/21
 * @since 1.0
 */
@Service
public class MvcArgumentVerify implements HandlerMethodArgumentResolver {

    @Autowired
    private ParamterVerfy paramterVerfy;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
       Parameter[] parameters = methodParameter.getMethod().getParameters();
        for(Parameter parameter : parameters){
            ParameterValid valid = parameter.getAnnotation(ParameterValid.class);
            if(valid!=null){
                paramterVerfy.verifyParameter(parameter);
            }
        }
        return null;
    }
}
