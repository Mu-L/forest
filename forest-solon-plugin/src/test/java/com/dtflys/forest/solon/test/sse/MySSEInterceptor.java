package com.dtflys.forest.solon.test.sse;

import com.dtflys.forest.annotation.SSEDataMessage;
import com.dtflys.forest.annotation.SSEName;
import com.dtflys.forest.annotation.SSEValue;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.SSEInterceptor;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.io.InputStream;

@Component
public class MySSEInterceptor implements SSEInterceptor {
    
    @Inject
    private TestComp testComp;

    @Override
    public void onSuccess(InputStream data, ForestRequest request, ForestResponse response) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("onSuccess\n");
    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("afterExecute\n");
    }
    
    @SSEDataMessage
    public void onData(ForestRequest request, @SSEName String name, @SSEValue String value) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("Receive name=" + name + "; value=" + value + "; comp=" + testComp.getValue() + "\n");
    }
}
