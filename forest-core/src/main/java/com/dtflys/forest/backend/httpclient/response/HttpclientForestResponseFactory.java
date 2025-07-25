package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.http.Res;
import com.dtflys.forest.http.UnclosedResponse;
import com.dtflys.forest.utils.ReflectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 17:07
 */
public class HttpclientForestResponseFactory implements ForestResponseFactory<HttpResponse> {

    private volatile ForestResponse<?> resultResponse;

    @Override
    public synchronized ForestResponse<?> createResponse(ForestRequest<?> request, HttpResponse httpResponse, LifeCycleHandler lifeCycleHandler, Throwable exception, Date requestTime) {
        if (resultResponse != null) {
            return resultResponse;
        }
        HttpEntity entity = null;
        if (httpResponse != null) {
            entity = httpResponse.getEntity();
            if (entity != null) {
                entity = new HttpclientEntity(request, entity, lifeCycleHandler);
            }
        }
        final HttpclientForestResponse response = isUnclosedResponse(request, lifeCycleHandler) ?
                new HttpclientUnclosedResponse(request, httpResponse, entity, requestTime, new Date()) :
                new HttpclientForestResponse(request, httpResponse, entity, requestTime, new Date());
        this.resultResponse = response;
        response.setException(exception);
        return response;
    }
    

}
